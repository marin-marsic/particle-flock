package texture;

import com.jogamp.opengl.util.texture.Texture;

import vector.Vector3D;

public class ParticleTexture {

	static final int N_ROWS = 4;
	static final int N_COLS = 4;
	
	static final double ROW_FACTOR = 1.0 / N_ROWS;
	static final double COL_FACTOR = 1.0 / N_COLS;

	private Texture texture;
	private int nFrames;

	public ParticleTexture(Texture texture, int nFrames) {
		this.texture = texture;
		this.nFrames = nFrames;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public int getnFrames() {
		return nFrames;
	}

	public void setnFrames(int nFrames) {
		this.nFrames = nFrames;
	}

	public Vector3D getBottomLeftCoord(int frame) {
		double x = COL_FACTOR * (frame % N_ROWS);
		double y  = (1 - ROW_FACTOR) - (ROW_FACTOR * (frame / N_COLS));
		return new Vector3D(x, y, 0);
	}
	
	public Vector3D getBottomRightCoord(int frame) {
		Vector3D v = getBottomLeftCoord(frame);
		v.add(new Vector3D(COL_FACTOR, 0, 0));
		return v;
	}
	
	public Vector3D getTopLeftCoord(int frame) {
		Vector3D v = getBottomLeftCoord(frame);
		v.add(new Vector3D(0, ROW_FACTOR, 0));
		return v;
	}
	
	public Vector3D getTopRightCoord(int frame) {
		Vector3D v = getBottomLeftCoord(frame);
		v.add(new Vector3D(COL_FACTOR, ROW_FACTOR, 0));
		return v;
	}
}
