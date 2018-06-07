package me.jetp250.wands.projectiles.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;

import me.jetp250.wands.projectiles.MagicMissile;
import me.jetp250.wands.projectiles.data.particles.ParticleContainer;
import me.jetp250.wands.utilities.configuration.RangedFloat;
import me.jetp250.wands.utilities.configuration.RangedInt;
import me.jetp250.wands.utilities.math.Vec3f;
import me.jetp250.wands.wands.Wand;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.World;

public class ProjectileData {

	private static final Map<String, ProjectileData> NAME_MAP = new HashMap<>();

	public static final ProjectileData DEFAULT_DATA = new ProjectileData();

	private final ParticleContainer[] particles;

	private RangedFloat damage;
	private RangedFloat speed;
	private RangedFloat range;
	private RangedFloat yawOffset;

	private RangedInt spawnDelay;

	private float hitboxSize;

	private boolean bounce;

	protected ProjectileData() {
		this.damage = new RangedFloat(1);
		this.speed = new RangedFloat(1);
		this.range = new RangedFloat(10);
		this.spawnDelay = new RangedInt(0);
		this.particles = ParticleContainer.DEFAULT_PARTICLES;
		this.yawOffset = new RangedFloat(0);
		this.hitboxSize = 0.5f;
	}

	protected ProjectileData(ProjectileData toCopy) {
		this.damage = toCopy.damage;
		this.speed = toCopy.speed;
		this.range = toCopy.range;
		this.spawnDelay = toCopy.spawnDelay;
		this.particles = toCopy.particles.clone();
		this.hitboxSize = toCopy.hitboxSize;
		this.yawOffset = toCopy.yawOffset;
	}

	protected ProjectileData(ConfigurationSection section) {
		this.damage = new RangedFloat(section.getString("Damage", "2"));
		this.range = new RangedFloat(section.getString("Range", "10"));
		this.speed = new RangedFloat(section.getString("Speed", "1"));

		this.spawnDelay = new RangedInt(section.getString("Spawn Delay", "0"));
		this.yawOffset = new RangedFloat(section.getString("Angle", "0"));

		this.hitboxSize = (float) section.getDouble("Hitbox", 0.8);

		this.bounce = section.getBoolean("Bounce", false);

		ConfigurationSection particles = section.getConfigurationSection("Particles");
		this.particles = ParticleContainer.fromSection(particles);

		NAME_MAP.put(section.getName().toLowerCase().trim().replace(' ', '_'), this);
	}

	public boolean getShouldBounce() {
		return this.bounce;
	}

	public RangedInt getSpawnDelay() {
		return this.spawnDelay;
	}

	public float getHitboxSize() {
		return this.hitboxSize;
	}

	public RangedFloat getYawOffset() {
		return this.yawOffset;
	}

	public RangedFloat getDamage() {
		return this.damage;
	}

	public RangedFloat getSpeed() {
		return this.speed;
	}

	public RangedFloat getRange() {
		return this.range;
	}

	public MagicMissile createProjectile(Wand wand, EntityLiving shooter) {
		return new MagicMissile(this, wand, shooter);
	}

	public void displayParticles(MagicMissile projectile) {
		Vec3f pos = projectile.getPosition();
		Random random = ThreadLocalRandom.current();
		List<PacketPlayOutWorldParticles> packets = new ArrayList<>();
		for (ParticleContainer container : this.particles) {
			container.constructPackets(random, pos.x, pos.y, pos.z, packets);
		}
		World world = ((CraftEntity) projectile.getShooter()).getHandle().world;
		for (EntityHuman human : world.players) {
			if (distanceSquared(human, pos) > 150 * 150)
				continue;
			PlayerConnection connection = ((EntityPlayer) human).playerConnection;
			for (Packet<?> packet : packets) {
				connection.sendPacket(packet);
			}
		}
	}

	@Override
	public ProjectileData clone() {
		return new ProjectileData(this);
	}

	// TODO remake this from ground up so that these methods are not even needed

	///////
	protected void loadSettings(Map<String, Object> settings) {
		Object angleOffset = settings.get("angle");
		if (angleOffset != null) {
			if (angleOffset instanceof Number) {
				this.yawOffset = new RangedFloat(((Number) angleOffset).floatValue());
			} else {
				this.yawOffset = new RangedFloat(angleOffset.toString());
			}
		}
		Object hitboxSize = settings.get("hitbox");
		if (hitboxSize instanceof Number) {
			this.hitboxSize = ((Number) hitboxSize).floatValue();
		}
		Object bounce = settings.get("bounce");
		if (bounce != null) {
			this.bounce = ((Boolean) bounce).booleanValue();
		}
		Object damage = settings.get("damage");
		if (damage != null) {
			if (damage instanceof Number) {
				this.damage = new RangedFloat(((Number) damage).floatValue());
			} else {
				this.damage = new RangedFloat(damage.toString());
			}
		}
		Object range = settings.get("range");
		if (range != null) {
			if (range instanceof Number) {
				this.range = new RangedFloat(((Number) range).floatValue());
			} else {
				this.range = new RangedFloat(range.toString());
			}
		}
		Object speed = settings.get("speed");
		if (speed != null) {
			if (speed instanceof Number) {
				this.speed = new RangedFloat(((Number) speed).floatValue());
			} else {
				this.speed = new RangedFloat(speed.toString());
			}
		}
		Object delay = settings.get("spawn_delay");
		if (delay != null) {
			if (delay instanceof Number) {
				this.spawnDelay = new RangedInt(((Number) delay).intValue());
			} else {
				this.spawnDelay = new RangedInt(delay.toString());
			}
		}
	}

	public ProjectileData withData(Map<String, Object> settings) {
		ProjectileData data;
		Object homing = settings.get("homing");
		if (homing instanceof Boolean && (Boolean) homing) {
			data = new HomingProjectileData(this);
		} else {
			data = new ProjectileData(this);
		}
		data.loadSettings(settings);
		return data;
	}

	public static ProjectileData fromSection(ConfigurationSection section) {
		if (section.getBoolean("Homing")) {
			return new HomingProjectileData(section);
		}
		return new ProjectileData(section);
	}
	////////

	private static double distanceSquared(Entity entity, Vec3f pos) {
		double x = entity.locX - pos.x;
		double y = entity.locY - pos.y;
		double z = entity.locZ - pos.z;
		return x * x + y * y + z * z;
	}

}
