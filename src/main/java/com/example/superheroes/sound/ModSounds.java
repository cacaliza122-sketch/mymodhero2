package com.example.superheroes.sound;

import com.example.superheroes.ModId;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
	public static final SoundEvent LIGHTNING_THUNDER_ANIME = register("lightning.thunder.anime");
	public static final SoundEvent LIGHTNING_THUNDER_LOUD = register("lightning.thunder.loud");

	private ModSounds() {
	}

	private static SoundEvent register(String path) {
		ResourceLocation id = ModId.of(path);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	public static void init() {
	}
}
