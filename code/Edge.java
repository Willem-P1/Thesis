package code;

public class Edge
{
    int v;
    boolean enabled;
    public Edge(int vertex)
    {
        v = vertex;
        enabled = true;
    }

    public int getVertex()
    {
        return v;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public boolean equals(Object obj) {
        Edge e = (Edge) obj;
        return v == e.getVertex();
    }

    @Override
    public String toString() {
        return "" + v;
    }
}