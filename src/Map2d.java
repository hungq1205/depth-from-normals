public interface Map2d<T>
{
    public int xdim();
    public int ydim();

    public T get(int x, int y);
    public void set(int x, int y, T value);
}
