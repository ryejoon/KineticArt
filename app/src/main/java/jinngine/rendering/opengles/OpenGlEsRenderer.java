/**
 * Copyright (c) 2008-2010  Morten Silcowitz.
 *
 * This file is part of the Jinngine physics library
 *
 * Jinngine is published under the GPL license, available 
 * at http://www.gnu.org/copyleft/gpl.html. 
 */
package jinngine.rendering.opengles;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable.Callback;
import android.opengl.*;
import android.view.inputmethod.InputMethodSession.EventCallback;


import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.*;

import jinngine.examples.MobileExample;
import jinngine.geometry.Box;
import jinngine.geometry.ConvexHull;
import jinngine.geometry.Geometry;
import jinngine.geometry.UniformCapsule;
import jinngine.math.Matrix4;
import jinngine.math.Vector3;
import jinngine.physics.Body;
import jinngine.rendering.Rendering;

public class OpenGlEsRenderer implements GLSurfaceView.Renderer  {

	private static final String TAG = "GLRenderer";
	private final Context context;
	private long startTime;
	private long fpsStartTime;
	private long numFrames;
	
	
	
	private static final long serialVersionUID = 1L;
	public List<Geometry> toDraw = new ArrayList<Geometry>();
	private double width;
	private double height;
	private double drawHeight;
	//private final Vector3 cameraTo = new Vector3(-12,-3,0).multiply(1);	
	private final Vector3 cameraTo = new Vector3(0,0,0);	
	private Vector3 cameraFrom = cameraTo.add(new Vector3(0,0.5,3).multiply(5));
	//camera transform
	public double[] proj = new double[16];
	public double[] camera = new double[16];
	public double zoom = 0.95;

	public OpenGlEsRenderer(Context context){
		this.context = context;
    	//new MobileExample(this);
	}
	
	public void drawMe(final Geometry g) {
		if ( g instanceof Box  ) {
			toDraw.add(g);					
		}

	}
	
	private ConvexHull buildIcosphere(double r, int depth) {
		final List<Vector3> vertices = new ArrayList<Vector3>();
//		vertices.add(new Vector3( 1, 1, 1).normalize());
//		vertices.add(new Vector3(-1,-1, 1).normalize());
//		vertices.add(new Vector3(-1, 1,-1).normalize());
//		vertices.add(new Vector3( 1,-1,-1).normalize());
		// point on icosahedron
		final double t = (1.0 + Math.sqrt(5.0))/ 2.0;
		vertices.add(new Vector3(-1,  t,  0).normalize());
		vertices.add( new Vector3( 1,  t,  0).normalize());
		vertices.add( new Vector3(-1, -t,  0).normalize());
		vertices.add( new Vector3( 1, -t,  0).normalize());
		vertices.add( new Vector3( 0, -1,  t).normalize());
		vertices.add( new Vector3( 0,  1,  t).normalize());
		vertices.add( new Vector3( 0, -1, -t).normalize());
		vertices.add( new Vector3( 0,  1, -t).normalize());
		vertices.add( new Vector3( t,  0, -1).normalize());
		vertices.add( new Vector3( t,  0,  1).normalize());
		vertices.add( new Vector3(-t,  0, -1).normalize());
		vertices.add( new Vector3(-t,  0,  1).normalize());

		int n = 0;
		while (true) {
			ConvexHull hull = new ConvexHull(vertices);

			if (n>=depth)
				return hull;

			// for each face, add a new sphere support 
			// point in direction of the face normal
			Iterator<Vector3[]> iter = hull.getFaces();
			while(iter.hasNext()) {
				Vector3[] face = iter.next();
				Vector3 normal =face[1].sub(face[0]).cross(face[2].sub(face[1])).normalize();
				vertices.add(new Vector3(normal));
			}
			
			// depth level done
			n++;
		}
	}

	
	private Matrix4 getCameraMatrix() {
		return new Matrix4(camera);
	}

