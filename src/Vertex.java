import java.nio.FloatBuffer;

import org.joml.Vector3f;

public class Vertex 
{
    public static long primitiveSize = 12;

    public Vector3f position;

    public Vertex(Vector3f position) 
    {
        this.position = position;
    }

    public Vertex(float x, float y, float z)
    {
        this.position = new Vector3f(x, y, z);    
    }

    public void putBuffer(FloatBuffer buffer, int memLoc)
    {
        buffer.put(memLoc, position.x());
        buffer.put(memLoc + 1, position.y());
        buffer.put(memLoc + 2, position.z());
    }

    public void putBuffer(FloatBuffer buffer, int memLoc, float depth)
    {
        buffer.put(memLoc, position.x());
        buffer.put(memLoc + 1, position.y());
        buffer.put(memLoc + 2, position.z());
        buffer.put(memLoc + 3, depth);
    }
}
