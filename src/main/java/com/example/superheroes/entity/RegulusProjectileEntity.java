package com.example.superheroes.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class RegulusProjectileEntity extends Projectile {
	private static final int MAX_LIFE = 80;
	private static final float DAMAGE = 28.0f;

	private int lifeTicks = 0;

	public RegulusProjectileEntity(EntityType<? extends RegulusProjectileEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	protected boolean canHitEntity(Entity entity) {
		return entity != getOwner() && super.canHitEntity(entity);
	}

	@Override
	public void tick() {
		super.tick();
		if (++lifeTicks > MAX_LIFE) {
			discard();
			return;
		}
		Vec3 motion = getDeltaMovement();
		Vec3 start = position();
		Vec3 end = start.add(motion);

		EntityHitResult ehr = ProjectileUtil.getEntityHitResult(level(), this, start, end,
				getBoundingBox().expandTowards(motion).inflate(0.6),
				this::canHitEntity);
		if (ehr != null) {
			onHit(ehr);
			if (isRemoved()) {
				return;
			}
		}

		setPos(end);

		if (level().isClientSide) {
			level().addParticle(ParticleTypes.CRIT, end.x, end.y, end.z, 0.0, 0.0, 0.0);
			level().addParticle(ParticleTypes.SMOKE, end.x, end.y, end.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Entity target = result.getEntity();
		if (level() instanceof ServerLevel sl) {
			Entity owner = getOwner();
			target.hurt(sl.damageSources().indirectMagic(this, owner), DAMAGE);
			sl.sendParticles(ParticleTypes.CRIT,
					target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
					16, 0.4, 0.4, 0.4, 0.1);
		}
		discard();
	}
}
