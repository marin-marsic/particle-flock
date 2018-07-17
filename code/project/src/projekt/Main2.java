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
import model.Rectangle;
import model.SceneObject;
import model.TransformationObject;
import texture.ParticleTexture;
import tree.KDTree;
import vector.Vector3D;

import javax.script.ScriptEngineManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main2 {

	static {
		GLProfile.initSingleton();
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			Texture texture2;

			@Override
			public void run() {
				GLProfile glprofile = GLProfile.getDefault();
				GLCapabilities glcapatibilities = new GLCapabilities(glprofile);
				final GLCanvas glcanvas = new GLCanvas(glcapatibilities);
				TransformationObject to = ObjectLoader.loadObjects(40, args[0], args[1]);		// args = "bull.obj", "frog.obj"
				Environment environment = new Environment(1, 640, 480, new ParticleTexture(texture2, 15));
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
							InputStream stream2 = getClass().getResourceAsStream("butterfly.png");
							texture2 = TextureIO.newTexture(stream2, false, "png");
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

						gl2.glEnable(GL.GL_BLEND);
						gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
						// texture.enable(gl2);
						// texture.bind(gl2);
						texture2.enable(gl2);
						texture2.bind(gl2);
						gl2.glColor4d(1.0, 1.0, 1.0, 0.5);
						gl2.glPointSize(3);
						gl2.glBegin(GL2.GL_QUADS);
						for (Boid b : environment.getBoids()) {
							Rectangle rectangle = b.getPositionRectangl();
							double d = 50;

							// gl2.glVertex3d(v.getX(), height - v.getY(), v.getZ());
							Vector3D texVec1 = environment.getTexture().getBottomLeftCoord(b.getTextureFrame());
							gl2.glTexCoord2d(texVec1.getX(), texVec1.getY());
							Vector3D v1 = rectangle.getBottomRight();
							gl2.glVertex3d(v1.getX(), height - v1.getY(), v1.getZ());
							// gl2.glVertex3d(-d, 0, -d);

							Vector3D texVec2 = environment.getTexture().getBottomRightCoord(b.getTextureFrame());
							gl2.glTexCoord2d(texVec2.getX(), texVec2.getY());
							Vector3D v2 = rectangle.getBottomLeft();
							gl2.glVertex3d(v2.getX(), height - v2.getY(), v2.getZ());
							// gl2.glVertex3d(d, 0, -d);

							Vector3D texVec3 = environment.getTexture().getTopRightCoord(b.getTextureFrame());
							gl2.glTexCoord2d(texVec3.getX(), texVec3.getY());
							Vector3D v3 = rectangle.getTopRight();
							gl2.glVertex3d(v3.getX(), height - v3.getY(), v3.getZ());
							// gl2.glVertex3d(d, 0, d);

							Vector3D texVec4 = environment.getTexture().getTopLeftCoord(b.getTextureFrame());
							gl2.glTexCoord2d(texVec4.getX(), texVec4.getY());
							Vector3D v4 = rectangle.getTopLeft();
							gl2.glVertex3d(v4.getX(), height - v4.getY(), v4.getZ());
						}
						gl2.glEnd();
						gl2.glDisable(GL.GL_BLEND);
						gl2.glDepthMask(true);

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