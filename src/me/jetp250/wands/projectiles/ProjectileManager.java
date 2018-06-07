package me.jetp250.wands.projectiles;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;

import me.jetp250.wands.events.MagicMissileRemoveEvent;
import me.jetp250.wands.events.MagicMissileSpawnEvent;
import net.minecraft.server.v1_12_R1.MinecraftServer;

public final class ProjectileManager {

	private static ProjectileManager instance;

	public static ProjectileManager getInstance() {
		if (instance == null) {
			instance = new ProjectileManager();
		}
		return instance;
	}

	private final Set<MagicMissile> projectiles;
	private final Deque<QueuedProjectile> queued;
	private BukkitTask task;

	public ProjectileManager() {
		this.projectiles = Sets.newIdentityHashSet();
		this.queued = new ArrayDeque<>();
	}

	public boolean addProjectile(MagicMissile projectile) {
		if (projectile.data.getSpawnDelay().getUpperBound() > 0) {
			this.queued.add(new QueuedProjectile(projectile));
			return true;
		}
		return internalAddProjectile(projectile);
	}

	private boolean internalAddProjectile(MagicMissile projectile) {
		projectile.onSpawn();
		if (new MagicMissileSpawnEvent(projectile).callEvent()) {
			projectiles.add(projectile);
			return true;
		}
		return false;
	}

	private void update() {
		if (!queued.isEmpty()) {
			updateQueue();
		}
		Iterator<MagicMissile> iterator = projectiles.iterator();
		while (iterator.hasNext()) {
			MagicMissile proj = iterator.next();
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
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::update, 0L, 0L);
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

	static final class QueuedProjectile {

		final MagicMissile projectile;
		final int scheduledTick;

		public QueuedProjectile(MagicMissile projectile) {
			this.projectile = projectile;
			Random random = ThreadLocalRandom.current();
			int delay = projectile.data.getSpawnDelay().getRandomValue(random);
			this.scheduledTick = MinecraftServer.currentTick + delay;
		}

	}

}
