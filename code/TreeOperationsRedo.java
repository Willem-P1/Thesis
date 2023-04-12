package code;

import java.util.*;

public class TreeOperationsRedo {
    public interface Operation
    {
        public void revert();
    }

    public class VertexSupressOperation implements Operation
    {
        Tree tree;
        List<Edge> edges;
        public int v;
        //one class to store tree operations that will have to be reverted
        public VertexSupressOperation(Tree tree, List<Edge> edges, int v)
        {
            this.tree = tree;
            this.edges = edges;
            this.v = v;
        }

        public void revert()
        {
            int a = edges.get(0).getVertex();
            int b = edges.get(1).getVertex();
            tree.bisectEdge(a, b, v);
        }
    }

    public class CherryReductionOperation implements Operation
    {
        Tree tree1;
        Tree tree2;
        VertexSupressOperation VSOp1;
        VertexSupressOperation VSOp2;
        int v;
        //one class to store tree operations that will have to be reverted
        public CherryReductionOperation(Tree tree1, Tree tree2, int v, VertexSupressOperation vsop1,VertexSupressOperation vsop2)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.v = v;
            this.VSOp1 = vsop1;
            this.VSOp2 = vsop2;
        }

        public void revert()
        {   //readd parent node
            VSOp1.revert();
            VSOp2.revert();
            
            //readd node
            tree1.addNode(v);
            tree2.addNode(v);

            //readd edge to create cherry
            tree1.addEdge(v, VSOp1.v);
            tree2.addEdge(v, VSOp2.v);

        }
    }

    public VertexSupressOperation suppressDeg2Vertex(Tree tree, int v)
    {
        List<Edge> edges = tree.removeNode(v);
        
        //edges list should be size 2, if not you messed up
        Edge edge1 = edges.get(0);
        Edge edge2 = edges.get(1);
        tree.addEdge(edge1.getVertex(), edge2.getVertex());
        return new VertexSupressOperation(tree, edges, v);//needed for reversal of the operation
    }

    public Operation removeCommonCherry(Tree tree, Tree forest, int v)
    {
        List<Edge> edges1 = tree.removeNode(v);
        List<Edge> edges2 = forest.removeNode(v);
        VertexSupressOperation op1 = suppressDeg2Vertex(tree, edges1.get(0).getVertex());
        VertexSupressOperation op2 = suppressDeg2Vertex(forest, edges2.get(0).getVertex());
        return new CherryReductionOperation(tree, forest, v, op1, op2);
    }
    
    public void revertSupression(Operation op)
    {
        op.revert();
    }

    //find path from a to b
    public boolean MAF(Tree tree, Tree forest, int[][] edgesToRemove, int k)
    {
        List<Operation> operations = new ArrayList<>();
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
        int[] ab = findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        List<Integer> path = findPath(forest, a, b);

        //find edges to cherry
        List<Edge> edgesA = forest.getNode(a);
        List<Edge> edgesB = forest.getNode(b);
        //since a and b are leaves both lists should be of size 1
        if(MAF(tree, forest,new int[][]{{a,edgesA.get(0).getVertex()}},k))
            return true;

        if(MAF(tree, forest,new int[][]{{a,edgesB.get(0).getVertex()}},k))
            return true;

        if(path != null)//ther is a path within forest
        {
            //get pendant nodes
            //you know the nodes and the two connections so we just have to find the third unkown node to get the pendant node
            List<int[]> pendant = getPendantNodes(forest, path);

            for(int i = 0; i < pendant.size();i++)
            {
                int[][] edges = new int[pendant.size()-1][2];
                int index = 0;
                for(int l = 0; l < pendant.size();l++)
                {
                    if(l == i) continue;
                    edges[index++] = pendant.get(l);
                }
                if(MAF(tree, forest,edges,k))
                    return true;
            }
            
        }

        for(Operation op : operations)
        {
            op.revert();
        }

        //readd edges
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            forest.addEdge(edgesToRemove[i][0], edgesToRemove[i][1]);
        }
        k += edgesToRemove.length;

        return false;
    }

    public List<int[]> getPendantNodes(Tree forest, List<Integer> path)
    {
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
        return pendant;
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
                return new int[]{leaves[0],leaves[1]};
            }
        }

        return null;
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