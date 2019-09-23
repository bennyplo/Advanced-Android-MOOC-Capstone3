package com.bennyplo.capstone3;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class MyView  extends GLSurfaceView {
    private final MyRenderer mRenderer;
    public MyView(Context context) {
        super(context);
        setEGLContextClientVersion(2);// Create an OpenGL ES 2.0 context.
        mRenderer = new MyRenderer();// Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        Timer timer=new Timer();
        final MyView thisview=this;
        TimerTask task= new TimerTask() {
            float pxangle=0;
            float pyangle=0;
            float pzangle=0;
            @Override
            public void run() {
                mRenderer.setXAngle(pxangle); //spining about the y-axis
                mRenderer.setYAngle(pyangle); //spining about the y-axis
                requestRender();
                pyangle+=1;//spining about the y-axis
                if (pyangle>=360)pyangle=0;
                pxangle++;//rotate about the x-axis
                if (pxangle>=360)pxangle=0;
                pzangle++;//rotate about the z-axis
                if (pzangle>=360)pzangle=0;
            }
        };
        timer.scheduleAtFixedRate(task,1000,100);
    }
}
