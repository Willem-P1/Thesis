package code;

import java.util.*;

public class Tree {
    
    Map<Integer, List<Edge>> nodes;

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
        List<Edge> edges = nodes.remove(label);
        Edge temp = new Edge(label);//used for removing edges from lists
        for(Edge e : edges)
        {
            nodes.get(e.getVertex()).remove(temp);
        }
    }

    public void addEdge(int from, int to)
    {
        nodes.get(from).add(new Edge(to));
        nodes.get(to).add(new Edge(from));
    }

    @Override
    public String toString() {
        String output = "";
        for(int key : nodes.keySet())
        {
            for(Edge e : nodes.get(key))
            {
                output += key + " - " + e + "\n";
            }
        }
        return output;
    }

}
