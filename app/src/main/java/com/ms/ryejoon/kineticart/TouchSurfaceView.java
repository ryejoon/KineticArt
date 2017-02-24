package com.ms.ryejoon.kineticart;

import jinngine.math.Vector3;
import jinngine.rendering.Rendering;
import com.ms.ryejoon.kineticart.CubeRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class TouchSurfaceView extends GLSurfaceView{
	boolean dragging1 = false;
	boolean dragging2 = false;

    public TouchSurfaceView(Context context) {
        super(context);
    }

    @Override public boolean onTrackballEvent(MotionEvent e) {
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        currentX[0] = e.getX(0);
        currentY[0] = e.getY(0);
    	currentX[1] = e.getX(1); 
    	currentY[1] = e.getY(1);
    	float xPrevDistance;
    	float yPrevDistance;
    	float xCurrentDistance;
    	float yCurrentDistance;
    	
        Vector3 p;
        Vector3 d;
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:/*
            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
            requestRender();*/
        	if(dragging2){
        		xPrevDistance = Math.abs(mPreviousX[1] - mPreviousX[0]);
        		yPrevDistance = Math.abs(mPreviousY[1] - mPreviousY[0]);
        		xCurrentDistance = Math.abs(currentX[1] - currentX[0]);
        		yCurrentDistance = Math.abs(currentY[1] - currentY[0]);
        		mRenderer.changeCameraDistance((xPrevDistance + yPrevDistance) - (xCurrentDistance + yCurrentDistance));       		
        		
        	}else{
            	if(mRenderer.isRightVerge(currentX[0])){
            		mRenderer.XRotateCamera(currentY[0] - mPreviousY[0]);           		
            	}else if(mRenderer.isDownVerge(currentY[0])){
            		mRenderer.YRotateCamera(currentX[0] - mPreviousX[0]);     
            	}else{
            		p = new Vector3();
            		d = new Vector3();
            		mRenderer.getPointerRay(p, d, currentX[0], currentY[0]);		
            		eventCallback.mouseDragged(currentX[0], currentY[0], p, d );
            	}
            }
            break;
        case MotionEvent.ACTION_DOWN:
        	p = new Vector3();
        	d = new Vector3();
        	//ī�޶� ���忡�� ���� ��ǥ��, �������� �������� point�� direction���� ������ �Լ��ε�?
        	mRenderer.getPointerRay(p, d, currentX[0], currentY[0]);        	
        	//p = point, d = direction 
        	eventCallback.mousePressed(currentX[0], currentY[0], p, d );
        	mRenderer.drawLine(p, p.add(d.multiply(10)));
        	break;

        case MotionEvent.ACTION_UP:
        	eventCallback.mouseReleased();
        	break;
        case MotionEvent.ACTION_POINTER_1_DOWN:
        	dragging2 = true;
    		break;
    		
        case MotionEvent.ACTION_POINTER_1_UP:
        	dragging2 = false;
    		break;
    		
        case MotionEvent.ACTION_POINTER_2_DOWN:	
        	dragging2 = true;
    		break;
    		
        case MotionEvent.ACTION_POINTER_2_UP:
        	dragging2 = false;        	
    		break;
    	}
        mPreviousX[0] = currentX[0];
        mPreviousY[0] = currentY[0];
        mPreviousX[1] = currentX[1];
        mPreviousY[1] = currentY[1];
        return true;
    }
    
    public static void setCallback(Rendering.EventCallback ec){
    	eventCallback = ec;
    }
    
    public void setRenderer(CubeRenderer cr){
    	super.setRenderer(cr);
    	mRenderer = cr;
    	setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private CubeRenderer mRenderer;
    private float[] mPreviousX = new float[2];
    private float[] mPreviousY = new float[2];
    private float[] currentX = new float[2];
    private float[] currentY = new float[2];
    private static Rendering.EventCallback eventCallback;

}
