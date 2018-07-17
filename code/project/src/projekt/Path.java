package projekt;

import java.util.ArrayList;
import java.util.List;

import vector.Vector3D;

public class Path {

	private List<Vector3D> nodes;

	public Path() {
		nodes = new ArrayList();
	}
	
	public void addNode(Vector3D node) {
		nodes.add(node);
	}

	public List<Vector3D> getNodes() {
		return nodes;
	}
}
