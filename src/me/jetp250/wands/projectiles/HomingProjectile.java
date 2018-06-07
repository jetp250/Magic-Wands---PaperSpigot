package me.jetp250.wands.projectiles;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.jetp250.wands.projectiles.data.HomingProjectileData;
import me.jetp250.wands.utilities.math.FloatMath;
import me.jetp250.wands.utilities.math.Vec3f;
import me.jetp250.wands.wands.Wand;
import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.MovingObjectPosition.EnumMovingObjectType;
import net.minecraft.server.v1_12_R1.Vec3D;

public class HomingProjectile extends ParticleProjectile {

	private EntityLiving target;
	private final float speed;

	private final float turnRate;
	private final boolean forceHit;

	private final float homingRange;

	public HomingProjectile(HomingProjectileData data, Wand wand, EntityLiving shooter) {
		super(data, wand, shooter);
		Random random = ThreadLocalRandom.current();
		this.speed = data.getSpeed().getRandomValue(random);
		this.turnRate = data.getTurnRate().getRandomValue(random);
		this.forceHit = data.getForceHit();
		this.homingRange = data.getHomingRange().getRandomValue(random);
	}

	@Override
	protected void move() {
		updateTarget();
		if (target == null) {
			super.move();
			return;
		}
		Vec3f targetPos = new Vec3f(target);
		float turnRate = getTurnRate();
		Vec3f dir = targetPos.sub(this.pos).normalize();
		this.velocity.mult(turnRate).add(dir).normalize().mult(speed);
		super.move();
	}

	private float getTurnRate() {
		float turnRate = this.turnRate;
		if (forceHit) {
			float distance = distanceSquared(target);
			float maxSquared = homingRange * homingRange;
			turnRate *= FloatMath.invSqrt(distance / maxSquared);
		}
		return 1f / turnRate;
	}

	private void updateTarget() {
		EntityLiving target = findTarget();
		if ((this.target != null && this.target.dead) || target != null) {
			this.target = target;
		}
	}

	private EntityLiving findTarget() {
		float size = homingRange;
		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		AxisAlignedBB area = new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);
		List<Entity> entities = world.getEntities(shooter, area, ENTITY_PREDICATE);
		if (entities.isEmpty())
			return null;
		Vec3D pos = new Vec3D(x, y, z);
		Entity nearest = null;
		float minDistance = Float.MAX_VALUE;
		for (Entity entity : entities) {
			float distance = distanceSquared(entity);
			if (distance < minDistance) {
				MovingObjectPosition rayTrace = world.rayTrace(pos, new Vec3D(entity.locX, entity.locY, entity.locZ));
				if (rayTrace == null || rayTrace.type != EnumMovingObjectType.BLOCK
						|| !world.getType(rayTrace.a()).getMaterial().isSolid()) {
					minDistance = distance;
					nearest = entity;
				}
			}
		}
		return (EntityLiving) nearest;
	}

	@Override
	protected void onSpawn() {
		Random random = ThreadLocalRandom.current();
		float yaw = shooter.yaw + data.getYawOffset().getRandomValue(random);
		float pitch = shooter.pitch;
		this.velocity = Vec3f.fromYawPitch(yaw, pitch).mult(speed);
		this.pos = new Vec3f(shooter).add(velocity);
	}

}
