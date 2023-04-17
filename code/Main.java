package code;
import java.util.*;
import code.TreeOperationsRedo.Operation;

public class Main {
    public static void main(String[] args) {
        // testPathFinding();
        boolean DEBUG = false;
        for(String s : args)
        {
            if(s.equals("-d")){DEBUG = true;}
        }
        Parser l;
        if(DEBUG){
            l = new Parser("code/test.txt");
        }else{
            l = new Parser(args[0]);
        }

        Tree[] trees  = l.parse();
        TreeOperationsRedo to = new TreeOperationsRedo();
        to.reduceCommonCherries(trees[0], trees[1]);
        to.suppressDeg2Vertex(trees[0], -2);
        to.suppressDeg2Vertex(trees[1], -2);
        // System.out.println(trees[0]);
        
        for(int i = 0; i <= 15; i++){
            boolean result = to.MAF(trees[0], trees[1], new int[0][0], i);
            System.out.println("k=" + i + " : " + result);
            if(result)
                break;
        }
        // System.out.println("after MAF");
        // System.out.println(trees[0]);
    }

    public static void singletonRemovalTest()
    {
        Tree[] trees  = new Tree[2];
        trees[0] = new Tree();
        trees[1] = new Tree();
        trees[0].addNode(1);
        trees[0].addNode(2);
        trees[0].addNode(3);
        trees[0].addNode(4);

        trees[0].addNode(-2);
        trees[0].addEdge(-2, 1);
        trees[0].addEdge(-2, 2);
        trees[0].addEdge(-2, 3);
        
        trees[1].addNode(1);
        trees[1].addNode(4);


        TreeOperationsRedo to = new TreeOperationsRedo();
        System.out.println("singleton removal test");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        trees[0].printDeg();
        System.out.println("tree 2");
        System.out.println(trees[1]);
        trees[1].printDeg();

        List<Operation> ops = to.removeSingletons(trees[0], trees[1]);
        System.out.println("after singleton removal");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        trees[0].printDeg();
        System.out.println("tree 2");
        System.out.println(trees[1]);
        trees[1].printDeg();

        for(Operation op : ops)
        {
            op.revert();
        }

        System.out.println("after reversal:");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        trees[0].printDeg();
        System.out.println("tree 2");
        System.out.println(trees[1]);
        trees[1].printDeg();
    }

    public static void cherryReducionTest()
    {
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperationsRedo to = new TreeOperationsRedo();
        System.out.println("Cherry reduction test");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        System.out.println("tree 2");
        System.out.println(trees[1]);

        List<Operation> ops = to.reduceCommonCherries(trees[0], trees[1]);
        System.out.println("after reduction:");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        System.out.println("tree 2");
        System.out.println(trees[1]);
        
        Collections.reverse(ops);
        
        for(Operation op : ops)
        {
            op.revert();
        }

        System.out.println("after reversal:");
        System.out.println("tree 1");
        System.out.println(trees[0]);
        System.out.println("tree 2");
        System.out.println(trees[1]);
    }
    public static void testPathFinding()
    {
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperations to = new TreeOperations();
        int X = 50;
        int n = 10000;
        int[] array = new Random().ints(n, 1, X + 1).toArray();

        // System.out.println(trees[0]);
        long start = System.nanoTime();
        float sum = 0;
        for(int i = 0; i<array.length -1;i++)
        {
            sum += to.findPath(trees[0], array[i], array[i+1]).size();
        }
        long end = System.nanoTime();
        double time = (end - start)/1e6;
        System.out.println("time: " + time);
        System.out.println("avg len: " + sum/n);
        // System.out.println(to.findPath(trees[1], 16, 14));
    }

    public static void reversalTest()
    {
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperationsRedo to = new TreeOperationsRedo();
        //Routine to test tree opreations
        System.out.println("Suppression test");
        System.out.println(trees[0]);
        // trees[0].printDeg();
        to.suppressDeg2Vertex(trees[0], -2);
        System.out.println(trees[0]);
        System.out.println("Path test");
        List<Integer> path = to.findPath(trees[0],48, 21);
        System.out.println(path);
        List<int[]> pendantNodes = to.getPendantNodes(trees[0], path);
        for(int[] arr : pendantNodes)
        {
            System.out.print(arr[1] + " ");
        }
        System.out.print("\n");
        int[] cherry = to.findCherry(trees[0]);
        System.out.println(cherry[0] + ", " + cherry[1]);
        
        Operation op = to.removeCommonCherry(trees[0], trees[1], 50);
        System.out.println(trees[0]);
        op.revert();
        System.out.println("after reversal");
        System.out.println(trees[0]);
    }
}
