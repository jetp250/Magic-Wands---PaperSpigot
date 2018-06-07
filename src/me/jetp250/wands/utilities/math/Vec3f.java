package me.jetp250.wands.utilities.math;

import net.minecraft.server.v1_12_R1.Entity;

public class Vec3f {

	public float x, y, z;

	public Vec3f() {
	}

	public Vec3f(Vec3f toCopy) {
		this(toCopy.x, toCopy.y, toCopy.z);
	}

	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// These two are for convenience..
	public Vec3f(double x, double y, double z) {
		this((float) x, (float) y, (float) z);
	}

	public Vec3f(Entity entity) {
		this((float) entity.locX, (float) entity.locY + entity.getHeadHeight(), (float) entity.locZ);
	}

	public Vec3f setTo(Vec3f values) {
		return set(values.x, values.y, values.z);
	}

	public Vec3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3f setX(float x) {
		this.x = x;
		return this;
	}

	public Vec3f setY(float y) {
		this.y = y;
		return this;
	}

	public Vec3f setZ(float z) {
		this.z = z;
		return this;
	}

	public Vec3f add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vec3f div(float n) {
		float inv = 1F / n;
		this.x *= inv;
		this.y *= inv;
		this.z *= inv;
		return this;
	}

	public Vec3f add(Vec3f other) {
		return add(other.x, other.y, other.z);
	}

	public Vec3f sub(Vec3f other) {
		return add(-other.x, -other.y, -other.z);
	}

	public Vec3f mult(Vec3f other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
		return this;
	}

	public Vec3f mult(float n) {
		this.x *= n;
		this.y *= n;
		this.z *= n;
		return this;
	}

	public Vec3f reverse() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vec3f rotateZAround(float x, float y, float angle) {
		this.x -= x;
		this.y -= y;
		rotateZ(angle);
		this.x += x;
		this.y += y;
		return this;
	}

	public Vec3f rotateYAround(float x, float z, float angle) {
		this.x -= x;
		this.z -= z;
		rotateY(angle);
		this.x += x;
		this.z += z;
		return this;
	}

	public Vec3f rotateXAround(float y, float z, float angle) {
		this.y -= y;
		this.z -= z;
		rotateX(angle);
		this.y += y;
		this.z += z;
		return this;
	}

	public Vec3f rotateZ(float angle) {
		float radians = FloatMath.toRadians(angle);
		float sin = FloatMath.sin(radians);
		float cos = FloatMath.cos(radians);
		float newX = x * cos - y * sin;
		float newY = x * sin + y * cos;
		this.x = newX;
		this.y = newY;
		return this;
	}

	public Vec3f rotateY(float angle) {
		float radians = FloatMath.toRadians(angle);
		float sin = FloatMath.sin(radians);
		float cos = FloatMath.cos(radians);
		float newX = x * cos + z * sin;
		float newZ = -x * sin + z * cos;
		this.x = newX;
		this.z = newZ;
		return this;
	}

	public Vec3f rotateX(float angle) {
		float radians = FloatMath.toRadians(angle);
		float sin = FloatMath.sin(radians);
		float cos = FloatMath.cos(radians);
		float newY = y * cos - z * sin;
		float newZ = y * sin + z * cos;
		this.y = newY;
		this.z = newZ;
		return this;
	}

	public Vec3f rotate90() {
		float tx = x;
		this.x = -this.z;
		this.z = tx;
		return this;
	}

	public Vec3f rotate90Reverse() {
		float ty = z;
		this.z = -x;
		this.x = ty;
		return this;
	}

	public Vec3f normalize() {
		float invLen = FloatMath.invSqrt(x * x + y * y + z * z);
		x *= invLen;
		y *= invLen;
		z *= invLen;
		return this;
	}

	public Vec3f zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public float distanceSquared(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public float distanceSquared(Vec3f other) {
		return distanceSquared(other.x, other.y, other.z);
	}

	public float distance(float x, float y, float z) {
		return FloatMath.sqrt(distanceSquared(x, y, z));
	}

	public float distance(Vec3f other) {
		return FloatMath.sqrt(distanceSquared(other));
	}

	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	public float length() {
		return FloatMath.sqrt(lengthSquared());
	}

	public Vec3f setLength(float length) {
		float mult = length * FloatMath.invSqrt(x * x + y * y + z * z);
		this.x *= mult;
		this.y *= mult;
		this.z *= mult;
		return this;
	}

	public Vec3f limit(float length) {
		float lengthSquared = x * x + y * y + z * z;
		if (lengthSquared > length * length) {
			float mult = length * FloatMath.invSqrt(lengthSquared);
			this.x *= mult;
			this.y *= mult;
			this.z *= mult;
		}
		return this;
	}

	public float getYaw() {
		return FloatMath.atan2(x, z);
	}

	public Vec3f setYaw(float yaw) {
		float radians = FloatMath.toRadians(yaw);
		float length = FloatMath.sqrt(x * x + z * z);
		this.x = FloatMath.sin(radians) * length;
		this.z = FloatMath.cos(radians) * length;
		return this;
	}

	public float getPitch() {
		return FloatMath.atan2(y, FloatMath.sqrt(x * x + z * z));
	}

	public Vec3f setPitch(float degrees) {
		float pitch = FloatMath.toRadians(degrees);
		float yaw = getYaw();
		float length = this.length();
		this.y = -FloatMath.sin(pitch) * length;
		float xz = FloatMath.cos(pitch) * length;
		this.x = -xz * FloatMath.sin(yaw);
		this.z = xz * FloatMath.cos(yaw);
		return this;
	}

	/**
	 * In radians!
	 */
	public float angle(Vec3f other) {
		float dot = dot(other) * FloatMath.invSqrt(lengthSquared() * other.lengthSquared());
		return (float) Math.acos(dot);
	}

	public float dot(Vec3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Vec3f)) {
			return false;
		}
		Vec3f other = (Vec3f) obj;
		return Math.abs(lengthSquared() - other.lengthSquared()) < 1.0E-6D;
	}

	@Override
	public int hashCode() {
		long hash = 7;
		long rawXBits = Double.doubleToRawLongBits(x);
		long rawYBits = Double.doubleToRawLongBits(y);
		long rawZBits = Double.doubleToRawLongBits(z);
		hash = 79 * hash + (rawXBits ^ rawXBits >>> 32);
		hash = 79 * hash + (rawYBits ^ rawYBits >>> 32);
		hash = 79 * hash + (rawZBits ^ rawZBits >>> 32);
		return (int) hash;
	}

	@Override
	public Vec3f clone() {
		return new Vec3f(x, y, z);
	}

	public static Vec3f random() {
		return fromYawPitch(FloatMath.randFloat() * 2 * FloatMath.PI, FloatMath.randFloat() * 2 * FloatMath.PI);
	}

	public static Vec3f fromAngle(float yaw) {
		yaw = FloatMath.toRadians(yaw);
		return new Vec3f(FloatMath.sin(yaw), 0, FloatMath.cos(yaw));
	}

	/**
	 * @param yaw
	 *            - Degrees
	 * @param pitch
	 *            - Degrees
	 */
	public static Vec3f fromYawPitch(float yaw, float pitch) {
		float yawRadians = FloatMath.toRadians(yaw);
		float pitchRadians = FloatMath.toRadians(pitch);
		float xz = FloatMath.cos(pitchRadians);
		return new Vec3f(-xz * FloatMath.sin(yawRadians), -FloatMath.sin(pitchRadians), xz * FloatMath.cos(yawRadians));
	}

}
