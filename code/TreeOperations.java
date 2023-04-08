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
        
        for(int v : tree.getTree().keySet())
        {
            if(v > 0){continue;}//leaf nodes always have only one edge cannot become 2 if everything is done correctly
            List<Edge> edges = tree.getTree().get(v);
            if(edges.size() == 2){
                toRemove.add(v);
            }
        }

        for(int v : toRemove)
        {
            supressDeg2Vertex(tree, v);
        }
        //remove this to remove looping
        if(!toRemove.isEmpty()){supressDeg2Vertex(tree);}
    }

    //this assumes a size of tree > 3
    //TODO: i do not like the size of this function
    public void commonCherryReduction(Tree tree, Tree other)
    {
        List<Integer> toRemove = new ArrayList<>();
        for(int v : tree.getTree().keySet())
        {
            if(v > 0){continue;}//only check internal nodes
            List<Edge> edges = tree.getTree().get(v);
            int[] leaves = new int[3];//just use 3 in case of a fuckup, prevents errors when |X| = 3
            int leafCount = 0;
            for(Edge e : edges)
            {
                if(e.getVertex() > 0){leaves[leafCount++] = e.getVertex();}
            }

            if(leafCount >= 2)
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
        //remove this to remove looping reduction
        if(!toRemove.isEmpty()){commonCherryReduction(tree, other);}
    }

    public int[] findCherry(Tree tree)
    {
        for(int v : tree.getTree().keySet())
        {
            if(v > 0){continue;}
            List<Edge> edges = tree.getTree().get(v);
            int[] leaves = new int[3];//just use 3 in case of a fuckup, prevents errors when |X| = 3
            int leafCount = 0;
            for(Edge e : edges)
            {
                if(e.getVertex() > 0){leaves[leafCount++] = e.getVertex();}
            }

            if(leafCount >= 2)
            {
                return new int[]{leaves[0],leaves[1]};//cherry found
            }
        }
        return null;//if no cherry was found
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



    public boolean MAF(Tree tree, Tree forest, int[][] edgeRemoval, int k)
    {
        //check base case
        if(k < 0){return false;}

        //First remove the edges from previous recursive call
        for(int i = 0; i < edgeRemoval.length; i++)
        {
            forest.removeEdge(edgeRemoval[i][0], edgeRemoval[i][1]);
        }
        k -= edgeRemoval.length;
        
        //how to i revert this idk bro its needed tho
        //and we need to remove common cherries again
        supressDeg2Vertex(forest);

        //TODO: find cherry in tree
        //then find path in forest
        int a = -1;
        int b = -1;
        List<Integer> path = findPath(forest, a, b);
        
        if(path == null)//no path within forest
        {
            //find edges to cherry
            List<Edge> edgesA = forest.getNode(a);
            List<Edge> edgesB = forest.getNode(b);
            //since a and b are leaves both lists should be of size 1
            if(MAF(tree, forest,new int[][]{{a,edgesA.get(0).getVertex()}},k))
                return true;

            if(MAF(tree, forest,new int[][]{{a,edgesB.get(0).getVertex()}},k))
                return true;
        }
        else
        {
            //get pendant nodes
            //you know the nodes and the two connections so we just have to find the third unkown node to get the pendant node
            List<int[]> pendant = new ArrayList<>();
            for(int i = 0; i < path.size();i++)
            {
                int label = path.get(i);
                if(label > 0){continue;}
                int other = path.get(i - 1);
                int other2 = path.get(i + 1);
                
                for(Edge e : forest.getNode(label))
                {
                    if(e.getVertex() == other || e.getVertex() == other2)
                    {
                        continue;
                    }
                    pendant.add(new int[]{label,e.getVertex()});
                }
            }
            //recursive call
            if(pendant.size() == 1)
            {
                if(MAF(tree, forest,new int[][]{pendant.get(0)},k))
                    return true;
            }else
            {
                List<Edge> edgesA = forest.getNode(a);
                List<Edge> edgesB = forest.getNode(b);
                //use cheap recursive call first 
                if(MAF(tree, forest,new int[][]{{a,edgesA.get(0).getVertex()}},k))
                    return true;

                if(MAF(tree, forest,new int[][]{{a,edgesB.get(0).getVertex()}},k))
                    return true;

                if(MAF(tree, forest,(int[][])pendant.toArray(),k))
                    return true;
            }
        }

        //TODO: revert degree 2 vertex supression

        //add back the edges we just removed from previous recursive call
        for(int i = 0; i < edgeRemoval.length; i++)
        {
            forest.addEdge(edgeRemoval[i][0], edgeRemoval[i][1]);
        }
        k += edgeRemoval.length;//probably not neccecary due to pass by value

        return false;
    }

    //find path from a to b
    public List<Integer> findPath(Tree tree, int a, int b)
    {
        List<Integer> path = new ArrayList<>();
        if(recursiveDFS(tree, -1, a, b, path))
        {
            return path;
        }
        return null;
    }

    //dfs for finding the path to the other node
    private boolean recursiveDFS(Tree tree, int prev, int from, int to, List<Integer> currPath)
    {
        if(from == to){
            currPath.add(from);
            return true;
        }
        for(Edge e : tree.getNode(from))
        {
            if(e.getVertex() == prev){continue;}//skip previously visited node
            if(recursiveDFS(tree, from, e.getVertex(), to, currPath))
            {
                currPath.add(from);
                return true;
            }
        }
        return false;
    }
}