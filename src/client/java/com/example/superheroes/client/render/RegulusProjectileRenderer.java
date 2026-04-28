package com.example.superheroes.client.render;

import com.example.superheroes.ModId;
import com.example.superheroes.entity.RegulusProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RegulusProjectileRenderer extends EntityRenderer<RegulusProjectileEntity> {
	private static final ResourceLocation TEXTURE = ModId.of("textures/entity/regulus_projectile.png");

	public RegulusProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(RegulusProjectileEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(RegulusProjectileEntity entity, float yaw, float partialTick,
	                   PoseStack pose, MultiBufferSource buffer, int packedLight) {
	}
}
