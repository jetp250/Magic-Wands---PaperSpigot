package me.jetp250.wands.projectiles.data.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.jetp250.wands.utilities.configuration.RangedFloat;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;

public class ParticleContainer {

	private static final EnumParticle DEFAULT_PARTICLE = EnumParticle.REDSTONE;
	public static final ParticleContainer[] DEFAULT_PARTICLES = new ParticleContainer[] { new ParticleContainer() };
	private static final int[] EMPTY_DATA = new int[0];

	private final EnumParticle type;
	private final RangedFloat xOffset, yOffset, zOffset;
	private final RangedFloat speed;
	private final int amount;

	private final int[] data;

	private boolean colored;

	protected ParticleContainer() {
		this.type = ParticleContainer.DEFAULT_PARTICLE;
		this.xOffset = new RangedFloat(0);
		this.yOffset = new RangedFloat(0);
		this.zOffset = new RangedFloat(0);
		this.speed = new RangedFloat(0);
		this.amount = 5;
		this.data = ParticleContainer.EMPTY_DATA;
	}

	public ParticleContainer(ConfigurationSection section) {
		this.type = getType(section.getString("Type", "redstone"));
		this.amount = section.getInt("Amount", 5);
		if (section.isSet("Color") && section.isConfigurationSection("Color")) {
			ConfigurationSection color = section.getConfigurationSection("Color");
			this.xOffset = getColorProperty(color, "Red");
			this.yOffset = getColorProperty(color, "Green");
			this.zOffset = getColorProperty(color, "Blue");
			this.speed = new RangedFloat(1);
			this.colored = true;
		} else {
			this.xOffset = new RangedFloat(section.getString("X Offset", "0"));
			this.yOffset = new RangedFloat(section.getString("Y Offset", "0"));
			this.zOffset = new RangedFloat(section.getString("Z Offset", "0"));
			this.speed = new RangedFloat(section.getString("Speed", "0"));
		}
		this.data = parseData(section.getString("Data"), this.type);
	}

	public void constructPackets(Random random, float x, float y, float z, List<PacketPlayOutWorldParticles> target) {
		float xOff = xOffset.getRandomValue(random);
		float yOff = yOffset.getRandomValue(random);
		float zOff = zOffset.getRandomValue(random);
		if (!colored) {
			float speed = this.speed.getRandomValue(random);
			target.add(new PacketPlayOutWorldParticles(type, true, x, y, z, xOff, yOff, zOff, speed, amount, data));
			return;
		}
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(type, true, x, y, z, xOff, yOff, zOff, 1f, 0, data);
		for (int i = 0; i < amount; ++i) {
			target.add(packet);
		}
	}

	public static ParticleContainer[] fromSection(ConfigurationSection section) {
		if (section == null) {
			return ParticleContainer.DEFAULT_PARTICLES;
		}
		Set<String> keys = section.getKeys(false);
		List<ParticleContainer> list = new ArrayList<>();
		for (String sectionName : keys) {
			if (!section.isConfigurationSection(sectionName))
				continue;
			list.add(new ParticleContainer(section.getConfigurationSection(sectionName)));
		}
		if (list.isEmpty()) {
			return new ParticleContainer[] { new ParticleContainer(section) };
		}
		return list.toArray(new ParticleContainer[list.size()]);
	}

	private static EnumParticle getType(String name) {
		EnumParticle type = EnumParticle.a(name); // case sensitive!
		return type != null ? type : ParticleContainer.DEFAULT_PARTICLE;
	}

	private static RangedFloat getColorProperty(ConfigurationSection section, String name) {
		double val = section.getDouble(name, 0D);
		if (val <= 0.0001D)
			return new RangedFloat(Float.MIN_NORMAL);
		return new RangedFloat((float) val / 255F);
	}

	private static int[] parseData(String input, EnumParticle type) {
		if (input == null || input.isEmpty())
			return ParticleContainer.EMPTY_DATA;
		String[] parts = input.split("\\:");
		int id = getId(parts[0], type);
		if (id == -1)
			return ParticleContainer.EMPTY_DATA;
		int data = 0;
		if (parts.length == 2) {
			try {
				data = Integer.parseInt(parts[1]);
			} catch (NumberFormatException ex) {
			}
		}
		return new int[] { id + data * 4096 };
	}

	private static int getId(String name, EnumParticle type) {
		if (type.d() == 0) { // required argument count
			return -1;
		}
		if (type == EnumParticle.ITEM_CRACK) {
			Item item = Item.b(name);
			return item == null ? -1 : Item.getId(item);
		}
		Block block = Block.getByName(name);
		return block == null ? -1 : Block.getId(block);
	}

}
