package me.jetp250.wands.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import me.jetp250.wands.projectiles.ProjectileBase;
import me.jetp250.wands.utilities.math.Vec3f;
import me.jetp250.wands.wands.Wand;

public abstract class MagicMissileEvent extends Event {

	private final ProjectileBase projectile;

	public MagicMissileEvent(ProjectileBase projectile) {
		this.projectile = projectile;
	}

	public ProjectileBase getProjectile() {
		return this.projectile;
	}

	public World getWorld() {
		return projectile.getWorld();
	}

	public LivingEntity getShooter() {
		return projectile.getShooter();
	}

	public Wand getWand() {
		return projectile.getWand();
	}

	public Location getProjectileLocation() {
		Vec3f position = projectile.getPosition();
		return new Location(getWorld(), position.x, position.y, position.z);
	}

}
