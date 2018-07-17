package projekt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import model.Rectangle;
import projekt.Environment.AnimationMode;
import vector.Vector3D;

public class Boid {
	
	private static final int RADIUS = 70;
	private static final int TRACE_SIZE = 1000;
	
	private static final double MIN_MAGNITUDE = 100;
	private static final double MAX_MAGNITUDE = 400;
	private static final double VALUE_SCALE_FACTOR = 100;
	private static final double VALUE_SCALE_FACTOR_R = 0.01;
	
	private static final double SPEED_UP_FACTOR = 1.001;
	private static final double SLOW_DOWN_FACTOR = 0.98;
	
	private static final double SPEED_UP_DISTANCE = 700;
	private static final double SLOW_DOWN_DISTANCE = 300;
	
	private static final double DIRECTION_CHANGE_FACTOR = 0.03;
	
	private static final double NEIGHBOURHOOD_RADIUS = 100;
	private static final double TOO_CLOSE_DISTANCE = 35;
	private static final double TEX_SIZE = 25;

	private static final double ALIGNMENT_FACTOR = 2;
	private static final double SEPARATE_FACTOR = 100;
	private static final double COHERE_FACTOR = 2;
	private static final double AVOID_PREDATOR_FACTOR = 1;
	private static final double AVOID_PREDATOR_DISTANCE = 270;
	
	private  int TEXTURE_FRAME_RATE = 1;
	private double f = 1;
	private boolean truncateSpeed = true;

	private Path path;
	private int currentNode;
	private Vector3D position;
	private Vector3D endPoint;
	private Vector3D velocity;
	private int textureFrame, textureCounter;
	
	public Boid(Path path, Vector3D startPoint, Vector3D endPOint, Random r, int nFrames) {
		this.path = path;
		this.position = startPoint;
		this.endPoint = endPOint;
		velocity = new Vector3D(0, 0, 0);
		textureFrame = r.nextInt(nFrames);
		TEXTURE_FRAME_RATE = r.nextInt(1) + 1;
		textureCounter = 0;
	}

	private Vector3D getNodeToFollow(Environment env) {
		if (env.animationMode == AnimationMode.ANIM_START) {
			currentNode = 0;
			return path.getNodes().get(0);
		} else if (env.animationMode == AnimationMode.ANIM_END) {
			return endPoint;
		} else {
			if (currentNode >= path.getNodes().size()-1) {
				f *= 0.9;
				truncateSpeed = false;
				return endPoint;
			} else {
				if (position.distanceTo(path.getNodes().get(currentNode)) <= RADIUS) {
					currentNode++;
				}
				
				return path.getNodes().get(currentNode);
			}
		}
	}
	
	public void updatePosition(Environment env) {
		Vector3D nodeToFollow = getNodeToFollow(env);
		
		if (env.animationMode != AnimationMode.ANIM_END) {
			Vector3D force = nodeToFollow.copy();
			force.subtract(position);
			
			List<Boid> neighbours = getNeighbours(env.getNearBoids(this));
			Vector3D flockingVector = separate(neighbours).scale(f);
			flockingVector.add(avoidPredator(env.getPredator()));
//			flockingVector.add(align(neighbours));
//			flockingVector.add(cohere(neighbours));
			
			force.add(flockingVector);
			
			// scale and truncate vector
			force = force.truncate(MIN_MAGNITUDE, MAX_MAGNITUDE);
			
			double factor = 1;
			if (force.magnitude() < SLOW_DOWN_DISTANCE) {
				factor = SLOW_DOWN_FACTOR;
			} else if (force.magnitude() > SPEED_UP_DISTANCE) {
				factor = SPEED_UP_FACTOR;
			}
			
			Vector3D newVelocity = velocity.copy().scale(VALUE_SCALE_FACTOR * factor);
			newVelocity.add(force.scale(DIRECTION_CHANGE_FACTOR));
			if (truncateSpeed) {
				newVelocity = newVelocity.truncate(MIN_MAGNITUDE, MAX_MAGNITUDE);
			}	
			newVelocity = newVelocity.scale(VALUE_SCALE_FACTOR_R);
			velocity = newVelocity;
		} else {
			Vector3D force = nodeToFollow.copy();
			force.subtract(position);
			
			List<Boid> neighbours = getNeighbours(env.getNearBoids(this));
			Vector3D flockingVector = separate(neighbours).scale(f);
			f *= 0.998;
			flockingVector.add(align(neighbours));
			flockingVector.add(cohere(neighbours));
			
			// scale and truncate vector
			force = force.truncate(MIN_MAGNITUDE, MAX_MAGNITUDE);
			
			double factor = 1;
			if (force.magnitude() < SLOW_DOWN_DISTANCE) {
				factor = SLOW_DOWN_FACTOR;
			} else if (force.magnitude() > SPEED_UP_DISTANCE) {
				factor = SPEED_UP_FACTOR;
			}
			
			Vector3D newVelocity = velocity.copy().scale(VALUE_SCALE_FACTOR * factor);
			newVelocity.add(force.scale(DIRECTION_CHANGE_FACTOR));
			newVelocity = newVelocity.scale(VALUE_SCALE_FACTOR_R);
			velocity = newVelocity;
		}
		
		textureCounter++;
		if (textureCounter % TEXTURE_FRAME_RATE == 0) {
			textureFrame++;
			textureCounter = 0;
			if (textureFrame == env.getTexture().getnFrames()) {
				textureFrame = 1;
			}
		}
		
		// update position
		position.add(velocity);
	}

