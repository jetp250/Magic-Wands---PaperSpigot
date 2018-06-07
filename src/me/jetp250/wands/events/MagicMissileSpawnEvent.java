package me.jetp250.wands.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.jetp250.wands.projectiles.MagicMissile;

public class MagicMissileSpawnEvent extends MagicMissileEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled;

	public MagicMissileSpawnEvent(MagicMissile projectile) {
		super(projectile);
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return MagicMissileSpawnEvent.HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return MagicMissileSpawnEvent.HANDLERS;
	}

}
