package tree;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import model.Line;
import projekt.Boid;
import vector.Vector3D;

public class Node {

	private double value, prevVal;
	private List<Boid> items;
	private Node left;
	private Node right;
	private Side side;
	
	public enum Side {
		LEFT, RIGHT, ROOT
	}

	public Node(int depth, List<Boid> items, double prevVal, Side side) {
		this.prevVal = prevVal;
		this.side = side;
		if (depth == 0 || items.size() == 0) {
			this.items = items;
			this.left = null;
			this.right = null;
		} else {
			this.items = null;
			TreeMap<Double, Boid> sortedMap = new TreeMap<>();
			for (Boid b : items) {
				if (depth % 2 == 0) {
					sortedMap.put(b.getPosition().getX(), b);
				} else {
					sortedMap.put(b.getPosition().getY(), b);
				}
			}
			List<Boid> values = new ArrayList<>(sortedMap.values());
			int middle = values.size() / 2 + 1;
			List<Boid> leftList = values.subList(0, middle);
			List<Boid> rightList = values.subList(middle, values.size());

			if (depth % 2 == 0) {
				this.value = values.get(middle).getPosition().getX();
			} else {
				this.value = values.get(middle).getPosition().getY();
			}
			this.left = new Node(depth - 1, leftList, value, Side.LEFT);
			this.right = new Node(depth - 1, rightList, value, Side.RIGHT);
		}
	}

	public List<Boid> getItems(int depth, Vector3D vector) {
		if (depth != 0) {
			double otherValue = 0;
			if (depth % 2 == 0) {
				otherValue = vector.getX();
			} else {
				otherValue = vector.getY();
			}
			if (otherValue < value) {
				return left.getItems(depth - 1, vector);
			} else {
				return right.getItems(depth - 1, vector);
			}
		}
		return items;
	}
	
	public List<Line> getXBorders(int depth) {
		List<Line> lines = new ArrayList();
		if (depth != 0) {
			lines.addAll(left.getXBorders(depth-1));
			lines.addAll(right.getXBorders(depth-1));
			if (side == Side.ROOT) {
				if (depth % 2 == 0) {
					Vector3D start = new Vector3D(value, -10, 0);
					Vector3D end = new Vector3D(value, 2000, 0);
					lines.add(new Line(start, end));
				} else {
					Vector3D start = new Vector3D(-10, value, 0);
					Vector3D end = new Vector3D(2000, value, 0);
					lines.add(new Line(start, end));
				}
			} else if (side == Side.LEFT) {
				if (depth % 2 == 0) {
					Vector3D start = new Vector3D(value, -10, 0);
					Vector3D end = new Vector3D(value, prevVal, 0);
					lines.add(new Line(start, end));
				} else {
					Vector3D start = new Vector3D(-10, value, 0);
					Vector3D end = new Vector3D(prevVal, value, 0);
					lines.add(new Line(start, end));
				}
			} else {
				if (depth % 2 == 0) {
					Vector3D start = new Vector3D(value, prevVal, 0);
					Vector3D end = new Vector3D(value, 2000, 0);
					lines.add(new Line(start, end));
				} else {
					Vector3D start = new Vector3D(prevVal, value, 0);
					Vector3D end = new Vector3D(2000, value, 0);
					lines.add(new Line(start, end));
				}
			}
		}
		return lines;
	}

}
