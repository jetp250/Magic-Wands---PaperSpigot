package me.jetp250.wands.utilities.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class Configuration extends YamlConfiguration {

	private final File file;

	public Configuration(File folder, String name) {
		this(new File(folder, name));
	}

	public Configuration(File file) {
		this.file = file;
		if (!file.exists()) {
			try {
				Files.createParentDirs(file);
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		load();
	}

	public void save() {
		try {
			this.save(this.file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void load() {
		try {
			this.load(file);
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
	}

}
