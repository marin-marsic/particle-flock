package tree;

import java.util.List;

import model.Line;
import projekt.Boid;
import tree.Node.Side;
import vector.Vector3D;

public class KDTree {
	
	private int depth;
	private Node tree;

	public KDTree(int depth, List<Boid> boids) {
		this.depth = depth;
		this.tree = constructTree(boids);
	}

	private Node constructTree(List<Boid> boids) {
		return new Node(depth, boids, 0, Side.ROOT);
	}
	
	public List<Boid> getNearBoids(Boid b) {
		return tree.getItems(depth, b.getPosition());
	}
	
	public List<Line> getBorders() {
		return tree.getXBorders(depth);
	}

}
