package me.jetp250.wands.wands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.minecraft.server.v1_12_R1.ItemStack;

public class WandStorage {

	public static final String WAND_TYPE_NBT = "WandType";

	private static final Map<String, Wand> NAME_MAP = new HashMap<>();

	private WandStorage() {
	}

	public static void loadWands(FileConfiguration config) {
		NAME_MAP.clear();
		for (String sectionName : config.getKeys(false)) {
			if (!config.isConfigurationSection(sectionName))
				continue;
			ConfigurationSection properties = config.getConfigurationSection(sectionName);
			Wand wand = new Wand(properties);
			NAME_MAP.put(wand.getName(), wand);
		}
	}

	public static Wand fromItemStack(ItemStack stack) {
		if (!stack.hasTag())
			return null;
		String type = stack.getTag().getString(WAND_TYPE_NBT);
		return getByName(type);
	}

	public static Collection<Wand> listWands() {
		return Collections.unmodifiableCollection(NAME_MAP.values());
	}

	public static Wand getByName(String name) {
		return getByExactName(name.toLowerCase().replace(' ', '_'));
	}

	public static Wand getByExactName(String name) {
		return NAME_MAP.get(name);
	}

}