	private Matrix4 getProjectionMatrix() {		
		return new Matrix4(proj);
	}
	/**
	 * ī�޶��� ���忡�� ���� ��ǥ��, �������� ���� ������ ������ ������ �Լ��� ��?
	 * @param p
	 * @param d
	 * @param x
	 * @param y
	 */
	public void getPointerRay(Vector3 p, Vector3 d, double x, double y) {
		// clipping planes
		Vector3 near = new Vector3(2*x/(double)width-1,-2*(y-((height-drawHeight)*0.5))/(double)drawHeight+1, 0.7);
		Vector3 far = new Vector3(2*x/(double)width-1,-2*(y-((height-drawHeight)*0.5))/(double)drawHeight+1, 0.9);

		//inverse transform
		Matrix4 T = getProjectionMatrix().multiply(getCameraMatrix()).inverse();
	
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		
		Matrix4.multiply(T,near,p1);
		Matrix4.multiply(T,far,p2);
		
		p.assign(p1);
		d.assign(p2.sub(p1).normalize());
	}
	
	/**
	 * This is where the "magic" is done:
	 *
	 * Multiply the current ModelView-Matrix with a shadow-projetion
	 * matrix.
	 *
	 * l is the position of the light source
	 * e is a point on within the plane on which the shadow is to be
	 *   projected.  
	 * n is the normal vector of the plane.
	 *
	 * Everything that is drawn after this call is "squashed" down
	 * to the plane. Hint: Gray or black color and no lighting 
	 * looks good for shadows *g*
	 */
	private double[] shadowProjectionMatrix(Vector3 l, Vector3 e, Vector3  n)
	{
	  double d, c;
	  double[] mat = new double[16];

	  // These are c and d (corresponding to the tutorial)
	  
	  d = n.x*l.x + n.y*l.y + n.z*l.z;
	  c = e.x*n.x + e.y*n.y + e.z*n.z - d;

	  // Create the matrix. OpenGL uses column by column
	  // ordering

	  mat[0]  = l.x*n.x+c; 
	  mat[4]  = n.y*l.x; 
	  mat[8]  = n.z*l.x; 
	  mat[12] = -l.x*c-l.x*d;
	  
	  mat[1]  = n.x*l.y;        
	  mat[5]  = l.y*n.y+c;
	  mat[9]  = n.z*l.y; 
	  mat[13] = -l.y*c-l.y*d;
	  
	  mat[2]  = n.x*l.z;        
	  mat[6]  = n.y*l.z; 
	  mat[10] = l.z*n.z+c; 
	  mat[14] = -l.z*c-l.z*d;
	  
	  mat[3]  = n.x;        
	  mat[7]  = n.y; 
	  mat[11] = n.z; 
	  mat[15] = -d;

	  return mat;
	}


	public void getCamera(Vector3 from, Vector3 to) {
		from.assign(cameraFrom);
		to.assign(cameraTo);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config){
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
         gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                 GL10.GL_FASTEST);

         gl.glClearColor(0,0,0,0);
         
         gl.glEnable(GL10.GL_CULL_FACE);
         gl.glShadeModel(GL10.GL_SMOOTH);
         gl.glEnable(GL10.GL_DEPTH_TEST);
		  
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height){

        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		//GLU.gluLookAt(gl, (int)cameraFrom.x, (int)cameraFrom.y, (int)cameraFrom.z, (int)cameraTo.x, (int)cameraTo.y, (int)cameraTo.z, 0, 1, 0);
        
	}
	private float mAngle=0;
	public void onDrawFrame(GL10 gl){
        /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear().
         */

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -3.0f);
        gl.glRotatef(mAngle,        0, 1, 0);
        gl.glRotatef(mAngle*0.25f,  1, 0, 0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        


        
        //��� �������� �����س��� ����. ���߿�        
        Iterator it = toDraw.iterator();
        Geometry g;
        Vector3 pos;

    	g = (Box)it.next();
    	//gl.glTranslatef((float)pos.x, (float)pos.y, (float)pos.z);
    	
        gl.glRotatef(mAngle*2.0f, 0, 1, 1);

        while(it.hasNext()){
        	g = (Geometry)it.next();
        	pos = g.getBody().getPosition();
        	//gl.glTranslatef((float)pos.x, (float)pos.y, (float)pos.z);
        	
        	g.draw(gl);       	
        	
        }

        mAngle += 1.2f;
        
	}
}
