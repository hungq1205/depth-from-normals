public class SurfaceMesh extends Mesh implements Map2d<Vertex>
{
    private final int xdim, ydim;
    
    private SurfaceMesh(int xdim, int ydim)
    {
        this.xdim = xdim;
        this.ydim = ydim;
    }

    public int xdim() { return xdim; }
    public int ydim() { return ydim; }
    
    public Vertex get(int x, int y) 
    {
        return vertices[y * xdim + x];
    }

    public void set(int x, int y, Vertex value) 
    {
        vertices[y * xdim + x] = value;
    }

    public static SurfaceMesh generateXYSurface(int xdim, int ydim, float xmin, float xmax, float ymin, float ymax)
    {
        float cellWidth = (xmax - xmin) / xdim,
            cellHeight = (ymax - ymin) / ydim;
            
        SurfaceMesh mesh = new SurfaceMesh(xdim, ydim);
        mesh.vertices = new Vertex[xdim * ydim];
        mesh.triangles = new int[xdim * ydim * 6];

        for (int x = 0; x < xdim; x++)
            mesh.vertices[x] = new Vertex(xmin + x * cellWidth, ymin, 0);

        for (int y = 1; y < ydim; y++)
        {
            int curIndexOffset = y * xdim;
            
            mesh.vertices[curIndexOffset] = new Vertex(xmin, ymin + y * cellHeight, 0);
            for (int x = 1; x < xdim; x++)
            {
                mesh.vertices[curIndexOffset + x] = new Vertex(xmin + x * cellWidth, ymin + y * cellHeight, 0);

                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 0] = curIndexOffset + x - xdim - 1;
                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 1] = curIndexOffset + x - 1;
                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 2] = curIndexOffset + x;
 
                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 3] = curIndexOffset + x - xdim - 1;
                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 4] = curIndexOffset + x;
                mesh.triangles[(y - 1) * xdim * 6 + (x - 1) * 6 + 5] = curIndexOffset + x - xdim;
            }
        }

        return mesh;
    }

    public static SurfaceMesh generateXZSurface(int xdim, int zdim, float xmin, float xmax, float zmin, float zmax)
    {
        float cellWidth = (xmax - xmin) / xdim,
            cellHeight = (zmax - zmin) / zdim;
            
        SurfaceMesh mesh = new SurfaceMesh(xdim, zdim);
        mesh.vertices = new Vertex[xdim * zdim];
        mesh.triangles = new int[xdim * zdim * 6];

        for (int x = 0; x < xdim; x++)
            mesh.vertices[x] = new Vertex(xmin + x * cellWidth, 0, zmin);

        for (int z = 1; z < zdim; z++)
        {
            int curIndexOffset = z * xdim;
            
            mesh.vertices[curIndexOffset] = new Vertex(xmin, 0, zmin + z * cellHeight);
            for (int x = 1; x < xdim; x++)
            {
                mesh.vertices[curIndexOffset + x] = new Vertex(xmin + x * cellWidth, 0, zmin + z * cellHeight);

                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 0] = curIndexOffset + x - xdim - 1;
                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 1] = curIndexOffset + x - 1;
                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 2] = curIndexOffset + x;

                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 3] = curIndexOffset + x - xdim - 1;
                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 4] = curIndexOffset + x;
                mesh.triangles[(z - 1) * xdim * 6 + (x - 1) * 6 + 5] = curIndexOffset + x - xdim;
            }
        }

        return mesh;
    }
}
