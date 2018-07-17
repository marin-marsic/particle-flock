package model;

public class TransformationObject {

	private SceneObject firstObject, secondObject;

	public TransformationObject(SceneObject firstObject, SceneObject secondObject) {
		this.firstObject = firstObject;
		this.secondObject = secondObject;
	}

	public SceneObject getFirstObject() {
		return firstObject;
	}

	public SceneObject getSecondObject() {
		return secondObject;
	}
}
