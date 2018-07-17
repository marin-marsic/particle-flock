package model;

import vector.Vector3D;

public class Line {

	private Vector3D start, end;

	public Line(Vector3D start, Vector3D end) {
		this.start = start;
		this.end = end;
	}

	public Vector3D getStart() {
		return start;
	}

	public void setStart(Vector3D start) {
		this.start = start;
	}

	public Vector3D getEnd() {
		return end;
	}

	public void setEnd(Vector3D end) {
		this.end = end;
	}
	
	
}
