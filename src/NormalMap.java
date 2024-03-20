import org.joml.Vector3f;

public class NormalMap implements Map2d<Vector3f>
{
    public Vector3f[] content;

    private final int xdim, ydim;

    public NormalMap(int xdim, int ydim)
    {
        this.xdim = xdim;
        this.ydim = ydim;
        content = new Vector3f[ydim * xdim];
    }

    public int xdim() { return xdim; }
    public int ydim() { return ydim; }

    public Vector3f get(int x, int y) { return content[y * xdim + x]; }
    public void set(int x, int y, Vector3f value) { content[y * xdim + x] = value; }

    public FloatMap calcGradientMapToX()
    {
        FloatMap grad = new FloatMap(ydim, xdim);

        for (int y = 0; y < ydim; y++)
            for (int x = 0; x < xdim; x++)
                grad.set(x, y, -normalToGradient(get(x, y).x, get(x, y).z));

        return grad;
    }

    public FloatMap calcGradientMapToY()
    {
        FloatMap grad = new FloatMap(ydim, xdim);

        for (int y = 0; y < ydim; y++)
            for (int x = 0; x < xdim; x++)
                grad.set(x, y, normalToGradient(get(x, y).y, get(x, y).z));

        return grad;
    }
    
    public float normalToGradient(float normalComp)
    {
        if (normalComp > 0.499f)
            return 1.0f - (float)Math.sin(Math.acos(normalComp * 2 - 1));
        else
            return (float)Math.sin(Math.acos(normalComp * 2 - 1)) - 1.0f;
    }

    public float normalToGradient(float normalComp, float bComp)
    {
        return (normalComp * 2 - 1) / bComp;
    }
}
