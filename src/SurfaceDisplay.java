import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class SurfaceDisplay implements GLEventListener
{
    final String vertexShaderCode = 
        "#version 460\r\n" +
        "in layout(location=0) vec3 position;" +
        "in layout(location=1) float idepth;" +
        "out float odepth;" +
        "uniform mat4 transformation;" +
        "void main()" +
        "{" +
        "   gl_Position = transformation * vec4(position.x, position.y + idepth, position.z, 1.0);" +
        "   odepth = idepth + 0.15f;" +
        "}";

    final String fragmentShaderCode = 
        "#version 460\r\n"  +
        "in float odepth;" +
        "out vec4 color;" +
        "void main()" +
        "{" +
        "   color = vec4(0, odepth, odepth, 1.0);" +
        "}";

    public Transform transform;
    public float[] depthMap;
    public SurfaceMesh mesh;
    public float depthMultiplier = 1.0f;

    private int programID;
    private int[] ids;

    public SurfaceDisplay(SurfaceMesh mesh)
    {
        this.mesh = mesh;
    }

    public void installShaders(GL4 gl)
    {
        int vertexShaderID = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        int fragmentShaderID = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
    
        String[] adapter = new String[1];
        adapter[0] = vertexShaderCode;
        gl.glShaderSource(vertexShaderID, 1, adapter, null);
        adapter[0] = fragmentShaderCode;
        gl.glShaderSource(fragmentShaderID, 1, adapter, null);

        gl.glCompileShader(vertexShaderID);
        gl.glCompileShader(fragmentShaderID);

        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShaderID);
        gl.glAttachShader(programID, fragmentShaderID);
        gl.glLinkProgram(programID);

        gl.glUseProgram(programID);

        gl.glDeleteShader(vertexShaderID);
        gl.glDeleteShader(fragmentShaderID);
    }

    @Override
    public void display(GLAutoDrawable drawable)
    {
        final GL4 gl = drawable.getGL().getGL4();
        ids = new int[2];
        // 0: vertex buffer ID
        // 1: index buffer ID
        
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);        
        gl.glClearColor(.95f, .95f, .95f, 1f);
        installShaders(gl);

        Matrix4f transformation = Transform.transformationOf(new Matrix4f(), transform.position, transform.rotation);
        FloatBuffer transformationBuffer = Buffers.newDirectFloatBuffer(Buffers.SIZEOF_FLOAT * 16);

        int transformationUniformLoc = gl.glGetUniformLocation(programID, "transformation");
        gl.glUniformMatrix4fv(transformationUniformLoc, 1, false, transformation.get(transformationBuffer));

        IntBuffer indexBuffer = Buffers.newDirectIntBuffer(mesh.triangles);
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(mesh.vertices.length * 4);

        if (depthMap == null)
            depthMap = new float[mesh.ydim() * mesh.xdim()];

        for (int i = 0; i < mesh.vertices.length; i++)
            mesh.vertices[i].putBuffer(vertexBuffer, i * 4, depthMap[i] * depthMultiplier);

        gl.glGenBuffers(1, ids, 0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, ids[0]);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, mesh.vertices.length * (Vertex.primitiveSize + Buffers.SIZEOF_FLOAT), vertexBuffer, GL4.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 1, GL4.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 4, Buffers.SIZEOF_FLOAT * 3);

        gl.glGenBuffers(1, ids, 1);
        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ids[1]);
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, mesh.triangles.length * Buffers.SIZEOF_INT, indexBuffer, GL4.GL_STATIC_DRAW);
        
        gl.glDrawElements(GL4.GL_TRIANGLES, mesh.triangles.length, GL4.GL_UNSIGNED_INT, 0);
    
        indexBuffer.clear();
        vertexBuffer.clear();
        transformationBuffer.clear();
    }

    @Override
    public void dispose(GLAutoDrawable drawable)
    {
        final GL4 gl = drawable.getGL().getGL4();

        for (int i = 0; i < ids.length; i++)
            gl.glDeleteBuffers(1, ids, i);
        
        gl.glUseProgram(0);
        gl.glDeleteProgram(programID);
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        transform = new Transform(new Vector3f(0, -0.3f, 0), new Vector3f(0.65f, -0.57f, 0));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        final GL4 gl = drawable.getGL().getGL4();

        gl.glClearColor(.95f, .95f, .95f, 1f);
        gl.glViewport(x, y, width, height);
    }
}
