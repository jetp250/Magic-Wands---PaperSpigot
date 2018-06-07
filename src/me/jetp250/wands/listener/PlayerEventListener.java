package me.jetp250.wands.listener;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.jetp250.wands.events.PlayerUseWandEvent;
import me.jetp250.wands.wands.Wand;
import me.jetp250.wands.wands.WandStorage;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Items;

public final class PlayerEventListener implements Listener {

	public PlayerEventListener() {
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!checkAction(event.getAction()) || event.getHand() != EquipmentSlot.HAND)
			return;
		tryShoot(event.getPlayer());
	}

	private void tryShoot(Player shooter) {
		EntityPlayer player = ((CraftPlayer) shooter).getHandle();
		ItemStack mainHand = getMainHand(player);
		if (mainHand == null || mainHand.getItem() == Items.a)
			return;
		Wand wand = WandStorage.fromItemStack(mainHand);
		if (wand == null)
			return;
		PlayerUseWandEvent event = new PlayerUseWandEvent(shooter, wand);
		if (!event.callEvent()) { // cancelled
			return;
		}
		event.getWand().shoot(shooter);
	}

	private boolean checkAction(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}

	private ItemStack getMainHand(EntityPlayer player) {
		return player.getEquipment(EnumItemSlot.MAINHAND);
	}

}
