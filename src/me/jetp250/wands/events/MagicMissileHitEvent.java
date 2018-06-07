package me.jetp250.wands.events;

import javax.annotation.Nullable;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.jetp250.wands.projectiles.MagicMissile;

public class MagicMissileHitEvent extends MagicMissileEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private LivingEntity entity;
	private Block block;

	private boolean cancelled;

	public MagicMissileHitEvent(MagicMissile projectile, LivingEntity entity) {
		super(projectile);
		this.entity = entity;
	}

	public MagicMissileHitEvent(MagicMissile projectile, Block block) {
		super(projectile);
		this.block = block;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Nullable
	public Block getHitBlock() {
		return this.block;
	}

	@Nullable
	public LivingEntity getHitEntity() {
		return this.entity;
	}

	@Override
	public HandlerList getHandlers() {
		return MagicMissileHitEvent.HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
