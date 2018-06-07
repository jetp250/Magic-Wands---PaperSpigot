package me.jetp250.wands.projectiles;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.jetp250.wands.projectiles.data.ProjectileData;
import me.jetp250.wands.utilities.math.Vec3f;
import me.jetp250.wands.wands.Wand;
import net.minecraft.server.v1_12_R1.EntityLiving;

public class ParticleProjectile extends ProjectileBase {

	public ParticleProjectile(ProjectileData data, Wand wand, EntityLiving shooter) {
		super(data, wand, shooter);
	}

	@Override
	protected void update() {
		super.update();
		playParticles();
	}

	protected void playParticles() {
		data.displayParticles(this);
	}

	@Override
	protected void onSpawn() {
		Random random = ThreadLocalRandom.current();
		float speed = data.getSpeed().getRandomValue(random);
		float yaw = shooter.yaw + data.getYawOffset().getRandomValue(random);
		float pitch = shooter.pitch;
		this.velocity = Vec3f.fromYawPitch(yaw, pitch).mult(speed);
		this.pos = new Vec3f(shooter).add(velocity);
	}

}
