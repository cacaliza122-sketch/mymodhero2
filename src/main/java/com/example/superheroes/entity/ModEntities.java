package com.example.superheroes.entity;

import com.example.superheroes.ModId;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final EntityType<RegulusProjectileEntity> REGULUS_PROJECTILE = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ModId.of("regulus_projectile"),
			EntityType.Builder.<RegulusProjectileEntity>of(RegulusProjectileEntity::new, MobCategory.MISC)
					.sized(0.6f, 0.6f)
					.clientTrackingRange(8)
					.updateInterval(2)
					.build("regulus_projectile")
	);

	private ModEntities() {
	}

	public static void init() {
	}
}
