package me.jetp250.wands.projectiles.data;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import me.jetp250.wands.projectiles.HomingProjectile;
import me.jetp250.wands.projectiles.MagicMissile;
import me.jetp250.wands.utilities.configuration.RangedFloat;
import me.jetp250.wands.wands.Wand;
import net.minecraft.server.v1_12_R1.EntityLiving;

public class HomingProjectileData extends ProjectileData {

	private RangedFloat turnRate;
	private RangedFloat homingRange;
	private boolean forceHit;

	protected HomingProjectileData(ConfigurationSection section) {
		super(section);
		this.turnRate = new RangedFloat(section.getString("Turn Rate", "10"));
		this.homingRange = new RangedFloat(section.getString("Homing Range", "16"));
		this.forceHit = section.getBoolean("Force Hit", false);
	}

	protected HomingProjectileData(ProjectileData toCopy) {
		super(toCopy);
		if (!(toCopy instanceof HomingProjectileData)) {
			this.turnRate = new RangedFloat(1);
			this.homingRange = new RangedFloat(16);
			return;
		}
		HomingProjectileData copy = (HomingProjectileData) toCopy;
		this.turnRate = copy.turnRate;
		this.forceHit = copy.forceHit;
		this.homingRange = copy.homingRange;
	}

	public RangedFloat getTurnRate() {
		return this.turnRate;
	}

	public RangedFloat getHomingRange() {
		return this.homingRange;
	}

	public boolean getForceHit() {
		return this.forceHit;
	}

	@Override
	public ProjectileData withData(Map<String, Object> settings) {
		ProjectileData clone = new HomingProjectileData(this);
		clone.loadSettings(settings);
		return clone;
	}

	@Override
	public MagicMissile createProjectile(Wand wand, EntityLiving shooter) {
		return new HomingProjectile(this, wand, shooter);
	}

	@Override
	public ProjectileData clone() {
		return new HomingProjectileData(this);
	}

	@Override
	public void loadSettings(Map<String, Object> settings) {
		super.loadSettings(settings);
		Object forceHit = settings.get("force_hit");
		if (forceHit != null && forceHit instanceof Boolean) {
			this.forceHit = ((Boolean) forceHit).booleanValue();
		}
		Object turnRate = settings.get("turn_rate");
		if (turnRate != null) {
			if (turnRate instanceof Number) {
				this.turnRate = new RangedFloat(((Number) turnRate).floatValue());
			} else {
				this.turnRate = new RangedFloat(turnRate.toString());
			}
		}
		Object homingRange = settings.get("homing_range");
		if (homingRange != null) {
			if (homingRange instanceof Number) {
				this.homingRange = new RangedFloat(((Number) homingRange).floatValue());
			} else {
				this.turnRate = new RangedFloat(homingRange.toString());
			}
		}
	}

}
