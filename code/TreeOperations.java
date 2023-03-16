package code;

import java.util.*;

public class TreeOperations {
    // class for making all the algoritmic tree modifications like:
    // common cherry reduction, degree 2 vertex supression, etc.
    // I didnt want this in the tree class due to clutter
    
    //function to surpress all degree 2 vertices
    public void supressDeg2Vertex(Tree tree)
    {
        List<Integer> toRemove = new ArrayList<>();
        
        for(int v : tree.nodes.keySet())
        {
            if(v > 0){continue;}//leaf nodes always have only one edge cannot become 2 if everything is done correctly
            List<Edge> edges = tree.nodes.get(v);
            if(edges.size() == 2){
                toRemove.add(v);
            }
        }

        for(int v : toRemove)
        {
            supressDeg2Vertex(tree, v);
        }
    }
    
    //function is useful if you know which vertex needs to be supressed
    //can be used to skip checking all vertices
    public void supressDeg2Vertex(Tree tree, int v)
    {
        List<Edge> edges = tree.removeNode(v);
        
        //edges list should be size 2, if not you messed up
        Edge edge1 = edges.get(0);
        Edge edge2 = edges.get(1);
        tree.addEdge(edge1.getVertex(), edge2.getVertex());
    }

    //this assumes a size of tree > 3
    //TODO: i do not like the size of this function
    public void commonCherryReduction(Tree tree, Tree other)
    {
        List<Integer> toRemove = new ArrayList<>();
        for(int v : tree.nodes.keySet())
        {
            if(v > 0){continue;}//only check internal nodes
            List<Edge> edges = tree.nodes.get(v);
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
            tree.removeNode(v);
            other.removeNode(v);
        }
    }
}
