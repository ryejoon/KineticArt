package com.ms.ryejoon.kineticart;

import jinngine.examples.MobileExample;
import jinngine.rendering.opengles.OpenGlEsRenderer;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
	
    private TouchSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create our Preview view and set it as the content of our
        // Activity
        mGLSurfaceView = new TouchSurfaceView(this);
        CubeRenderer mRenderer = new CubeRenderer(true);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(mRenderer);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(mGLSurfaceView);
    }


    protected void onPuase(){ 
    	super.onPause();
    	mGLSurfaceView.onPause();
    }

    protected void onResume(){
    	super.onResume();
    	mGLSurfaceView.onResume();
    }
    
}