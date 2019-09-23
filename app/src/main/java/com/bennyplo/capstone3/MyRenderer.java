package com.bennyplo.capstone3;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer{
    private final float[] mMVPMatrix = new float[16];//model view projection matrix
    private final float[] mProjectionMatrix = new float[16];//projection mastrix
    private final float[] mViewMatrix = new float[16];//view matrix
    private final float[] mMVMatrix=new float[16];//model view matrix
    private final float[] mModelMatrix=new float[16];//model  matrix
    private FloorPlan mfloorPlan;
    private float mYAngle=0;
    private float mXAngle=0;
    private float mZAngle=0;
    private int plotdata[]={1539,1531,1547,1539,1543,1531,1575,1591,1543,1539,1523,1539,1543,1539,1859,2587,1455,1539,1523,1527,1543,1587,1619,1635,1655,1659,1639,1639,1579,1547,1527,1527,1547,1543,1551,1547,1547,1563,1539,1527,1523,1543,1539,1575,1599,1555,1531,1539,1551,1547,1487,1995,2331,1563,1539,1523,1563,1559,1591,1615,1635,1659,1651,1675,1631,1567,1531,1519,1527,1511,1531,1527,1539,1539,1527,1539,1543,1547,1547,1571,1603,1571,1539,1551,1547,1559,1487,1927,2475,1491,1531,1503,1551,1559,1571,1599,1623,1663,1659,1659,1615,1547,1519,1519,1511,1523,1539,1543,1551,1567,1563,1551,1555,1547,1587,1579,1567,1559,1539,1559,1555,1563};

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mfloorPlan=new FloorPlan();
    }
    public static void checkGlError(String glOperation) {
        int error;
        if ((error = GLES32.glGetError()) != GLES32.GL_NO_ERROR) {
            Log.e("MyRenderer", glOperation + ": glError " + error);
        }
    }
    public static int loadShader(int type, String shaderCode){
        // create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
        int shader = GLES32.glCreateShader(type);
        GLES32.glShaderSource(shader, shaderCode);// add the source code to the shader and compile it
        GLES32.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        float left=-ratio,right=ratio;
        Matrix.frustumM(mProjectionMatrix, 0, left,right, -1.0f, 1.0f, 0.5f, 8.0f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] mRotationMatrix_x = new float[16];
        float[] mRotationMatrix_y = new float[16];
        float[] mRotationMatrix_z = new float[16];

        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);
        GLES32.glClearDepthf(1.0f);//set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);//enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL);//indicate what type of depth test
        Matrix.setIdentityM(mMVPMatrix,0);//set the model view projection matrix to an identity matrix
        Matrix.setIdentityM(mMVMatrix,0);//set the model view  matrix to an identity matrix
        Matrix.setIdentityM(mModelMatrix,0);//set the model matrix to an identity matrix
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0f, 1.0f,//camera is at (0,0,1)
                0f, 0f, 0f,//looks at the origin
                0f, 1f, 0.0f);//head is down (set to (0,1,0) to look from the top)
        Matrix.translateM(mModelMatrix,0,0.0f,0.0f,-5f);//move backward for 5 units
        Matrix.setRotateM(mRotationMatrix_y, 0, mYAngle, 0, 1.0f, 0);//rotate around the y-axis
        Matrix.setRotateM(mRotationMatrix_x, 0, mXAngle, 1.0f, 0, 0);//rotate around the x-axis
        Matrix.setRotateM(mRotationMatrix_z, 0, mZAngle, 0f, 0, 1);//rotate around the x-axis
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix_y, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix_x, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix_z, 0);

        // Calculate the projection and view transformation
        //calculate the model view matrix
        Matrix.multiplyMM(mMVMatrix,0,mViewMatrix,0,mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVMatrix,0);

        mfloorPlan.draw(mMVPMatrix);
    }
    public void setYAngle(float angle) { mYAngle = angle; }
    public void setXAngle(float angle){mXAngle=angle;}
    public void setZAngle(float angle){mZAngle=angle;}
}

