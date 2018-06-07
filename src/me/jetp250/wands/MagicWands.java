package me.jetp250.wands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.jetp250.wands.commands.WandCommandExecutor;
import me.jetp250.wands.listener.PlayerEventListener;
import me.jetp250.wands.projectiles.ProjectileManager;
import me.jetp250.wands.projectiles.data.ProjectileDataCache;
import me.jetp250.wands.utilities.configuration.Configuration;
import me.jetp250.wands.wands.WandStorage;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.Items;

public final class MagicWands extends JavaPlugin {

	public static final Item DEFAULT_WAND_ITEM = Items.STICK;

	private Configuration wands;
	private Configuration projectiles;

	@Override
	public void onEnable() {
		setupConfig();
		this.getCommand("wands").setExecutor(new WandCommandExecutor(this));
		Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
		ProjectileManager.getInstance().start(this);
		Bukkit.getScheduler().runTask(this, this::reload);
	}

	private void setupConfig() {
		this.wands = new Configuration(getDataFolder(), "Wands.yml");
		this.projectiles = new Configuration(getDataFolder(), "Projectiles.yml");
	}

	public void reload() {
		wands.load();
		projectiles.load();
		ProjectileDataCache.loadProjectiles(projectiles);
		WandStorage.loadWands(wands);
	}

	public Configuration getWands() {
		return this.wands;
	}

	public Configuration getProjectiles() {
		return this.projectiles;
	}

}
