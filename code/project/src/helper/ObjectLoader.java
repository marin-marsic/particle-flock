package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.jogamp.opengl.GL;

import model.SceneObject;
import model.TransformationObject;
import model.Triangle;
import vector.Vector3D;

public class ObjectLoader {
	
	private static Random random = new Random(System.currentTimeMillis());
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TransformationObject loadObjects(int numOfPoints, String firstFile, String secondFile) {
		SceneObject so1 = loadFromFile(numOfPoints, firstFile);
		SceneObject so2 = loadFromFile(numOfPoints, secondFile);
		return new TransformationObject(so1, so2);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static SceneObject loadFromFile(int numOfPoints, String filename) {
		File file = new File(filename);
		List<Vector3D> vertices = new ArrayList();
		List<Triangle> triangles = new ArrayList();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("v")) {
					String[] lineSeparated = line.split("\\s+");
					double x = Double.parseDouble(lineSeparated[1]);
					double y = -Double.parseDouble(lineSeparated[2]);
					double z = Double.parseDouble(lineSeparated[3]);
					Vector3D t = new Vector3D(x, y, z);
					vertices.add(t);
				} else if (line.startsWith("f")) {
					String[] lineSeparated = line.split("\\s+");
					int v1 = Integer.parseInt(lineSeparated[1]) - 1;
					int v2 = Integer.parseInt(lineSeparated[2]) - 1;
					int v3 = Integer.parseInt(lineSeparated[3]) - 1;
					Triangle t = new Triangle(v1, v2, v3);
					triangles.add(t);
				} else {
				}
			}
			vertices = normalize(vertices);
			if (vertices.size() > numOfPoints) {
				List<Vector3D> list = samplePointsFromTriangles(numOfPoints, vertices, triangles);
				return new SceneObject(list, null);
			} else {
				vertices.addAll(samplePointsFromTriangles(numOfPoints - vertices.size(), vertices, triangles));
				return new SceneObject(vertices, null);
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("Error reading file.");
		}
		return null;
	}
	
	private static List<Vector3D> samplePointsFromTriangles(int numOfPoints, List<Vector3D> vertices, List<Triangle> triangles) {
		List<Vector3D> list = new ArrayList();
		double decimalFactor = triangles.size() / (double) numOfPoints;
		int factor = triangles.size() / numOfPoints;
		int counter = 1;
		if (factor > 0) {
			while (counter * decimalFactor < numOfPoints) {
				int rTrinagle = random.nextInt(factor) + (int) ((counter - 1) * decimalFactor);
				Triangle t = triangles.get(rTrinagle);
				Vector3D point = sampleTriangle(t, vertices);
				list.add(point);
				counter++;
			}
		}
		int listSize = list.size();
		for (int i = 0; i < numOfPoints - listSize; i++) {
			int rTrinagle = random.nextInt(triangles.size());
			Triangle t = triangles.get(rTrinagle);
			Vector3D point = sampleTriangle(t, vertices);
			list.add(point);
		}
		return list;
	}

	private static Vector3D sampleTriangle(Triangle t, List<Vector3D> vertices) {
		double r1 = random.nextDouble();
		double r2 = random.nextDouble();
		Vector3D a = vertices.get(t.getV1());
		Vector3D b = vertices.get(t.getV2());
		Vector3D c = vertices.get(t.getV3());
		double x = (1 - Math.sqrt(r1)) * a.getX() + (Math.sqrt(r1) * (1 - r2)) * b.getX() + (Math.sqrt(r1) * r2) * c.getX();
		double y = (1 - Math.sqrt(r1)) * a.getY() + (Math.sqrt(r1) * (1 - r2)) * b.getY() + (Math.sqrt(r1) * r2) * c.getY();
		double z = 0;
		
		return new Vector3D(x, y, z);
	}

	private static List<Vector3D> normalize(List<Vector3D> vertices) {
		double xMin, xMax, yMin, yMax, zMin, zMax;
		
		xMin = xMax = vertices.get(0).getX();
		yMin = yMax = vertices.get(0).getY();
		zMin = zMax = vertices.get(0).getZ();

		// find minimum and maximum values
		int nVertices = vertices.size();
		for (int i = 1; i < nVertices; i++) {
			Vector3D v = vertices.get(i);
			if (xMin > v.getX()) {
				xMin = v.getX();
			}
			if (xMax < v.getX()) {
				xMax = v.getX();
			}
			if (yMin > v.getY()) {
				yMin = v.getY();
			}
			if (yMax < v.getY()) {
				yMax = v.getY();
			}
			if (zMin > v.getZ()) {
				zMin = v.getZ();
			}
			if (zMax < v.getZ()) {
				zMax = v.getZ();
			}
		}

		// center of an object
		double xS, yS, zS;
		xS = (xMin + xMax) / 2;
		yS = (yMin + yMax) / 2;
		zS = (zMin + zMax) / 2;
		
		Vector3D center = new Vector3D(xS, yS,  zS);

		// maximum range
		double M = Math.max(Math.abs(xMax - xMin), Math.abs(yMax - yMin));
		M = Math.max(M, Math.abs(zMax - zMin));

		// translation and scaling
		List<Vector3D> newVertices = new ArrayList();
		for (Vector3D v : vertices) {
			double x = ((v.getX() - xS) * 400 / M) + 600;
			double y = ((v.getY() - yS) * 400 / M) + 400;
			double z = 0;
			newVertices.add(new Vector3D(x, y, z));
		}
		return newVertices;
	}
}
