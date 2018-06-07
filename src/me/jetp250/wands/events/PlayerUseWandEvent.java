package me.jetp250.wands.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import me.jetp250.wands.wands.Wand;

public class PlayerUseWandEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private Wand wand;
	private boolean cancelled;

	public PlayerUseWandEvent(Player player, Wand wand) {
		super(player);
		this.wand = wand;
	}

	public Wand getWand() {
		return wand;
	}

	public void setWand(Wand wand) {
		this.wand = wand;
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
		return PlayerUseWandEvent.HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return PlayerUseWandEvent.HANDLERS;
	}

}
