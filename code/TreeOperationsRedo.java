package code;

import java.util.*;

public class TreeOperationsRedo {

    public List<Edge> suppressDeg2Vertex(Tree tree, int v)
    {
        List<Edge> edges = tree.removeNode(v);
        
        //edges list should be size 2, if not you messed up
        Edge edge1 = edges.get(0);
        Edge edge2 = edges.get(1);
        tree.addEdge(edge1.getVertex(), edge2.getVertex());
        return edges;//needed for reversal of the operation
    }

    //find path from a to b
    public boolean MAF(Tree tree, Tree forest, int[][] edgesToRemove, int k)
    {
        //remove edges
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            forest.removeEdge(edgesToRemove[i][0], edgesToRemove[i][1]);
        }
        k -= edgesToRemove.length;

        if(k<0)
            return false;

        
        //TODO: if |Rt| <= 2 return true;

        //if there is a node r in Rt that is a root in Fdot2 remove r from Rt and add to Rd
        
        //find sibling pair in T1
        int a, b, v = -1;
        List<Integer> path = null;
        //remove common cherries
        do{
            int[] ab = findCherry(tree);
            a = ab[0];
            b = ab[1];
            v = ab[2];
            path = findPath(forest, a, b);
        }while(!detectCommonCherry(tree, forest, a, b, v));
        
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

        //readd edges
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            forest.addEdge(edgesToRemove[i][0], edgesToRemove[i][1]);
        }
        k += edgesToRemove.length;

        return false;
    }

    public int[] findCherry(Tree tree)
    {
        for(int v : tree.getTree().keySet())
        {
            if(v > 0) 
                continue;
            
            List<Edge> edges = tree.getNode(v);
            int[] leaves= new int[3];
            int leafCount = 0;
            for(Edge e : edges)
            {
                if(e.getVertex() > 0){leaves[leafCount++] = e.getVertex();}
            }

            if(leafCount >= 2)
            {
                return new int[]{leaves[0],leaves[1],v};//the cherry
            }
        }

        return null;
    }

    public boolean detectCommonCherry(Tree tree, Tree forest, int a, int b, int v)
    {
        List<Edge> otherEdges = forest.getNode(a);
        Edge otherEdge = otherEdges.get(0);//get connected internal node
        boolean isCommon = false;
        for(Edge e : forest.getNode(otherEdge.getVertex()))
        {
            if(e.getVertex() == b)//cherry is a common cherry
            {
                isCommon = true;
                break;
            }
        }

        if(isCommon)//reduce the trees if common cherry is found
        {
            tree.removeNode(b);
            forest.removeNode(b);
            suppressDeg2Vertex(tree, otherEdge.getVertex());
            suppressDeg2Vertex(forest, otherEdge.getVertex());
            //TODO:save cherry reduction data so we can reverse it
            return true;
        }

        return false;
    }
    
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
