package com.example.superheroes.effect;

import com.example.superheroes.ModId;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ModEffects {
	public static final Holder<MobEffect> MADNESS = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT, ModId.of("madness"),
			new MadnessMobEffect(MobEffectCategory.HARMFUL, 0xFF1F2D)
	);

	public static final Holder<MobEffect> MADNESS_AFTERMATH = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT, ModId.of("madness_aftermath"),
			new MadnessAftermathMobEffect(MobEffectCategory.NEUTRAL, 0xFFE680)
	);

	public static final Holder<MobEffect> SUPERHERO_WEAKNESS = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT, ModId.of("superhero_weakness"),
			new SuperheroWeaknessEffect(MobEffectCategory.HARMFUL, 0xFF7FFF30)
	);

	private ModEffects() {
	}

	public static void init() {
	}

	public static boolean isMadness(net.minecraft.world.entity.LivingEntity entity) {
		return entity != null && entity.hasEffect(MADNESS);
	}

	public static boolean isAftermath(net.minecraft.world.entity.LivingEntity entity) {
		return entity != null && entity.hasEffect(MADNESS_AFTERMATH);
	}
}
