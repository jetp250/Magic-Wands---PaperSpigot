package me.jetp250.wands.wands;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.jetp250.wands.MagicWands;
import me.jetp250.wands.projectiles.data.ProjectileData;
import me.jetp250.wands.projectiles.data.ProjectileDataCache;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import net.minecraft.server.v1_12_R1.PlayerInventory;

public class Wand {

	private final String name;
	private final ItemStack stack;

	private int manaCost;

	private final ProjectileData[] projectileData;

	public Wand(ConfigurationSection section) {
		this.name = section.getName().toLowerCase().trim().replace(' ', '_');
		this.stack = createItemStack(section, name);
		this.projectileData = ProjectileDataCache.getProjectiles(section);
		this.manaCost = section.getInt("Mana Cost", 50);
	}

	public int getManaCost() {
		return this.manaCost;
	}

	public void setManaCost(int newCost) {
		this.manaCost = newCost;
	}

	public String getName() {
		return this.name;
	}

	public void shoot(Player shooter) {
		EntityPlayer player = ((CraftPlayer) shooter).getHandle();
		for (ProjectileData data : this.projectileData) {
			data.createProjectile(this, player).spawn();
		}
	}

	public void giveTo(Player bukkitPlayer) {
		ItemStack stack = this.stack.cloneItemStack();

		EntityPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
		PlayerInventory inventory = player.inventory;
		int slot = inventory.getFirstEmptySlotIndex();
		if (slot == -1) {
			player.dropItem(stack, 0f);
			return;
		}
		inventory.setItem(slot, stack);
	}

	static ItemStack createItemStack(ConfigurationSection section, String wandName) {
		String data = section.getString("Item");
		ItemStack stack = new ItemStack(MagicWands.DEFAULT_WAND_ITEM);
		if (data != null) {
			loadData(ChatColor.translateAlternateColorCodes('&', data), stack);
		} else {
			loadProperties(stack, section);
		}
		NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
		tag.setString(WandStorage.WAND_TYPE_NBT, wandName);
		tag.setLong("Uniquifier", ThreadLocalRandom.current().nextLong());
		stack.setTag(tag);
		return stack;
	}

	@SuppressWarnings("deprecation")
	private static void loadData(String data, ItemStack target) {
		String[] parts = data.trim().split(" ", 3);
		if (parts.length == 0)
			return;
		Item item = Item.REGISTRY.get(new MinecraftKey(parts[0]));
		if (item != null) {
			target.setItem(item);
		}
		if (parts.length == 1)
			return;
		try {
			target.setData(Integer.parseInt(parts[1]));
		} catch (NumberFormatException ex) {
		}
		if (parts.length == 2)
			return;
		String nbt = parts[2];
		try {
			target.setTag(MojangsonParser.parse(nbt));
		} catch (MojangsonParseException ex) {
			System.err.println("Invalid NBT tag! " + ex.getMessage());
		}
	}

	// Lengthy..
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void loadProperties(ItemStack target, ConfigurationSection section) {
		String materialName = section.getString("Material", "").toLowerCase();
		Item item = Item.REGISTRY.get(new MinecraftKey(materialName));
		if (item != null) {
			target.setItem(item);
		}
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound display = new NBTTagCompound();
		String name = section.getName();
		if (section.isSet("Name")) {
			name = ChatColor.translateAlternateColorCodes('&', section.getString("Name"));
		}
		display.setString("LocName", name);
		if (section.isSet("Lore")) {
			Object raw = section.get("Lore");
			if (raw instanceof Iterable) {
				NBTTagList lore = new NBTTagList();
				for (String line : (Iterable<String>) raw) {
					String translated = ChatColor.translateAlternateColorCodes('&', line);
					lore.add(new NBTTagString(translated));
				}
				display.set("Lore", lore);
			}
		}
		tag.set("display", display);
		if (section.isSet("Tag")) { // Custom NBT tag for resource packs
			Object raw = section.get("Tag");
			if (raw instanceof String) {
				tag.setString("Tag", (String) raw);
			} else {
				System.err.println("Invalid tag set, not a String");
			}
		}
		target.setTag(tag);
	}

}
