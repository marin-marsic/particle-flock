package model;

import vector.Vector3D;

public class Rectangle {
	
	private Vector3D topLeft, topRight, bottomLeft, bottomRight;

	public Rectangle(Vector3D topLeft, Vector3D topRight, Vector3D bottomLeft, Vector3D bottomRight) {
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
	}

	public Vector3D getTopLeft() {
		return topLeft;
	}

	public Vector3D getTopRight() {
		return topRight;
	}

	public Vector3D getBottomLeft() {
		return bottomLeft;
	}

	public Vector3D getBottomRight() {
		return bottomRight;
	}
}
