package code;

import java.util.*;

public class Tree {
    /*public class Edge
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
    }*/
    
    Map<Integer, List<Integer>> nodes;

    public Tree()
    {
        nodes = new HashMap<>();
    }

    public void addNode(int label)
    {
        //add new node to the list
        nodes.put(label, new ArrayList<>());
    }

    public void removeNode(int label)
    {
        List<Integer> edges = nodes.remove(label);
        for(int e : edges)
        {
            nodes.get(e).remove((Integer)label);
        }
    }

    public void addEdge(int from, int to)
    {
        nodes.get(from).add(to);
        nodes.get(to).add(from);
    }

    @Override
    public String toString() {
        String output = "";
        for(int key : nodes.keySet())
        {
            for(int e : nodes.get(key))
            {
                output += key + " - " + e + "\n";
            }
        }
        return output;
    }

}