	public Vector3D getPosition() {
		return position;
	}
	
	public double distanceToOtherBoid(Boid other) {
		return position.distanceTo(other.position);
	}
	
	public ArrayList<Boid> getNeighbours(List<Boid> boids) {
		ArrayList<Boid> neighbours = new ArrayList();

		for (Boid b : boids) {
			double distance = distanceToOtherBoid(b);
			if (distance <= NEIGHBOURHOOD_RADIUS && distance > 0.001) {
				neighbours.add(b);
			}
		}

		return neighbours;
	}
	
	private Vector3D align(List<Boid> neighbours) {
		double vX = 0;
		double vY = 0;
		double vZ = 0;

		for (Boid neighbour : neighbours) {
			vX += neighbour.velocity.getX();
			vY += neighbour.velocity.getY();
			vZ += neighbour.velocity.getZ();
		}

		if (neighbours.size() != 0) {
			double factor = ALIGNMENT_FACTOR / neighbours.size();
			vX *= factor;
			vY *= factor;
			vZ *= factor;
		}
		return new Vector3D(vX, vY, vZ);
	}

	private Vector3D separate(List<Boid> neighbours) {
		double vX = 0;
		double vY = 0;
		double vZ = 0;

		double count = 0;
		double size = neighbours.size();
		for (Boid neighbour : neighbours) {
			double distance = distanceToOtherBoid(neighbour);
			if (distance > 0 && distance <= TOO_CLOSE_DISTANCE) {
				vX += (position.getX() - neighbour.position.getX());
				vY += (position.getY() - neighbour.position.getY());
				vZ += (position.getZ() - neighbour.position.getZ());
				count++;
			}
		}

		if (count != 0) {
			double factor = SEPARATE_FACTOR / neighbours.size();
			vX *= factor;
			vY *= factor;
			vZ *= factor;
		}

		return new Vector3D(vX, vY, vZ);
	}
	
	private Vector3D avoidPredator(Vector3D predator) {
		double distanceToRedator = predator.distanceTo(position);
		if (predator != null && distanceToRedator < AVOID_PREDATOR_DISTANCE) {
			double vX = position.getX() - predator.getX();
			double vY = position.getY() - predator.getY();
			double vZ = position.getZ() - predator.getZ();

			double factor = AVOID_PREDATOR_FACTOR * (AVOID_PREDATOR_DISTANCE - distanceToRedator);
			vX *= factor;
			vY *= factor;
			vZ *= factor;

			return new Vector3D(vX, vY, vZ);
		} 
		
		return new Vector3D(0, 0, 0);
	}

	private Vector3D cohere(List<Boid> neighbours) {
		double vX = 0;
		double vY = 0;
		double vZ = 0;

		double count = 0;
		for (Boid neighbour : neighbours) {
			vX += neighbour.position.getX();
			vY += neighbour.position.getY();
			vZ += neighbour.position.getZ();
			count++;
		}

		if (count != 0) {
			vX /= count;
			vY /= count;
			vZ /= count;
		}

		vX = (vX - position.getX()) * COHERE_FACTOR;
		vY = (vY - position.getY()) * COHERE_FACTOR;
		vZ = (vZ - position.getZ()) * COHERE_FACTOR;

		return new Vector3D(vX, vY, vZ);
	}

	public int getTextureFrame() {
		return textureFrame;
	}
	
	public Rectangle getPositionRectangl() {
		Vector3D velocityN = velocity.copy().normalize().scale(TEX_SIZE);
		double nX = velocityN.getY();
		double nY = -velocityN.getX();
		double vX = velocityN.getX();
		double vY = velocityN.getY();
		
		Vector3D topLeft = new Vector3D(position.getX() - nX + vX, position.getY() - nY + vY, 0);
		Vector3D topRight = new Vector3D(position.getX() + nX + vX, position.getY() + nY + vY, 0);
		Vector3D bottomLeft = new Vector3D(position.getX() + nX - vX, position.getY() + nY - vY, 0);
		Vector3D bottomRight = new Vector3D(position.getX() - nX - vX, position.getY() - nY - vY, 0);
		return new Rectangle(topLeft, topRight, bottomLeft, bottomRight);
	}
}
