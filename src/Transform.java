import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform 
{
    public Vector3f position, rotation;
    
    private static final float HALF_PI = 3.14f / 2;

    public Transform(Vector3f position, Vector3f rotation)
    {
        this.position = position;
        this.rotation = rotation;
    }

    public static Matrix4f transformationOf(Matrix4f base, Vector3f position, Vector3f rotation)
    {
        if (rotation.x > HALF_PI)
            rotation.x = HALF_PI;
        else if (rotation.x < -0.9f)
            rotation.x = -0.9f;

        if (rotation.y > HALF_PI + 0.1f)
            rotation.y = HALF_PI + 0.1f;
        else if (rotation.y < -HALF_PI - 0.1f)
            rotation.y = -HALF_PI - 0.1f;


        return base.translate(position).rotateXYZ(rotation.x, rotation.y, 0);
    }

    public static Matrix4f transformationOf(Matrix4f base, Vector3f position, Vector3f rotation, Vector3f scale)
    {
        return base.scale(scale).translate(position).rotateXYZ(rotation);
    }
}
