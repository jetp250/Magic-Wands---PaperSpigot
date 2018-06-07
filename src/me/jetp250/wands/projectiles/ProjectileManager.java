package me.jetp250.wands.projectiles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import me.jetp250.wands.events.MagicMissileRemoveEvent;
import me.jetp250.wands.events.MagicMissileSpawnEvent;
import net.minecraft.server.v1_12_R1.MinecraftServer;

public final class ProjectileManager implements Runnable {

	private static ProjectileManager instance;

	public static ProjectileManager getInstance() {
		if (instance == null) {
			instance = new ProjectileManager();
		}
		return instance;
	}

	private final Set<ProjectileBase> projectiles;
	private final Set<QueuedProjectile> queued;
	private BukkitTask task;

	public ProjectileManager() {
		this.projectiles = new HashSet<>();
		this.queued = new HashSet<>();
	}

	public boolean addProjectile(ProjectileBase projectile) {
		if (projectile.data.getSpawnDelay().getUpperBound() > 0) {
			this.queued.add(new QueuedProjectile(projectile));
			return true;
		}
		return internalAddProjectile(projectile);
	}

	private boolean internalAddProjectile(ProjectileBase projectile) {
		projectile.onSpawn();
		if (new MagicMissileSpawnEvent(projectile).callEvent()) {
			projectiles.add(projectile);
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (!queued.isEmpty()) {
			updateQueue();
		}
		Iterator<ProjectileBase> iterator = projectiles.iterator();
		while (iterator.hasNext()) {
			ProjectileBase proj = iterator.next();
			try {
				proj.tick();
			} catch (Exception ex) {
				ex.printStackTrace();
				iterator.remove();
				continue;
			}
			if (proj.isDead()) {
				new MagicMissileRemoveEvent(proj).callEvent();
				iterator.remove();
			}
		}
	}

	private void updateQueue() {
		int currentTick = MinecraftServer.currentTick;
		Iterator<QueuedProjectile> iterator = this.queued.iterator();
		while (iterator.hasNext()) {
			QueuedProjectile next = iterator.next();
			if (next.scheduledTick <= currentTick) {
				internalAddProjectile(next.projectile);
				iterator.remove();
			}
		}
	}

	public void start(Plugin plugin) {
		stop();
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 0L);
	}

	public void stop() {
		if (task == null) // Already stopped
			return;
		task.cancel();
		task = null;
	}

	public void clear() {
		projectiles.clear();
	}

	private static class QueuedProjectile {

		final ProjectileBase projectile;
		final int scheduledTick;

		public QueuedProjectile(ProjectileBase projectile) {
			this.projectile = projectile;
			Random random = ThreadLocalRandom.current();
			int delay = projectile.data.getSpawnDelay().getRandomValue(random);
			this.scheduledTick = MinecraftServer.currentTick + delay;
		}

	}

}
