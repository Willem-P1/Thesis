package code;

import java.util.*;

public class TreeOperations {
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
        int a,b;
        boolean addEdge;

        //one class to store tree operations that will have to be reverted
        public NodeRemovalOperation(Tree tree1, Tree tree2, int a, boolean addEdge, int b)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.a = a;
            this.b = b;
            this.addEdge = addEdge;
        }

        public void revert()
        {
            //readd node
            tree1.addNode(a);
            tree2.addNode(a);
            
            if(addEdge)
                tree1.addEdge(a, b);

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

    public class CherryReductionOperation33 implements Operation
    {
        Tree tree1;
        Tree tree2;
        VertexSupressOperation VSOp1;
        VertexSupressOperation VSOp2;
        int v;
        //one class to store tree operations that will have to be reverted
        public CherryReductionOperation33(Tree tree1, Tree tree2, int v, VertexSupressOperation vsop1,VertexSupressOperation vsop2)
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

    public class CherryReductionOperation22 implements Operation
    {
        Tree tree1;
        Tree tree2;
        int a,b;
        //one class to store tree operations that will have to be reverted
        public CherryReductionOperation22(Tree tree1, Tree tree2, int a, int b)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.a = a;
            this.b = b;
        }

        public void revert()
        {
            //readd nodes
            tree1.addNode(b);
            tree2.addNode(b);

            //readd edge to create cherry
            tree1.addEdge(a, b);
            tree2.addEdge(a, b);

        }
    }
    
    public class CherryReductionOperation32 implements Operation
    {
        Tree tree1;
        Tree tree2;
        VertexSupressOperation VSOp1;
        int a,b,v;
        //one class to store tree operations that will have to be reverted
        public CherryReductionOperation32(Tree tree1, Tree tree2, VertexSupressOperation vsop1, int a, int b)
        {
            this.tree1 = tree1;
            this.tree2 = tree2;
            this.VSOp1 = vsop1;
            this.a = a;
            this.b = b;
        }

        public void revert()
        {
            VSOp1.revert();

            //readd nodes
            tree1.addNode(b);
            tree2.addNode(b);

            //readd edge to create cherry
            tree1.addEdge(VSOp1.v, b);
            tree2.addEdge(a, b);

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
        int other1 = edges1.get(0).getVertex();
        int other2 = edges2.get(0).getVertex();
        if(other1 > 0 && other2 == other1){
            //a--b cherry
            return new CherryReductionOperation22(tree, forest, other1, v);
        }

        if(other2 > 0 && other2 != other1){
            //forest has a--b cherry
            //tree has normal cherry
            VertexSupressOperation op1 = suppressDeg2Vertex(tree, other1);
            return new CherryReductionOperation32(tree, forest, op1, other2, v);
        }
        VertexSupressOperation op1 = suppressDeg2Vertex(tree, other1);
        VertexSupressOperation op2 = suppressDeg2Vertex(forest, other2);
        return new CherryReductionOperation33(tree, forest, v, op1, op2);
    }
    
    public Operation removeEdge(Tree tree, int a , int b)
    {
        tree.removeEdge(a, b);

        return new EdgeRemovalOperation(tree,a,b);
    }

    public int MCTSTBR(Tree tree, Tree forest, int[][] edgesToRemove, int k, double maxTime)
    {
        //remove edges
        if(DEBUG)
            System.out.println("Edges to remove:");
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            if(DEBUG)
                System.out.println(edgesToRemove[i][0] + " - " + edgesToRemove[i][1]);
            removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]);

            if(edgesToRemove[i][0] < 0){suppressDeg2Vertex(forest, edgesToRemove[i][0]);}
            if(edgesToRemove[i][1] < 0){suppressDeg2Vertex(forest, edgesToRemove[i][1]);}
        }
        k += edgesToRemove.length;
        
        //reduce the tree
        reduce(tree, forest);

        if(tree.size() <= 2)
            return k;
        
        //find sibling pair in T1
        int[] ab = findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        if(DEBUG){
            System.out.println("[" + ab[0] + ", " + ab[1] + "]");
            System.out.println(forest);
        }

        List<Integer> path = findPath(forest, a, b);

        
        // find edges to cherry
        // I initially accessed only the edge list 
        // but due to some weird bug, after the recursive calls these variables
        // would be null and crash the program.
        // I do not know what caused this but this fixes the symptoms
        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();
        
        int[][] bestEdges = new int[][]{{a,parentA}};
        int bestK = evaluateEdges(tree, forest, bestEdges, k,maxTime);
        
        if(evaluateEdges(tree, forest, new int[][]{{b,parentB}}, k, maxTime) < bestK){
            bestEdges = new int[][]{{b,parentB}};
        }
        
        if(path == null)//there is a path within forest
            return MCTSTBR(tree, forest, bestEdges,k, maxTime);

        
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
            if(evaluateEdges(tree, forest, edges, k, maxTime) < bestK){
                bestEdges = edges;
            }
        }

        
        return MCTSTBR(tree, forest,bestEdges,k, maxTime);
    }

    

    private int evaluateEdges(Tree tree, Tree forest, int[][] edgesToRemove, int k, double maxTime) {
        Main.a++;
        List<Operation> operations = new ArrayList<>();

        for(int i = 0; i < edgesToRemove.length; i++)
        {
            if(DEBUG)
                System.out.println(edgesToRemove[i][0] + " - " + edgesToRemove[i][1]);
            operations.add(removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]));

            if(edgesToRemove[i][0] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][0]));}
            if(edgesToRemove[i][1] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][1]));}
        }
        k += edgesToRemove.length;
        
        operations.addAll(reduce(tree, forest));
        
        
        int min = Integer.MAX_VALUE;
        long startTime = System.nanoTime();
        long endTime = startTime;
        while(endTime - startTime < maxTime)
        {
            long startTime2 = System.nanoTime();

            Tree T = tree.copy();
            Tree F = forest.copy();
            
            long endTime2 = System.nanoTime();
            double total = endTime2 - startTime2;
            total /= 1.0e9;
            Main.t2 += total;
            
            int result = MCTBR(T, F, new int[0][0], k);
            min = Math.min(min, result);
            
            endTime =  System.nanoTime();
        }

        reverseOperations(operations);
        return min;
    }

    public int MCTBR(Tree tree, Tree forest, int[][] edgesToRemove, int k)
    {
        //remove edges
        if(DEBUG)
            System.out.println("Edges to remove:");
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            if(DEBUG)
                System.out.println(edgesToRemove[i][0] + " - " + edgesToRemove[i][1]);
            removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]);

            if(edgesToRemove[i][0] < 0){suppressDeg2Vertex(forest, edgesToRemove[i][0]);}
            if(edgesToRemove[i][1] < 0){suppressDeg2Vertex(forest, edgesToRemove[i][1]);}
        }
        k += edgesToRemove.length;
        long startTime = System.nanoTime();
        //reduce tree
        reduce(tree, forest);
        long endTime = System.nanoTime();
        double total = endTime - startTime;
        total /= 1.0e9;
        Main.t += total;
        //TODO: if |Rt| <= 2 return true;
        if(tree.size() <= 2)
            return k;
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

        
        // find edges to cherry
        // I initially accessed only the edge list 
        // but due to some weird bug, after the recursive calls these variables
        // would be null and crash the program.
        // I do not know what caused this but this might fix the symptoms
        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();
        
        Random random = new Random();
        double r = random.nextDouble();

        if(path == null)//there is a path within forest
            r *= 0.666666;//limit to just the two options


        //since a and b are leaves both lists should be of size 1
        if(r <= 0.333333){
            // Main.a++;
            return MCTBR(tree, forest,new int[][]{{a,parentA}},k);
        }

        if(r <= 0.666666){
            // Main.b++;
            return MCTBR(tree, forest,new int[][]{{b,parentB}},k);
        }
            

        
        //get pendant nodes
        //you know the nodes and the two connections so we just have to find the third unkown node to get the pendant node
        List<int[]> pendant = getPendantNodes(forest, path);
        int i = random.nextInt(pendant.size());
        int[][] edges = new int[pendant.size()-1][2];
        int index = 0;
        for(int l = 0; l < pendant.size();l++)
        {
            if(l == i) continue;
            edges[index++] = pendant.get(l);
        }

        
        // Main.c++;
        return MCTBR(tree, forest,edges,k);
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
        
        //reduce tree
        operations.addAll(reduce(tree, forest));

        //TODO: if |Rt| <= 2 return true;
        if(tree.size() <= 2)
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

        // find edges to cherry
        // I initially accessed only the edge list 
        // but due to some weird bug, after the recursive calls these variables
        // would be null and crash the program.
        // I do not know what caused this but this might fix the symptoms
        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();
        
        //since a and b are leaves both lists should be of size 1
        if(MAF(tree, forest,new int[][]{{a,parentA}},k))
        {
            System.out.println(0);
            return true;
        }

        if(MAF(tree, forest,new int[][]{{b,parentB}},k))
        {
            System.out.println(1);
            return true;
        }
        if(path != null)//there is a path within forest
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
                {
                    System.out.println(2);
                    return true;
                }
            }
            
        }

        //revert edits to trees
        reverseOperations(operations);

        k += edgesToRemove.length;

        return false;
    }

    public List<Operation> reduce(Tree tree, Tree forest) {
        List<Operation> operations = new ArrayList<>();
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

        return operations;
    }

    public int doOp(Tree tree, Tree forest, int move, int k, List<Operation> operations)
    {
        // System.out.println(tree.size());
        // System.out.println("move: " + move);

        int moveNum = 0;
        int[][] edgesToRemove = new int[0][0];
        
        int[] ab = findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        List<Integer> path = findPath(forest, a, b);

        // find edges to cherry
        // I initially accessed only the edge list 
        // but due to some weird bug, after the recursive calls these variables
        // would be null and crash the program.
        // I do not know what caused this but this might fix the symptoms
        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();

        if(moveNum == move){edgesToRemove = new int[][]{{a,parentA}};}
        moveNum++;

        if(moveNum == move){edgesToRemove = new int[][]{{b,parentB}};}
        moveNum++;
        // System.out.println("[" + ab[0] + ", " + ab[1] + "]");
        // System.out.println(path);
        if(path != null){
            List<int[]> pendant = getPendantNodes(forest, path);

            for(int i = 0; i < pendant.size();i++)
            {
                if(moveNum != move){moveNum++; continue;}
                int[][] edges = new int[pendant.size()-1][2];
                int index = 0;
                for(int l = 0; l < pendant.size();l++)
                {
                    if(l == i) continue;
                    edges[index++] = pendant.get(l);
                }
                edgesToRemove = edges;
                break;
            }
        }
        if(edgesToRemove.length == 0){System.out.println("Something went wrong performing operation on tree");}
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            operations.add(removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]));

            if(edgesToRemove[i][0] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][0]));}
            if(edgesToRemove[i][1] < 0){operations.add(suppressDeg2Vertex(forest, edgesToRemove[i][1]));}
        }
        k += edgesToRemove.length;

        operations.addAll(reduce(tree, forest));

        return k;
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
            if(tree.getNode(v).size() == 0)//found singleton in tree too
            {
                tree.removeNode(v);
                forest.removeNode(v);
                operations.add(new NodeRemovalOperation(tree, forest, v, false, -1));
            }
            else
            {
                forest.removeNode(v);
                int parent = tree.removeNode(v).get(0).getVertex();
                if(parent > 0)
                {
                    operations.add(new NodeRemovalOperation(tree, forest, v, true, parent));

                }else
                {
                    VertexSupressOperation vso = suppressDeg2Vertex(tree, parent);
                    operations.add(new SingletonDisconnectOperation(tree, forest, vso, v));
                     
                }
            }
        }
        return operations;
    }

    public List<Operation> reduceCommonCherries(Tree tree1, Tree tree2)
    {
        // System.out.println(tree1.size() + " : " + tree2.size() + "   " + tree1.getTree().keySet() + tree2.getTree().keySet());

        Set<Integer> toRemove = new HashSet<>();
        List<Operation> operations = new ArrayList<>();
        
        for(int a : tree1.getTree().keySet())
        {
            if(a < 0){continue;}
            if(tree1.getNode(a).size() == 0){continue;}//a is a singleton
            int b = findCherryFromNode(tree1, a);
            if(b < a){continue;}// to prevent finding cherries twice only find the cherry from the lowest label value

            //cherry found, check with other tree
            if(checkCherry(a, b, tree2))
            {
                if(!toRemove.contains(b))
                    toRemove.add(b);
            }
        }
        
        for(int v : toRemove)
        {
            operations.add(removeCommonCherry(tree1,tree2,v));
        }
        
        // if(!toRemove.isEmpty()){
        //     operations.addAll(reduceCommonCherries(tree1, tree2));
        // }
        
        return operations;
    }
    
    public int findCherryFromNode(Tree tree, int a)
    {
        int v = tree.getNode(a).get(0).getVertex();
        if(v > 0){return v;}//a--b cherry
        for(Edge e : tree.getNode(v))
        {
            int b = e.getVertex();
            if(b > 0 && b != a)
                return b;
        }
        return -1;
    }

    //check for cherry
    public boolean checkCherry(int a, int b, Tree tree)
    {
        if(tree.getNode(a).size() == 0 || tree.getNode(b).size() == 0){return false;}
        int v1 = tree.getNode(a).get(0).getVertex();
        int v2 = tree.getNode(b).get(0).getVertex();
        return v1 == v2 || v1 == b;//also find a--b cherries
        
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
                if(leaves[0] < leaves[1]){return new int[]{leaves[0],leaves[1]};}
                else{return new int[]{leaves[1],leaves[0]};}
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

    public void reverseOperations(List<Operation> operations)
    {
        Collections.reverse(operations);
        for(Operation op : operations)
        {
            op.revert();
        }
    }
}
