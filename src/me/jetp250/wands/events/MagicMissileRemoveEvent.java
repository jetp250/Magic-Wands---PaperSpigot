package me.jetp250.wands.events;

import org.bukkit.event.HandlerList;

import me.jetp250.wands.projectiles.MagicMissile;

public class MagicMissileRemoveEvent extends MagicMissileEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public MagicMissileRemoveEvent(MagicMissile projectile) {
		super(projectile);
	}

	@Override
	public HandlerList getHandlers() {
		return MagicMissileRemoveEvent.HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return MagicMissileRemoveEvent.HANDLERS;
	}

}
