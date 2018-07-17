package projekt;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import helper.ObjectLoader;
import model.Line;
import model.SceneObject;
import model.TransformationObject;
import texture.ParticleTexture;
import tree.KDTree;
import vector.Vector3D;

import javax.script.ScriptEngineManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {

	static {
		GLProfile.initSingleton();
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			Texture textureBoid, textureSky;
			long millis = System.currentTimeMillis();

			@Override
			public void run() {
				GLProfile glprofile = GLProfile.getDefault();
				GLCapabilities glcapatibilities = new GLCapabilities(glprofile);
				final GLCanvas glcanvas = new GLCanvas(glcapatibilities);
				TransformationObject to = ObjectLoader.loadObjects(8000, args[0], args[1]);		// args = "bull.obj", "frog.obj"
				Environment environment = new Environment(8, 640, 480, new ParticleTexture(textureBoid, 15));
				environment.setBoids(to);

				// mouse action
				glcanvas.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						environment.input(e.getX(), e.getY(), 0);
						glcanvas.display();
					}
				});

				glcanvas.addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						environment.setPredator(new Vector3D(e.getX(), e.getY(), 0));
					}
				});

				// key press
				glcanvas.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_A) {
							environment.switchAnimationMode();
						}
						if (e.getKeyCode() == KeyEvent.VK_B) {
							environment.switchShowBorders();
						}
						if (e.getKeyCode() == KeyEvent.VK_P) {
							environment.switchMode();
						}
						if (e.getKeyCode() == KeyEvent.VK_S) {
							millis = System.currentTimeMillis();
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {
									while (true) {
										environment.update();
										glcanvas.display();
									}
								}
							});
							t.start();
						}
						if (e.getKeyCode() == KeyEvent.VK_T) {
							environment.switchShowTrace();
						}
						if (e.getKeyCode() == KeyEvent.VK_R) {
							environment.reset();
						}
						if (e.getKeyCode() == KeyEvent.VK_F) {
							System.out.println(environment.getnFrames());
							System.out.println((System.currentTimeMillis() - millis) / 1000);
							System.out.println(((double)environment.getnFrames()) / ((System.currentTimeMillis() - millis) / 1000) );
							System.out.println("===========================");
						}
					}
				});

				glcanvas.addGLEventListener(new GLEventListener() {

					@Override
					public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
						GL2 gl2 = glautodrawable.getGL().getGL2();
						gl2.glMatrixMode(GL2.GL_PROJECTION);
						gl2.glLoadIdentity();

						GLU glu = new GLU();
						glu.gluOrtho2D(0.0f, width, 0.0f, height);

						gl2.glMatrixMode(GL2.GL_MODELVIEW);
						gl2.glLoadIdentity();

						gl2.glViewport(0, 0, width, height);
						environment.width = width;
						environment.height = height;
					}

					@Override
					public void init(GLAutoDrawable arg0) {
						try {
							InputStream stream = getClass().getResourceAsStream("light-particle2.png");
							textureBoid = TextureIO.newTexture(stream, false, "png");
							stream = getClass().getResourceAsStream("sky.jpg");
							textureSky = TextureIO.newTexture(stream, false, "jpg");
						} catch (IOException exc) {
							exc.printStackTrace();
							System.exit(1);
						}
					}

					@Override
					public void dispose(GLAutoDrawable arg0) {
					}

					@Override
					public void display(GLAutoDrawable glautodrawable) {
						GL2 gl2 = glautodrawable.getGL().getGL2();
						gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
						int width = glautodrawable.getSurfaceWidth();
						int height = glautodrawable.getSurfaceHeight();
						gl2.glOrtho(0, width - 1, 0, height - 1, 0, 1);

						gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

						gl2.glLoadIdentity();
						gl2.glPointSize(1.0f);
						
//						gl2.glEnable(GL.GL_BLEND);
//						gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//						textureSky.enable(gl2);
//						textureSky.bind(gl2);
//						gl2.glColor4d(1.0, 1.0, 1.0, 1);
//						gl2.glBegin(GL2.GL_QUADS);
//						gl2.glTexCoord2f(0.0f, 0.0f);
//						gl2.glVertex3d(0, height, 0);
//						gl2.glTexCoord2f(0.0f, 1.0f);
//						gl2.glVertex3d(0, 0, 0);
//						gl2.glTexCoord2f(1.0f, 1.0f);
//						gl2.glVertex3d(width, 0, 0);
//						gl2.glTexCoord2f(1.0f, 0.0f);
//						gl2.glVertex3d(width, height, 0);
//						gl2.glEnd();
//						gl2.glDisable(GL.GL_BLEND);
//						gl2.glDepthMask(true);
//						textureSky.disable(gl2);

						gl2.glEnable(GL.GL_BLEND);
						gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
						textureBoid.enable(gl2);
						textureBoid.bind(gl2);
						gl2.glColor4d(0.0, 0.0, 0.0, 0.7);
						gl2.glPointSize(3);
						gl2.glBegin(GL2.GL_QUADS);
						for (Boid b : environment.getBoids()) {
							Vector3D v = b.getPosition();
							// gl2.glVertex3d(v.getX(), height - v.getY(), v.getZ());
							gl2.glTexCoord2f(0.0f, 0.0f);
							double d = 3;
							gl2.glVertex3d(v.getX() - d, height - v.getY() - d, v.getZ());
							// gl2.glVertex3d(-d, 0, -d);

							gl2.glTexCoord2f(1.0f, 0.0f);
							gl2.glVertex3d(v.getX() + d, height - v.getY() - d, v.getZ());
							// gl2.glVertex3d(d, 0, -d);

							gl2.glTexCoord2f(1.0f, 1.0f);
							gl2.glVertex3d(v.getX() + d, height - v.getY() + d, v.getZ());
							// gl2.glVertex3d(d, 0, d);

							gl2.glTexCoord2f(0.0f, 1.0f);
							gl2.glVertex3d(v.getX() - d, height - v.getY() + d, v.getZ());
						}
						gl2.glEnd();
						gl2.glDisable(GL.GL_BLEND);
						gl2.glDepthMask(true);
						textureBoid.disable(gl2);

						gl2.glColor3f(0, 0, 1);
						gl2.glPointSize(1);
						gl2.glBegin(GL.GL_POINTS);
						for (Vector3D v : environment.getPath().getNodes()) {
							gl2.glVertex3d(v.getX(), height - v.getY(), v.getZ());
						}
						gl2.glEnd();

						if (environment.shouldShowTreeBorders()) {
							gl2.glColor3f(1, 0, 0);
							gl2.glPointSize(1);
							gl2.glBegin(GL.GL_LINES);
							for (Line line : environment.getKdTree().getBorders()) {
								gl2.glVertex3d(line.getStart().getX(), height - line.getStart().getY(),
										line.getStart().getZ());
								gl2.glVertex3d(line.getEnd().getX(), height - line.getEnd().getY(),
										line.getEnd().getZ());
							}
							gl2.glEnd();
						}

					}
				});

				final JFrame jframe = new JFrame("Boids");
				jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				jframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						jframe.dispose();
						System.exit(0);
					}
				});

				jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
				jframe.setSize(environment.width, environment.height);
				jframe.setVisible(true);
				glcanvas.requestFocusInWindow();
			}

		});
	}
}