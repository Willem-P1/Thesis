package code;

import java.util.*;

public class Tree {
    
    Map<Integer, List<Edge>> nodes;

    public Tree()
    {
        nodes = new HashMap<>();
    }

    public Map<Integer, List<Edge>> getTree()
    {
        return nodes;
    }

    public void addNode(int label)
    {
        //add new node to the list
        nodes.put(label, new ArrayList<>());
    }

    public List<Edge> removeNode(int label)
    {
        List<Edge> edges = nodes.remove(label);
        Edge temp = new Edge(label);//used for removing edges from lists
        for(Edge e : edges)
        {
            nodes.get(e.getVertex()).remove(temp);
        }

        return edges;//return old edges for convenience in other function
    }

    public void addEdge(int from, int to)
    {
        nodes.get(from).add(new Edge(to));
        nodes.get(to).add(new Edge(from));
    }

    public void removeEdge(int from, int to)
    {
        nodes.get(from).remove(new Edge(to));
        nodes.get(to).remove(new Edge(from));
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

    //TODO: i do not like the fact that these functions are in the tree class
    // maybe put them in another file idk.
    // I want to make sure the project stays readable and maintainable

    //function to surpress all degree 2 vertices
    public void supressDeg2Vertex()
    {
        List<Integer> toRemove = new ArrayList<>();
        
        for(int v : nodes.keySet())
        {
            if(v > 0){continue;}//leaf nodes always have only one edge cannot become 2 if everything is done correctly
            List<Edge> edges = nodes.get(v);
            if(edges.size() == 2){
                toRemove.add(v);
            }
        }

        for(int v : toRemove)
        {
            supressDeg2Vertex(v);
        }
    }
    
    //function is useful if you know which vertex needs to be supressed
    //can be used to skip checking all vertices
    public void supressDeg2Vertex(int v)
    {
        List<Edge> edges = removeNode(v);
        
        //edges list should be size 2, if not you messed up
        Edge edge1 = edges.get(0);
        Edge edge2 = edges.get(1);
        addEdge(edge1.getVertex(), edge2.getVertex());
    }

    //this assumes a size of tree > 3
    //TODO: i do not like the size of this function
    public void commonCherryReduction(Tree other)
    {
        List<Integer> toRemove = new ArrayList<>();
        for(int v : nodes.keySet())
        {
            if(v > 0){continue;}//only check internal nodes
            List<Edge> edges = nodes.get(v);
            int[] leaves = new int[3];//just use 3 in case of a fuckup, prevents errors
            int leafCount = 0;
            for(Edge e : edges)
            {
                if(e.getVertex() > 0){leaves[leafCount++] = e.getVertex();}
            }

            if(leafCount == 2)
            {
                //check for cherry on other tree
                List<Edge> otherEdges = other.getTree().get(leaves[0]);
                Edge otherEdge = otherEdges.get(0);//get connected internal node
                for(Edge e : other.getTree().get(otherEdge.getVertex()))
                {
                    if(e.getVertex() == leaves[1])
                    {
                        toRemove.add(leaves[1]);
                        break;
                    }
                }

            }
        }

        for(int v : toRemove)
        {
            removeNode(v);
            other.removeNode(v);
        }
    }
}
