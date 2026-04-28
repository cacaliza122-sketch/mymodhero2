package com.example.superheroes.effect;

import com.example.superheroes.attachment.ModAttachments;
import com.example.superheroes.hero.IronManHero;
import com.example.superheroes.network.ModNetworking;
import com.example.superheroes.transform.HeroData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

public final class IronManEnergyBalancer {
	private static final float SOFT_CAP = 500f;
	private static final float OVERFLOW_DRAIN_PER_TICK = 5f;

	private IronManEnergyBalancer() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tick(player);
			}
		});
	}

	private static void tick(ServerPlayer player) {
		HeroData data = player.getAttachedOrCreate(ModAttachments.HERO_DATA);
		if (!data.hasHero()) {
			return;
		}
		if (!IronManHero.ID.equals(data.heroId())) {
			return;
		}
		float energy = data.energy();
		if (energy <= SOFT_CAP) {
			return;
		}
		float newEnergy = Math.max(SOFT_CAP, energy - OVERFLOW_DRAIN_PER_TICK);
		HeroData updated = data.withResources(newEnergy, data.mana());
		player.setAttached(ModAttachments.HERO_DATA, updated);
		ModNetworking.syncResources(player, updated);
	}
}
