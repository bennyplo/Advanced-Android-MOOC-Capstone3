package com.bennyplo.capstone3;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FloorPlan3D {
    private final String vertexShaderCode =
            "attribute vec3 aVertexPosition;"+"uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                    " attribute vec4 aVertexColor;"+//the colour  of the object
                    "void main() {gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
                    "gl_PointSize = 40.0;"+
                    "        vColor=aVertexColor;}";//get the colour from the application program
    private final String fragmentShaderCode = "precision mediump float;varying vec4 vColor; "+
            "void main() {gl_FragColor = vColor;}";

    private final FloatBuffer vertexBuffer,colorBuffer;
    private final int mProgram;
    private int mPositionHandle,mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;
    private int vertexCount;// number of vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int colorStride=COLOR_PER_VERTEX*4;//4 bytes per vertex
    static float FloorPlanVertex[]= {
            //exterior
            -3,3f,-1,     -3,-3f,-1,     -3,-3f,1,     -3,-3f,1,     -3,3f,-1,     -3,3f,1,
            3,3f,-1,       3,-3f,-1,      3,-3f,1,      3,-3f,1,      3,3f,-1,      3,3f,1,
            3,3,-1,       -3,3,-1,       -3,3,1,       -3,3,1,        3,3,-1,       3,3,1,
            3,-3,-1,      -3,-3,-1,      -3,-3,1,      -3,-3,1,       3,-3,-1,      3,-3,1,
            //floor
            -3.0f,-3.0f,-1.0f,     3.0f,3.0f,-1.0f,      3.0f,-3.0f,-1.0f,      -3.0f,3.0f,-1.0f,
            -3.0f,-3.0f,-1.0f,     3,3,-1,
    };
    static float FloorPlanColor[]= {
            //exterior wall
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,            0.4f, 0.4f, 0.4f, 1.0f,
            //floor color
            0.2f, 0.2f, 0.2f, 1.0f,            0.2f, 0.2f, 0.2f, 1.0f,            0.2f, 0.2f, 0.2f, 1.0f,
            0.2f, 0.2f, 0.2f, 1.0f,            0.2f, 0.2f, 0.2f, 1.0f,            0.2f, 0.2f, 0.2f, 1.0f,
    };
    public FloorPlan3D(){
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(FloorPlanVertex.length * 4);// (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(FloorPlanVertex);
        vertexBuffer.position(0);
        vertexCount=FloorPlanVertex.length/COORDS_PER_VERTEX;
        ByteBuffer cb=ByteBuffer.allocateDirect(FloorPlanColor.length * 4);// (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(FloorPlanColor);
        colorBuffer.position(0);
        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES32.glCreateProgram();             // create empty OpenGL Program
        GLES32.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES32.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES32.glLinkProgram(mProgram);                  // link the  OpenGL program to create an executable
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition");
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle);
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor");
        // Enable a handle to the  colour
        GLES32.glEnableVertexAttribArray(mColorHandle);
        // Prepare the colour coordinate data
        GLES32.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX, GLES32.GL_FLOAT, false, colorStride, colorBuffer);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyRenderer.checkGlError("glGetUniformLocation");
    }

    public void draw(float[] mvpMatrix) {
        GLES32.glUseProgram(mProgram);// Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyRenderer.checkGlError("glUniformMatrix4fv");
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES32.glVertexAttribPointer(mColorHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false, colorStride, colorBuffer);
        // Draw the floor plan
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount);
    }
}
