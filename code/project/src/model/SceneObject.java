package model;

import java.util.List;

import vector.Vector3D;

public class SceneObject {

	private List<Vector3D> vertices;
	private List<Triangle> triangles;
	
	public SceneObject(List<Vector3D> vertices, List<Triangle> triangles) {
		this.vertices = vertices;
		this.triangles = triangles;
	}

	public List<Vector3D> getVertices() {
		return vertices;
	}

	public List<Triangle> getTriangles() {
		return triangles;
	}
}
