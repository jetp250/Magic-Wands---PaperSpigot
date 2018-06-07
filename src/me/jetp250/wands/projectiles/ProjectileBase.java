package me.jetp250.wands.projectiles;

import java.util.List;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.entity.LivingEntity;

import com.google.common.base.Predicate;

import me.jetp250.wands.events.MagicMissileHitEvent;
import me.jetp250.wands.projectiles.data.ProjectileData;
import me.jetp250.wands.utilities.math.FloatMath;
import me.jetp250.wands.utilities.math.Vec3f;
import me.jetp250.wands.wands.Wand;
import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EnumDirection;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.MovingObjectPosition.EnumMovingObjectType;
import net.minecraft.server.v1_12_R1.Vec3D;
import net.minecraft.server.v1_12_R1.WorldServer;

public abstract class ProjectileBase {

	protected static final Predicate<Entity> ENTITY_PREDICATE = e -> e instanceof EntityLiving;

	protected Vec3f lastPos;
	protected Vec3f pos;
	protected Vec3f velocity;

	protected final ProjectileData data;

	private final float maxRangeSquared;
	private float distanceTraveled;

	protected final WorldServer world;
	protected final EntityLiving shooter;
	protected final Wand wand;

	private float damage;
	protected int ticksLived;
	protected boolean dead;

	public ProjectileBase(ProjectileData data, Wand wand, EntityLiving shooter) {
		this.shooter = shooter;
		this.data = data;
		this.world = (WorldServer) shooter.world;
		this.wand = wand;
		this.damage = data.getDamage().getRandomValue(world.random);
		this.maxRangeSquared = data.getRange().getRandomValue(world.random);
		this.lastPos = new Vec3f();
		this.pos = new Vec3f();
		this.velocity = new Vec3f();
	}

	public World getWorld() {
		return this.world.getWorld();
	}

	public float getDamage() {
		return this.damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public Vec3f getPosition() {
		return pos;
	}

	public Vec3f getVelocity() {
		return velocity;
	}

	public int getTicksLived() {
		return this.ticksLived;
	}

	final void tick() {
		this.lastPos.setTo(pos);
		ticksLived++;
		update();
	}

	protected void move() {
		this.pos.add(this.velocity);
		this.distanceTraveled += this.velocity.length();
	}

	protected void update() {
		move();
		if (distanceTraveled >= maxRangeSquared) {
			die();
			return;
		}
		checkCollisions();
	}

	public void die() {
		this.dead = true;
	}

	public boolean isDead() {
		return this.dead;
	}

	public Wand getWand() {
		return this.wand;
	}

	public LivingEntity getShooter() {
		return (LivingEntity) this.shooter.getBukkitEntity();
	}

	protected void onSpawn() {
	}

	protected void onCollision(MovingObjectPosition collision) {
		if (collision.type == EnumMovingObjectType.BLOCK) {
			if (!data.getShouldBounce()) {
				die();
				return;
			}
			bounce(collision.direction);
			return;
		}
		Entity entity = collision.entity;
		if (entity instanceof EntityLiving) {
			((EntityLiving) entity).damageEntity(DamageSource.mobAttack(shooter), damage);
			die();
		}
	}

	private void bounce(EnumDirection dir) {
		int x = dir.getAdjacentX();
		int y = dir.getAdjacentY();
		int z = dir.getAdjacentZ();
		if (x != 0)
			this.velocity.x = -this.velocity.x;
		else if (y != 0)
			this.velocity.y = -this.velocity.y;
		else if (z != 0)
			this.velocity.z = -this.velocity.z;
		float hitbox = data.getHitboxSize() / 2;
		pos.x += x * hitbox;
		pos.y += y * hitbox;
		pos.z += z * hitbox;
		this.lastPos.setTo(pos);
	}

	protected final void checkCollisions() {
		MovingObjectPosition result = rayTrace();
		if (result != null && result.type == EnumMovingObjectType.BLOCK) {
			IBlockData data = world.getType(result.a());
			if (data.getMaterial().isSolid()) {
				Vec3D pos = result.pos;
				this.pos.set((float) pos.x, (float) pos.y, (float) pos.z);
				int x = FloatMath.floor(this.pos.x);
				int y = FloatMath.floor(this.pos.y);
				int z = FloatMath.floor(this.pos.z);
				CraftChunk chunk = (CraftChunk) world.getChunkAt(x >> 4, z >> 4).bukkitChunk;
				CraftBlock block = new CraftBlock(chunk, x, y, z);
				if (new MagicMissileHitEvent(this, block).callEvent()) {
					onCollision(result);
				}
			}
			return;
		}
		EntityLiving colliding = getCollidingEntity();
		if (colliding == null)
			return;
		if (!new MagicMissileHitEvent(this, (LivingEntity) colliding.getBukkitEntity()).callEvent()) {
			return;
		}
		MovingObjectPosition pos = new MovingObjectPosition(colliding);
		onCollision(pos);
	}

	protected EntityLiving getCollidingEntity() {
		float size = data.getHitboxSize() * 0.5F;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.x - size, pos.y - size, pos.z - size, pos.x + size, pos.y + size, pos.z
				+ size);
		List<Entity> entities = world.getEntities(shooter, aabb, ENTITY_PREDICATE);
		if (entities.isEmpty()) {
			return null;
		}
		float minDistance = Float.MAX_VALUE;
		Entity nearest = null;
		for (Entity entity : entities) {
			float distance = distanceSquared(entity);
			if (distance < minDistance) {
				minDistance = distance;
				nearest = entity;
			}
		}
		return (EntityLiving) nearest;
	}

	protected float distanceSquared(Entity entity) {
		float x = (float) entity.locX - pos.x;
		float y = (float) (entity.locY + entity.getHeadHeight() / 2) - pos.y;
		float z = (float) entity.locZ - pos.z;
		return x * x + y * y + z * z;
	}

	protected MovingObjectPosition rayTrace() {
		Vec3D last = new Vec3D(lastPos.x, lastPos.y, lastPos.z);
		Vec3D now = new Vec3D(pos.x, pos.y, pos.z);
		return world.rayTrace(last, now);
	}

	public final boolean spawn() {
		return ProjectileManager.getInstance().addProjectile(this);
	}

}
