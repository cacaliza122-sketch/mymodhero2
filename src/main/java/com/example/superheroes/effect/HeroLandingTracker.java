package com.example.superheroes.effect;

import com.example.superheroes.ability.AbilityIds;
import com.example.superheroes.attachment.ModAttachments;
import com.example.superheroes.hero.Hero;
import com.example.superheroes.hero.Heroes;
import com.example.superheroes.transform.HeroData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class HeroLandingTracker {
	private static final float MIN_FALL_DISTANCE = 3.0f;
	private static final double TELEPORT_DETECT_DROP = 8.0;
	private static final long LANDING_COOLDOWN_MS = 250L;

	private static final Map<UUID, State> states = new HashMap<>();

	private HeroLandingTracker() {
	}

	private static final class State {
		double peakY;
		double prevY;
		boolean wasOnGround;
		boolean tracking;
		long lastLandingMs;
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			long now = System.currentTimeMillis();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickPlayer(player, now);
			}
			Iterator<Map.Entry<UUID, State>> it = states.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<UUID, State> e = it.next();
				if (server.getPlayerList().getPlayer(e.getKey()) == null) {
					it.remove();
				}
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			states.remove(handler.getPlayer().getUUID());
		});
	}

	public static void reset(ServerPlayer player) {
		states.remove(player.getUUID());
	}

	private static void tickPlayer(ServerPlayer player, long now) {
		HeroData data = player.getAttachedOrCreate(ModAttachments.HERO_DATA);
		if (!data.hasHero()) {
			states.remove(player.getUUID());
			return;
		}
		Hero hero = Heroes.get(data.heroId());
		if (hero == null) {
			states.remove(player.getUUID());
			return;
		}

		State s = states.computeIfAbsent(player.getUUID(), k -> {
			State ns = new State();
			ns.peakY = player.getY();
			ns.prevY = player.getY();
			ns.wasOnGround = player.onGround();
			ns.tracking = !player.onGround();
			ns.lastLandingMs = 0L;
			return ns;
		});

		double currentY = player.getY();
		boolean onGround = player.onGround();
		boolean flying = data.isActive(AbilityIds.FLIGHT)
				|| data.isActive(AbilityIds.IRON_MAN_FLIGHT)
				|| data.isActive(AbilityIds.SUPERSONIC);

		double drop = s.prevY - currentY;
		if (drop > TELEPORT_DETECT_DROP || -drop > TELEPORT_DETECT_DROP) {
			s.peakY = currentY;
			s.tracking = !onGround;
			s.wasOnGround = onGround;
			s.prevY = currentY;
			return;
		}

		if (flying) {
			s.peakY = currentY;
			s.tracking = !onGround;
			s.wasOnGround = onGround;
			s.prevY = currentY;
			return;
		}

		if (!onGround) {
			if (!s.tracking) {
				s.peakY = currentY;
				s.tracking = true;
			} else if (currentY > s.peakY) {
				s.peakY = currentY;
			}
		} else {
			if (!s.wasOnGround && s.tracking) {
				float fallDist = (float) Math.max(0.0, s.peakY - currentY);
				if (fallDist >= MIN_FALL_DISTANCE && now - s.lastLandingMs > LANDING_COOLDOWN_MS) {
					s.lastLandingMs = now;
					hero.onLanded(player, fallDist);
				}
				s.tracking = false;
				s.peakY = currentY;
			}
		}

		s.wasOnGround = onGround;
		s.prevY = currentY;
	}
}
