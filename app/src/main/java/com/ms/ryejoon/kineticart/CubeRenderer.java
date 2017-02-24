/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ms.ryejoon.kineticart;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jinngine.examples.MobileExample;
import jinngine.geometry.Box;
import jinngine.geometry.Geometry;
import jinngine.math.Matrix4;
import jinngine.math.Vector3;
import jinngine.physics.DefaultScene;
import jinngine.rendering.Rendering.Callback;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;


/**
 * Render a pair of tumbling cubes.
 */

public class CubeRenderer implements GLSurfaceView.Renderer {
	public List<Box> toDraw = new ArrayList<Box>();
	private double width;
	private double height;
	private double drawHeight;
	//private final Vector3 cameraTo = new Vector3(-12,-3,0).multiply(1);	
	private final Vector3 cameraTo = new Vector3(0,0,0);	
	private Vector3 cameraFrom = cameraTo.add(new Vector3(0,0,5));
	
	//camera transform
	public float[] proj = new float[16];
	public float[] cameraFloat = new float[16];
	public double zoom = 0.95;
	
	
	private MatrixGrabber mg = new MatrixGrabber();
	private MatrixTrackingGL mgl;
	private Cube mCube = new Cube();
	private float cameraYRotateTheta = 0;
	private float cameraXRotateTheta = 0;
	private float distance = 5;
	
	private FloatBuffer lineList; 
	
    public CubeRenderer(boolean useTranslucentBackground) {
        mTranslucentBackground = useTranslucentBackground;
        callback = new MobileExample(this);
        mCube = new Cube();
    }

    

	private Matrix4 getCameraMatrix() {
		return new Matrix4(cameraFloat);
	}

	private Matrix4 getProjectionMatrix() {		
		return new Matrix4(proj);
	}
	/**
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
	public void getCamera(Vector3 from, Vector3 to) {
		from.assign(cameraFrom);
		to.assign(cameraTo);
	}
	
    public void onDrawFrame(GL10 gl) {
    	synchronized(DefaultScene.monitor){
    	callback.tick();
    	
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	mgl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, (float) cameraFrom.x, (float) cameraFrom.y, (float) cameraFrom.z, 0, 0, 0, 0, 1, 0);
        
        mgl.glMatrixMode(GL10.GL_MODELVIEW);
        mgl.glLoadIdentity();
        GLU.gluLookAt(mgl, (float) cameraFrom.x, (float) cameraFrom.y, (float) cameraFrom.z, 0, 0, 0, 0, 1, 0);
         
        mg.getCurrentModelView(mgl);
        cameraFloat = mg.mModelView.clone();
        
        gl.glTranslatef(0, 0, -3.0f);
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        
        
        //���⼭���� �׸��� ��
        
        
        Iterator<Box> it = toDraw.iterator();
        Box g;
        Vector3 pos;
        float angle = 0;
        Vector3 axis = new Vector3(0, 0, 0);
        while(it.hasNext()){
        	g = (Box)it.next();
        	pos = g.getBody().getPosition();
        	angle = g.getBody().state.orientation.getAxisAngle(axis);
        	gl.glTranslatef((float)pos.x, (float)pos.y, (float)pos.z);
        	//ù��° ���� - ȸ���ϴ� ����, �ι�° ����, ȸ���ϴ� ���� ���⺤�͸� �ǹ��ϴ� ��...
        	gl.glRotatef(angle, (float)axis.x, (float)axis.y, (float)axis.z);

        	g.draw(gl);        	
        	
        	gl.glTranslatef(-(float)pos.x, -(float)pos.y, -(float)pos.z);
        	gl.glRotatef(-angle, -(float)axis.x, -(float)axis.y, -(float)axis.z);
        	
        }
//        if(lineList!=null){
//        	ByteBuffer lineIndex;
//        	byte[] lineIndices = {0,1};
//        	lineIndex = ByteBuffer.allocateDirect(lineIndices.length);
//        	lineIndex.put(lineIndices);
//        	gl.glVertexPointer(3, gl.GL_FLOAT, 0, lineList);
//        	gl.glDrawElements(gl.GL_LINES, 2, gl.GL_UNSIGNED_BYTE, lineIndex);
//        }
    	}
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	this.height = height;
    	this.width = width;
    	this.drawHeight = height;

        mgl = new MatrixTrackingGL(gl);
        
    	 gl.glViewport(0, 0, width, height);
    	 mgl.glViewport(0, 0, width, height);

         /*
          * Set our projection matrix. This doesn't have to be done
          * each time we draw, but usually a new projection needs to
          * be set when the viewport is resized.
          */

         float ratio = (float) width / height;
         gl.glMatrixMode(GL10.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1, 20);
         

         mgl.glMatrixMode(GL10.GL_PROJECTION);
         mgl.glLoadIdentity();
         mgl.glFrustumf(-ratio, ratio, -1, 1, 1, 20);
                  
         mg.getCurrentProjection(mgl);
         proj = mg.mProjection.clone();         
         
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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

         if (mTranslucentBackground) {
             gl.glClearColor(0,0,0,0);
         } else {
             gl.glClearColor(1,1,1,1);
         }
         gl.glEnable(GL10.GL_CULL_FACE);
         gl.glShadeModel(GL10.GL_SMOOTH);
         gl.glEnable(GL10.GL_DEPTH_TEST);
    }
    
    public void addToDraw(Geometry g){
		if ( g instanceof Box  ) {
			toDraw.add((Box) g);					
		}
    }
    
    
    public boolean isDownVerge(float YPoint){
    	if(YPoint > height * 0.9){
    		return true;   		
    	}    	
    	return false;
    }
    
    public boolean isRightVerge(float XPoint){
    	if(XPoint > width * 0.9){
    		return true;   		
    	}    	
    	return false;
    }
    
    public void YRotateCamera(float xChange){
    	cameraYRotateTheta += xChange * 5 / width;    
    	applyCameraMove();
    }
    
    public void XRotateCamera(float yChange){
    	cameraXRotateTheta += yChange * 5 / height;
    	applyCameraMove();    	
    }
    
    public void applyCameraMove(){
    	cameraFrom.x = (distance * Math.cos(cameraXRotateTheta)) * Math.cos(cameraYRotateTheta);
    	cameraFrom.y = distance * Math.sin(cameraXRotateTheta);
    	cameraFrom.z = (distance * Math.cos(cameraXRotateTheta)) * Math.sin(cameraYRotateTheta);
     }
    
    public void changeCameraDistance(float amount){
    	distance += amount * 0.01;
    	applyCameraMove();
    }
    
    public void drawLine(Vector3 p1, Vector3 p2){
        float[] vertices = {(float)p1.x, (float)p1.y, (float)p1.z, 
    				(float)p2.x, (float)p2.y, (float)p2.z};
    	ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
    	vbb.order(ByteOrder.nativeOrder());
        lineList = vbb.asFloatBuffer();
        lineList.put(vertices);
        lineList.position(0);   	
    	    	
    }
    

    public float mAngle = 0;
    private boolean mTranslucentBackground;
    private Callback callback;
	public float mAngleX;
	public float mAngleY;
}
