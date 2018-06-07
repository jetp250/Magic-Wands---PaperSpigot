package me.jetp250.wands.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.jetp250.wands.MagicWands;
import me.jetp250.wands.projectiles.ProjectileManager;
import me.jetp250.wands.wands.Wand;
import me.jetp250.wands.wands.WandStorage;

public class WandCommandExecutor implements TabExecutor {

	private final MagicWands plugin;

	public WandCommandExecutor(MagicWands plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("give", "reload");
		}
		String subcmd = args[0].toLowerCase();
		switch (subcmd) {
			case "give":
				return WandStorage.listWands().stream().map(wand -> wand.getName()).collect(Collectors.toList());
			default:
				return Collections.emptyList();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(listWands());
			return true;
		}
		String subcmd = args[0].toLowerCase();
		switch (subcmd) {
			case "give":
				giveWand(sender, args);
				break;
			case "reload":
			case "rl":
				reload(sender);
				break;
		}
		return true;
	}

	private void giveWand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command");
			return;
		}
		if (args.length < 2) {
			sender.sendMessage("Usage: /wands give <name>");
			return;
		}
		String name = args[1];
		Wand wand = WandStorage.getByName(name);
		if (wand == null) {
			sender.sendMessage("No wand found by name '" + name + "'");
			sender.sendMessage("Type '/wands' to list all wands");
			return;
		}
		wand.giveTo((Player) sender);
		sender.sendMessage("Wand added to your inventory!");
	}

	private void reload(CommandSender sender) {
		plugin.reload();
		ProjectileManager.getInstance().clear();
		sender.sendMessage("MagicWands reloaded");
	}

	private String listWands() {
		Collection<Wand> wands = WandStorage.listWands();
		if (wands.isEmpty())
			return "Loaded wands: None";
		StringBuilder builder = new StringBuilder("Loaded wands:");
		for (Wand wand : wands) {
			builder.append(' ').append(wand.getName()).append(',');
		}
		return builder.substring(0, builder.length() - 1).toString();
	}

}
