package me.jetp250.wands.projectiles.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

import me.jetp250.wands.utilities.configuration.OptionParser;

public final class ProjectileDataCache {

	private static final Map<String, ProjectileData> NAME_MAP = new HashMap<>();
	private static final Pattern SPLIT_PATTERN = Pattern.compile(" ");

	public static void loadProjectiles(ConfigurationSection root) {
		NAME_MAP.clear();
		for (String sectionName : root.getKeys(false)) {
			if (!root.isConfigurationSection(sectionName)) {
				continue;
			}
			ConfigurationSection section = root.getConfigurationSection(sectionName);
			ProjectileData data = ProjectileData.fromSection(section);
			NAME_MAP.put(section.getName().toLowerCase().trim().replace(' ', '_'), data);
		}
	}

	public static ProjectileData getByName(String name) {
		return NAME_MAP.get(name.toLowerCase());
	}

	public static ProjectileData[] getProjectiles(ConfigurationSection section) {
		if (!section.isList("Projectiles")) {
			return new ProjectileData[] { ProjectileData.DEFAULT_DATA };
		}
		List<String> list = section.getStringList("Projectiles");
		List<ProjectileData> result = new ArrayList<>();
		for (String string : list) {
			ProjectileData data = fromString(string);
			if (data != null) {
				result.add(data);
			}
		}
		if (result.isEmpty()) {
			return new ProjectileData[] { ProjectileData.DEFAULT_DATA };
		}
		return result.toArray(new ProjectileData[result.size()]);
	}

	private static ProjectileData fromString(String string) {
		string = string.trim().toLowerCase();
		String[] split = SPLIT_PATTERN.split(string);
		String name = split[0];
		ProjectileData data = NAME_MAP.get(name.replace(' ', '_'));
		if (data == null) {
			data = ProjectileData.DEFAULT_DATA;
		}
		if (split.length > 0) {
			String[] arguments = Arrays.copyOfRange(split, 1, split.length);
			data = data.withData(OptionParser.parse(arguments));
		}
		return data;
	}

}
