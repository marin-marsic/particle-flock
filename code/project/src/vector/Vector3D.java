package vector;

public final class Vector3D {
	
	private double x, y, z;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public Vector3D copy() {
		return new Vector3D(x, y, z);
	}
	
	public Vector3D normalize() {
		double d = magnitude(); 
		return new Vector3D(x/d, y/d, z/d);
	}
	
	public double magnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public Vector3D scale(double l) {
		return new Vector3D(x*l, y*l, z*l);
	}
	
	public Vector3D truncate(double min, double max) {
		double d = magnitude();
		if (d < min) {
			d = min;
		} else if (d > max) {
			d = max;
		}
		return this.copy().normalize().scale(d);
	}
	
	public void add(Vector3D vector) {
		x += vector.x;
		y += vector.y;
		z += vector.z;
	}
	
	public void subtract(Vector3D vector) {
		x -= vector.x;
		y -= vector.y;
		z -= vector.z;
	}
	
	public double distanceTo(Vector3D node) {
		double dX = x - node.getX();
		double dY = y - node.getY();
		double dZ = z - node.getZ();
		return (new Vector3D(dX, dY, dZ)).magnitude();
	}

}
