public class FloatMap implements Map2d<Float> 
{
    public float[] content;

    private final int xdim, ydim;

    public FloatMap(int xdim, int ydim)
    {
        this.xdim = xdim;
        this.ydim = ydim;
        content = new float[ydim * xdim];
    }

    public int xdim() { return xdim; }
    public int ydim() { return ydim; }

    public Float get(int x, int y) { return content[y * xdim + x]; }
    public void set(int x, int y, Float value) { content[y * xdim + x] = value; }

    public FloatMap accumulateAlongX()
    {
        for (int y = 0; y < ydim; y++)
            for (int x = 1; x < xdim; x++)
                set(x, y, get(x, y) + get(x - 1, y));

        return this;
    }

    public FloatMap accumulateAlongY()
    {
        for (int x = 0; x < xdim; x++)
            for (int y = 1; y < ydim; y++)
                set(x, y, get(x, y) + get(x, y - 1));

        return this;
    }

    public FloatMap add(FloatMap value)
    {
        for (int i = 0; i < content.length; i++)
            content[i] += value.content[i];

        return this;
    }

    public FloatMap subtract(FloatMap value)
    {
        for (int i = 0; i < content.length; i++)
            content[i] -= value.content[i];

        return this;
    }

    public FloatMap mul(float constant)
    {
        for (int i = 0; i < content.length; i++)
            content[i] *= constant;

        return this;
    }

    public FloatMap invert()
    {
        for (int i = 0; i < content.length; i++)
            content[i] = -content[i];   

        return this; 
    }

    public static FloatMap sum(FloatMap... values)
    {
        FloatMap result = new FloatMap(values[0].xdim, values[0].ydim);
        for (int i = 0; i < result.content.length; i++)
            for (FloatMap map : values)
                result.content[i] += map.content[i];

        return result;
    }

    public static FloatMap add(FloatMap a, FloatMap b)
    {
        FloatMap result = new FloatMap(a.xdim, a.ydim);
        for (int i = 0; i < result.content.length; i++)
            result.content[i] = a.content[i] + b.content[i];
        return result;
    }

    public static FloatMap getAccumulateAlongX(FloatMap value)
    {
        FloatMap result = new FloatMap(value.xdim, value.ydim);

        for (int y = 0; y < value.ydim; y++)
        {
            result.set(0, y, value.get(0, y));
            for (int x = 1; x < value.xdim; x++)
                result.set(x, y, value.get(x, y) + result.get(x - 1, y));
        }

        return result;
    }

    public static FloatMap getAccumulateAlongY(FloatMap value)
    {
        FloatMap result = new FloatMap(value.xdim, value.ydim);

        for (int x = 0; x < value.xdim; x++)
        {
            result.set(x, 0, value.get(x, 0));
            for (int y = 1; y < value.ydim; y++)
                result.set(x, y, value.get(x, y) + result.get(x, y - 1));
        }

        return result;
    }

    public static FloatMap getReverseAccumulateAlongX(FloatMap value)
    {
        FloatMap result = new FloatMap(value.xdim, value.ydim);

        for (int y = 0; y < value.ydim; y++)
        {
            result.set(value.xdim - 1, y, value.get(value.xdim - 1, y));
            for (int x = value.xdim - 2; x >= 0; x--)
                result.set(x, y, value.get(x, y) + result.get(x + 1, y));
        }

        return result;
    }

    public static FloatMap getReverseAccumulateAlongY(FloatMap value)
    {
        FloatMap result = new FloatMap(value.xdim, value.ydim);

        for (int x = 0; x < value.xdim; x++)
        {
            result.set(x, value.ydim - 1, value.get(x, value.ydim - 1));
            for (int y = value.ydim - 2; y >= 0; y--)
                result.set(x, y, value.get(x, y) + result.get(x, y + 1));
        }

        return result;
    }
}
