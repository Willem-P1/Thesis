package code;

import java.util.*;

public class TreeOperationsRedo {
    private final boolean DEBUG = false;
    
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

    public class EdgeRemovalOperation implements Operation
    {
        Tree tree;
        int a,b;
        //one class to store tree operations that will have to be reverted
        public EdgeRemovalOperation(Tree tree, int a, int b)
        {
            this.tree = tree;
            this.a = a;
            this.b = b;
        }

        public void revert()
        {
            tree.addEdge(a, b);
        }
    }

    public class NodeRemovalOperation implements Operation
    {
        Tree tree1;
        Tree tree2;
        int v;
        //one class to store tree operations that will have to be reverted
        public NodeRemovalOperation(Tree tree1, Tree tree2, int v)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.v = v;
        }

        public void revert()
        {
            //readd node
            tree1.addNode(v);
            tree2.addNode(v);
        }
    }

    public class SingletonDisconnectOperation implements Operation
    {
        Tree tree1;
        Tree tree2;
        VertexSupressOperation VSOp;
        int a;
        //one class to store tree operations that will have to be reverted
        public SingletonDisconnectOperation(Tree tree1, Tree tree2, VertexSupressOperation vsop,int a)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.VSOp = vsop;
            this.a = a;
        }

        public void revert()
        {
            VSOp.revert();
            //readd node
            tree1.addNode(a);
            tree2.addNode(a);
            tree1.addEdge(a, VSOp.v);
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
        if(DEBUG)
            System.out.println("Supress: " + v);
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
    
    public Operation removeEdge(Tree tree, int a , int b)
    {
        tree.removeEdge(a, b);

        return new EdgeRemovalOperation(tree,a,b);
    }

    //find path from a to b
    public boolean MAF(Tree tree, Tree forest, int[][] edgesToRemove, int k)
    {
        List<Operation> operations = new ArrayList<>();
        //remove edges
        if(DEBUG)
            System.out.println("Edges to remove:");
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            if(DEBUG)
                System.out.println(edgesToRemove[i][0] + " - " + edgesToRemove[i][1]);
            operations.add(removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]));

            if(edgesToRemove[i][0] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][0]));}
            if(edgesToRemove[i][1] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][1]));}
        }
        k -= edgesToRemove.length;

        if(k<0){
            reverseOperations(operations);
            return false;
        }
        int operationNum;
        do
        {
            operationNum = operations.size();
            //handle singletons
            operations.addAll(removeSingletons(tree, forest));

            //reduce cherries
            operations.addAll(reduceCommonCherries(tree, forest));
        }
        while(operationNum != operations.size());

        //TODO: if |Rt| <= 2 return true;
        if(tree.getTree().keySet().size() <= 2)
            return true;
        //if there is a node r in Rt that is a root in Fdot2 remove r from Rt and add to Rd
        
        //find sibling pair in T1
        int[] ab = findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        if(DEBUG){
            System.out.println("[" + ab[0] + ", " + ab[1] + "]");
            System.out.println(forest);
        }
        List<Integer> path = findPath(forest, a, b);

        //find edges to cherry
        List<Edge> edgesA = forest.getNode(a);
        List<Edge> edgesB = forest.getNode(b);
        System.out.println("[" + ab[0] + ", " + ab[1] + "]");

        //since a and b are leaves both lists should be of size 1
        if(MAF(tree, forest,new int[][]{{a,edgesA.get(0).getVertex()}},k))
            return true;

        if(MAF(tree, forest,new int[][]{{b,edgesB.get(0).getVertex()}},k))
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

        //revert edits to trees
        reverseOperations(operations);

        k += edgesToRemove.length;

        return false;
    }

    public List<Operation> removeSingletons(Tree tree, Tree forest)
    {
        List<Operation> operations = new ArrayList<>();
        List<Integer> toRemove = new ArrayList<>();

        for(int v : forest.getTree().keySet())
        {
            if(v < 0){continue;}//dont have to check internal nodes
            if(forest.getNode(v).size() == 0)
            {
                toRemove.add(v);
            }
            
        }

        for(int v : toRemove)
        {
            if(tree.getNode(v).size() == 0)//found singleton in forest
            {
                tree.removeNode(v);
                forest.removeNode(v);
                operations.add(new NodeRemovalOperation(tree, forest, v));
            }
            else
            {
                forest.removeNode(v);
                int parent = tree.removeNode(v).get(0).getVertex();
                VertexSupressOperation vso = suppressDeg2Vertex(tree, parent);
                operations.add(new SingletonDisconnectOperation(tree, forest, vso, v));
            }
        }
        return operations;
    }

    public List<Operation> reduceCommonCherries(Tree tree1, Tree tree2)
    {
        List<Integer> toRemove = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();
        
        for(int a : tree1.getTree().keySet())
        {
            if(a < 0){continue;}
            
            int b = findCherryFromNode(tree1, a);
            if(b < a){continue;}// to prevent finding cherries twice only find the cherry from the lowest label value

            //cherry found, check with other tree
            if(check3Cherry(a, b, tree2))
            {
                toRemove.add(b);
            }
        }
        
        for(int v : toRemove)
        {
            operations.add(removeCommonCherry(tree1,tree2,v));
        }
        
        if(!toRemove.isEmpty()){
            operations.addAll(reduceCommonCherries(tree1, tree2));
        }
        
        return operations;
    }
    
    public int findCherryFromNode(Tree tree, int a)
    {
        int v = tree.getNode(a).get(0).getVertex();
        for(Edge e : tree.getNode(v))
        {
            int b = e.getVertex();
            if(b > 0 && b != a)
                return b;
        }
        return -1;
    }

    //cherry of two leaves adjacent to same internal node
    public boolean check3Cherry(int a, int b, Tree tree)
    {
        int v1 = tree.getNode(a).get(0).getVertex();
        int v2 = tree.getNode(b).get(0).getVertex();
        return v1 == v2;//|| v1 == b
        
    }

    //cherry of two adjacent leaves
    public boolean check2Cherry(int a, int b, Tree tree)
    {
        int v1 = tree.getNode(a).get(0).getVertex();
        int v2 = tree.getNode(b).get(0).getVertex();
        return v1 == b && v2 == a;
        
    }

    public List<int[]> getPendantNodes(Tree forest, List<Integer> path)
    {
        List<int[]> pendant = new ArrayList<>();
        if(DEBUG){
            System.out.println(path);
            System.out.println(forest);
            forest.printDeg();
        }
        for(int i = 0; i < path.size();i++)
        {
            int label = path.get(i);
            if(label > 0){continue;}
            int other = path.get(i - 1);
            int other2 = path.get(i + 1);
            if(DEBUG)
                System.out.println(label);
            
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

    private void reverseOperations(List<Operation> operations)
    {
        Collections.reverse(operations);
        for(Operation op : operations)
        {
            op.revert();
        }
    }
}
