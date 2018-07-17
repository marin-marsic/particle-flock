package projekt;

import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.TransformationObject;
import texture.ParticleTexture;
import tree.KDTree;
import vector.Vector3D;

public class Environment {

	public int width;
	public int height;
	public Random r;

	int mode;
	int counter = 0;
	long nFrames;

	private boolean showTreeBorders, showTrace;
	private Path path;
	private ArrayList<Boid> boids;
	private KDTree kdTree;
	private Vector3D predator;
	public AnimationMode animationMode;
	private ParticleTexture texture;
	private int treeDepth = 1;
	
	public enum AnimationMode {
		ANIM_START, ANIM_PATH_FOLLOWING, ANIM_END
	}

	public Environment(int treeDepth, int width, int height, ParticleTexture texture) {
		this.treeDepth = treeDepth;
		this.width = width;
		this.height = height;
		this.r = new Random(System.currentTimeMillis());
		this.texture = texture;
		reset();
	}

	public void switchMode() {
		mode = 1 - mode;
	}

	public void reset() {
		path = new Path();
		this.mode = 0;
		showTreeBorders = false;
		showTrace = false;
		this.animationMode = AnimationMode.ANIM_START;
		predator = null;
	}

	public void input(double x, double y, double z) {
		path.addNode(new Vector3D(x, y, z));
	}
	
	public void setBoids(TransformationObject transformationObject) {
		this.boids = new ArrayList();
		List<Vector3D> startPoints = transformationObject.getFirstObject().getVertices();
		List<Vector3D> endPoints = transformationObject.getSecondObject().getVertices();
		for (int i = 0; i < startPoints.size(); i++) {
			boids.add(new Boid(path, startPoints.get(i).copy(), endPoints.get(i).copy(), r, texture.getnFrames()));
		}
		this.kdTree = new KDTree(treeDepth, boids);
	}

	public void update() {
		kdTree = new KDTree(treeDepth, boids);
		for (Boid boid : boids) {
			boid.updatePosition(this);
		}
		nFrames++;
	}

	public ArrayList<Boid> getBoids() {
		return boids;
	}
	
	public List<Boid> getNearBoids(Boid b) {
		return kdTree.getNearBoids(b);
	}

	public Path getPath() {
		return path;
	}
	
	public void switchAnimationMode() {
		if (animationMode == AnimationMode.ANIM_START) {
			animationMode = AnimationMode.ANIM_PATH_FOLLOWING;
		} else if (animationMode == AnimationMode.ANIM_PATH_FOLLOWING) {
			animationMode = AnimationMode.ANIM_END;
		} else {
			animationMode = AnimationMode.ANIM_START;
		}
	}

	public KDTree getKdTree() {
		return kdTree;
	}
	
	public void switchShowBorders() {
		showTreeBorders = !showTreeBorders;
	}

	public boolean shouldShowTreeBorders() {
		return showTreeBorders;
	}
	
	public void switchShowTrace() {
		showTrace = !showTrace;
	}

	public boolean shouldShowTrace() {
		return showTrace;
	}

	public Vector3D getPredator() {
		return predator;
	}

	public void setPredator(Vector3D predator) {
		this.predator = predator;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public long getnFrames() {
		return nFrames;
	}
}
