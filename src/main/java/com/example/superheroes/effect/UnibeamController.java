package com.example.superheroes.effect;

import com.example.superheroes.attachment.ModAttachments;
import com.example.superheroes.network.ModNetworking;
import com.example.superheroes.particle.ModParticles;
import com.example.superheroes.transform.HeroData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UnibeamController {
	public static final int CHARGE_TICKS = 200;
	public static final int STUN_TICKS = 200;
	public static final double PULL_RADIUS = 50.0;
	public static final double DEBUFF_RADIUS = 100.0;
	public static final double BEAM_RANGE = 30.0;
	public static final int CRATER_LENGTH = 30;
	public static final int CRATER_HALF_WIDTH = 4;
	private static final float DIRECT_HIT_DAMAGE = 80f;

	private static final Holder<MobEffect>[] CHARGE_DEBUFFS = effects(
			MobEffects.MOVEMENT_SLOWDOWN,
			MobEffects.WEAKNESS,
			MobEffects.DIG_SLOWDOWN,
			MobEffects.BLINDNESS,
			MobEffects.DARKNESS
	);
	private static final Holder<MobEffect>[] AOE_DEBUFFS = effects(
			MobEffects.MOVEMENT_SLOWDOWN,
			MobEffects.WEAKNESS,
			MobEffects.BLINDNESS,
			MobEffects.DIG_SLOWDOWN,
			MobEffects.HUNGER,
			MobEffects.CONFUSION,
			MobEffects.POISON,
			MobEffects.WITHER,
			MobEffects.DARKNESS
	);

	private static final Map<UUID, ChargeState> charging = new HashMap<>();
	private static final Map<UUID, StunState> stunned = new HashMap<>();

	private UnibeamController() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickCharging(player);
				tickStunned(player);
			}
			charging.keySet().removeIf(uuid -> server.getPlayerList().getPlayer(uuid) == null);
			stunned.keySet().removeIf(uuid -> server.getPlayerList().getPlayer(uuid) == null);
		});
	}

	public static boolean startCharge(ServerPlayer player) {
		UUID id = player.getUUID();
		if (charging.containsKey(id) || stunned.containsKey(id)) {
			return false;
		}
		charging.put(id, new ChargeState(0));
		ServerLevel level = player.serverLevel();
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.6f, 0.5f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.4f, 0.6f);
		return true;
	}

	public static boolean isBusy(ServerPlayer player) {
		UUID id = player.getUUID();
		return charging.containsKey(id) || stunned.containsKey(id);
	}

	public static boolean isCharging(ServerPlayer player) {
		return charging.containsKey(player.getUUID());
	}

	public static boolean isStunned(ServerPlayer player) {
		return stunned.containsKey(player.getUUID());
	}

	private static void tickCharging(ServerPlayer player) {
		ChargeState state = charging.get(player.getUUID());
		if (state == null) {
			return;
		}
		ServerLevel level = player.serverLevel();
		int t = state.progress;
		float p = (float) t / CHARGE_TICKS;
		Vec3 chest = player.position().add(0, player.getBbHeight() * 0.55, 0);

		int rays = Math.max(6, Math.round(8 + 24 * p));
		for (int i = 0; i < rays; i++) {
			double a = level.getRandom().nextDouble() * Math.PI * 2.0;
			double dist = 1.5 + level.getRandom().nextDouble() * (2.0 + 4.0 * p);
			double dy = (level.getRandom().nextDouble() - 0.5) * 2.0;
			double sx = chest.x + Math.cos(a) * dist;
			double sy = chest.y + dy;
			double sz = chest.z + Math.sin(a) * dist;
			level.sendParticles(ModParticles.UNIBEAM_SPARK,
					sx, sy, sz, 1,
					(chest.x - sx) * 0.4, (chest.y - sy) * 0.4, (chest.z - sz) * 0.4, 0.0);
		}
		level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
				chest.x, chest.y, chest.z,
				Math.round(2 + 8 * p), 0.4, 0.4, 0.4, 0.02);
		level.sendParticles(ParticleTypes.GLOW,
				chest.x, chest.y, chest.z,
				Math.round(1 + 6 * p), 0.6, 0.6, 0.6, 0.05);

		if (t % Math.max(2, 18 - (int) (16 * p)) == 0) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS,
					0.6f + 0.6f * p, 0.6f + 1.4f * p);
		}
		if (t == 60 || t == 120 || t == 170) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.6f, 0.7f + 0.5f * p);
		}

		AABB pullBox = player.getBoundingBox().inflate(PULL_RADIUS);
		List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, pullBox,
				e -> e != player && e.isAlive() && !e.isSpectator());
		for (LivingEntity target : nearby) {
			Vec3 toPlayer = chest.subtract(target.position().add(0, target.getBbHeight() * 0.5, 0));
			double dist = toPlayer.length();
			if (dist < 0.5 || dist > PULL_RADIUS) {
				continue;
			}
			Vec3 pull = toPlayer.normalize().scale(0.18 + 0.5 * p);
			Vec3 dm = target.getDeltaMovement();
			target.setDeltaMovement(dm.x * 0.4 + pull.x, dm.y * 0.5 + pull.y * 0.6, dm.z * 0.4 + pull.z);
			target.hurtMarked = true;
			target.fallDistance = 0f;
			if (t % 20 == 0) {
				int duration = 60;
				int amplifier = Math.round(2 + 4 * p);
				for (Holder<MobEffect> effect : CHARGE_DEBUFFS) {
					target.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
				}
			}
		}

		state.progress = t + 1;
		if (state.progress >= CHARGE_TICKS) {
			fireBeam(player);
			charging.remove(player.getUUID());
			stunned.put(player.getUUID(), new StunState(0));
		}
	}

	private static void tickStunned(ServerPlayer player) {
		StunState state = stunned.get(player.getUUID());
		if (state == null) {
			return;
		}
		ServerLevel level = player.serverLevel();
		Vec3 dm = player.getDeltaMovement();
		player.setDeltaMovement(0, Math.min(dm.y, 0), 0);
		player.hurtMarked = true;
		if (state.progress % 10 == 0) {
			level.sendParticles(ParticleTypes.SMOKE,
					player.getX(), player.getY() + 1.0, player.getZ(),
					6, 0.3, 0.6, 0.3, 0.02);
			level.sendParticles(ParticleTypes.LARGE_SMOKE,
					player.getX(), player.getY() + 1.2, player.getZ(),
					3, 0.3, 0.4, 0.3, 0.01);
		}
		if (state.progress == 0) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.4f, 0.6f);
		}
		state.progress++;
		if (state.progress >= STUN_TICKS) {
			stunned.remove(player.getUUID());
		}
	}

	private static void fireBeam(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		Vec3 origin = player.getEyePosition();
		Vec3 dir = player.getViewVector(1f);
		Vec3 end = origin.add(dir.scale(BEAM_RANGE));

		LivingEntity directHit = pickDirectHit(player, level, origin, dir);
		if (directHit != null) {
			directHit.hurt(level.damageSources().playerAttack(player), DIRECT_HIT_DAMAGE);
			applyDebuffs(directHit, AOE_DEBUFFS, 240, 1, true);
		}

		AABB aoeBox = player.getBoundingBox().inflate(DEBUFF_RADIUS);
		List<LivingEntity> aoeTargets = level.getEntitiesOfClass(LivingEntity.class, aoeBox,
				e -> e != player && e.isAlive() && !e.isSpectator() && e != directHit);
		for (LivingEntity target : aoeTargets) {
			applyDebuffs(target, AOE_DEBUFFS, 120, 0, false);
		}

		carveCrater(level, player, origin, dir);
		drainEnergy(player);

		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 4.0f, 0.5f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 0.7f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.4f, 1.4f);

		Vec3 chest = player.position().add(0, player.getBbHeight() * 0.55, 0);
		for (int i = 0; i < 80; i++) {
			double f = level.getRandom().nextDouble();
			Vec3 p = chest.add(dir.scale(f * BEAM_RANGE));
			level.sendParticles(ModParticles.UNIBEAM_SPARK,
					p.x, p.y, p.z, 4,
					0.3, 0.3, 0.3,
					0.05 + 0.3 * f);
		}
		level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
				end.x, end.y, end.z, 1, 0.0, 0.0, 0.0, 0.0);
	}

	private static LivingEntity pickDirectHit(ServerPlayer player, ServerLevel level, Vec3 origin, Vec3 dir) {
		Vec3 end = origin.add(dir.scale(BEAM_RANGE));
		BlockHitResult bh = level.clip(new ClipContext(origin, end,
				ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		Vec3 effectiveEnd = bh.getType() == HitResult.Type.BLOCK ? bh.getLocation() : end;
		AABB box = player.getBoundingBox().expandTowards(dir.scale(BEAM_RANGE)).inflate(2.5);
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, origin, effectiveEnd, box,
				e -> e instanceof LivingEntity && e.isAlive() && e != player && !e.isSpectator());
		return hit != null ? (LivingEntity) hit.getEntity() : null;
	}

	private static void applyDebuffs(LivingEntity target, Holder<MobEffect>[] effects, int baseDuration, int baseAmplifier, boolean direct) {
		int duration = direct ? baseDuration * 2 : baseDuration;
		int amplifier = direct ? Math.min(baseAmplifier * 2 + 1, 4) : baseAmplifier;
		for (Holder<MobEffect> effect : effects) {
			target.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true, true));
		}
	}

	private static void carveCrater(ServerLevel level, ServerPlayer player, Vec3 origin, Vec3 dir) {
		RandomSource rand = level.getRandom();
		Vec3 forward = dir.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : dir.normalize();
		Vec3 forwardH = new Vec3(forward.x, 0, forward.z);
		if (forwardH.lengthSqr() < 1e-6) {
			forwardH = new Vec3(1, 0, 0);
		} else {
			forwardH = forwardH.normalize();
		}
		for (int step = 1; step <= CRATER_LENGTH; step++) {
			Vec3 center = origin.add(forward.scale(step));
			BlockPos centerPos = BlockPos.containing(center);
			float spread = 0.3f + (step / (float) CRATER_LENGTH) * 0.7f;
			int rays = (int) (CRATER_HALF_WIDTH * 2 * spread + 4);
			for (int i = 0; i < rays * 6; i++) {
				int dx = rand.nextInt(CRATER_HALF_WIDTH * 2 + 1) - CRATER_HALF_WIDTH;
				int dy = rand.nextInt(CRATER_HALF_WIDTH * 2 + 1) - CRATER_HALF_WIDTH;
				int dz = rand.nextInt(CRATER_HALF_WIDTH * 2 + 1) - CRATER_HALF_WIDTH;
				int distSq = dx * dx + dy * dy + dz * dz;
				int maxDistSq = CRATER_HALF_WIDTH * CRATER_HALF_WIDTH;
				if (distSq > maxDistSq) {
					continue;
				}
				if (rand.nextFloat() < 0.35f * (distSq / (float) maxDistSq)) {
					continue;
				}
				BlockPos pos = centerPos.offset(dx, dy, dz);
				BlockState state = level.getBlockState(pos);
				if (state.isAir() || state.liquid()) {
					continue;
				}
				float hardness = state.getDestroySpeed(level, pos);
				if (hardness < 0f || hardness >= 50f) {
					continue;
				}
				level.destroyBlock(pos, false, player);
			}
			if (step % 3 == 0) {
				level.explode(player, center.x, center.y, center.z,
						3.0f + spread * 2.0f, true, Level.ExplosionInteraction.MOB);
			}
			placeFireRing(level, center, 2 + (int) (spread * 2));
			level.sendParticles(ParticleTypes.LAVA,
					center.x, center.y, center.z, 6, 1.0, 0.6, 1.0, 0.0);
			level.sendParticles(ParticleTypes.FLAME,
					center.x, center.y, center.z, 16, 1.4, 0.8, 1.4, 0.05);
		}
	}

	private static void placeFireRing(ServerLevel level, Vec3 center, int radius) {
		BlockPos centerPos = BlockPos.containing(center);
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (dx * dx + dz * dz > radius * radius) {
					continue;
				}
				for (int dy = -1; dy <= 2; dy++) {
					BlockPos pos = centerPos.offset(dx, dy, dz);
					BlockState state = level.getBlockState(pos);
					if (!state.isAir()) {
						continue;
					}
					BlockPos below = pos.below();
					if (BaseFireBlock.canBePlacedAt(level, pos, net.minecraft.core.Direction.UP)
							&& !level.getBlockState(below).isAir()
							&& level.getRandom().nextFloat() < 0.55f) {
						level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
					}
				}
			}
		}
	}

	private static void drainEnergy(ServerPlayer player) {
		HeroData data = player.getAttachedOrCreate(ModAttachments.HERO_DATA);
		if (!data.hasHero()) {
			return;
		}
		HeroData updated = data.withResources(0f, 0f);
		player.setAttached(ModAttachments.HERO_DATA, updated);
		ModNetworking.syncResources(player, updated);
	}

	@SafeVarargs
	private static Holder<MobEffect>[] effects(Holder<MobEffect>... entries) {
		return entries;
	}

	private static final class ChargeState {
		int progress;

		ChargeState(int progress) {
			this.progress = progress;
		}
	}

	private static final class StunState {
		int progress;

		StunState(int progress) {
			this.progress = progress;
		}
	}
}
