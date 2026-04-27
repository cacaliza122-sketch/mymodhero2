package com.example.superheroes.hero;

import com.example.superheroes.ModId;
import com.example.superheroes.ability.AbilityIds;
import com.example.superheroes.physics.ShockwaveUtil;
import com.example.superheroes.resource.ResourceKind;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class IronManHero implements Hero {
	public static final ResourceLocation ID = ModId.of("iron_man");
	public static final ResourceLocation SKIN = ModId.of("textures/entity/hero/ironman.png");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public float getEnergyMax() {
		return 120f;
	}

	@Override
	public float getEnergyRegenPerTick() {
		return 0.4f;
	}

	@Override
	public float getManaMax() {
		return 80f;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return switch (pose) {
			case CROUCHING -> EntityDimensions.scalable(0.6f, 1.5f).withEyeHeight(1.27f);
			case SWIMMING, FALL_FLYING, SPIN_ATTACK -> EntityDimensions.scalable(0.6f, 0.6f).withEyeHeight(0.4f);
			default -> EntityDimensions.scalable(0.6f, 1.8f).withEyeHeight(1.62f);
		};
	}

	@Override
	public List<ResourceLocation> getAbilities() {
		return List.of(AbilityIds.IRON_MAN_FLIGHT, AbilityIds.SUPERSONIC, AbilityIds.REPULSOR, AbilityIds.BOX_ESP);
	}

	@Override
	public ResourceKind getDefaultBinding(ResourceLocation abilityId) {
		if (abilityId.equals(AbilityIds.BOX_ESP)) {
			return ResourceKind.MANA;
		}
		return ResourceKind.ENERGY;
	}

	@Override
	public void applyPassives(Player player) {
		HeroAttributes.IRON_MAN.apply(player);
	}

	@Override
	public void removePassives(Player player) {
		HeroAttributes.IRON_MAN.remove(player);
	}

	@Override
	public boolean cancelsFallDamage(Player player) {
		return true;
	}

	@Override
	public ResourceLocation getSkinTexture() {
		return SKIN;
	}

	@Override
	public HeroTheme getTheme() {
		return HeroTheme.IRON_MAN;
	}

	@Override
	public void onLanded(ServerPlayer player, float fallDistance) {
		if (fallDistance < 3.0f) {
			return;
		}
		float scaled = Math.min(fallDistance, 60.0f);
		double radius = 2.0 + scaled * 0.25;
		float damage = 2.0f + scaled * 0.20f;
		ShockwaveUtil.detonate(player, player.position(), radius, damage, false);
		ServerLevel level = player.serverLevel();
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.IRON_GOLEM_DEATH, SoundSource.PLAYERS, 0.9f, 0.6f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.2f, 0.7f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.PLAYERS, 1.4f, 0.5f);
	}
}
