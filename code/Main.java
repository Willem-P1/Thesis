package code;
import java.util.*;
import code.TreeOperations.Operation;

public class Main {

    public static int a,b,c;
    public static double t,t2;
    public static void main(String[] args) {
        // testPathFinding();
        boolean DEBUG = false;
        boolean testAll = false;
        boolean useRandom = false;
        int n = 20;//default n value
        for(String s : args)
        {
            if(s.equals("-d")){DEBUG = true;}
            else if(s.equals("-r")){useRandom = true;}
            else if(s.equals("-a")){testAll = true;}
        }
        Parser l;
        if(DEBUG){
            if(useRandom){runOneRandom("code/test.txt",n);}
            else{runOne("code/test.txt");}
        }else{
            if(useRandom){
                if(testAll){
                    runAllRandom(n);
                }else{
                    runOneRandom(args[0],n);
                }
            }else
            {
                if(testAll){
                    runAll();
                }else{
                    runOne(args[0]);
                }
            }           
        }

        System.out.println(a + ", " + b + ", " + c);
        // System.out.println("after MAF");
        // System.out.println(trees[0]);
    }
    public static void runAllRandom(int n)
    {
        // String path = "kernelizing-agreement-forests-main\\code\\maindataset\\";
        String path = "kernelizing-agreement-forests-main\\code\\largetreedataset\\";
        String[] xNum = {"500", "1000", "1500", "2000", "2500", "3000"};//"50","100", "150","200", "250", "300", "350",
        String[] tbr = {"35"};//,"15","20, "30","};
        String[] skew = {"50","70","90"};
        String[] id = {"01","02","03","04", "05"};

        for(String x : xNum)
        {
            for(String t : tbr)
            {
                for(String s : skew)
                {
                    for(String i : id)
                    {
                        List<String> list = Arrays.asList("TREEPAIR",x,t,s,i);
                        String name = String.join("_", list);
                        System.out.print(name + ", ");
                        runOneRandom(path + name + ".tree", n);
                    }
                }
            }
        }
    }
    public static void runAll()
    {
        String path = "D:\\UM\\Thesis\\Thesis\\kernelizing-agreement-forests-main\\code\\maindataset\\";
        String[] xNum = {"50","100","150","200", "250", "300", "350"};
        String[] tbr = {"5","10"};//,"15","20"};
        String[] skew = {"50","70","90"};
        String[] id = {"01","02","03","04", "05"};

        for(String x : xNum)
        {
            for(String t : tbr)
            {
                for(String s : skew)
                {
                    for(String i : id)
                    {
                        List<String> list = Arrays.asList("TREEPAIR",x,t,s,i);
                        String name = String.join("_", list);
                        System.out.print(name + ", ");
                        runOne(path + name + ".tree");
                    }
                }
            }
        }
    }
    public static void runOneRandom(String path, int n)
    {
        TreeOperations to = new TreeOperations();
        int count = 0;
        t = 0;
        t2 = 0;
        // System.out.println(trees[0]);
        int min = Integer.MAX_VALUE;
        //for(int i = 0;i < n;i++)
        long startTime = System.nanoTime();
        long endTime = startTime;


        Parser l = new Parser(path);
        Tree[] trees  = l.parse();

        to.reduceCommonCherries(trees[0], trees[1]);
        to.suppressDeg2Vertex(trees[0], -2);
        to.suppressDeg2Vertex(trees[1], -2);
        // for(int i =0; i < n; i++)
        while(endTime - startTime < 60e9)
        {
            long startTime2 = System.nanoTime();

            count++;
            Tree tree = trees[0].copy();
            Tree forest = trees[1].copy();
            
            long endTime2 = System.nanoTime();
            double total = endTime2 - startTime2;
            total /= 1.0e9;
            Main.t2 += total;
            

            //TODO:make copying the tree after reduction possible to skip parsing

            int result = to.MCTBR(tree, forest, new int[0][0], 0);
            if(result < min)
                min = result;
            
            endTime =  System.nanoTime();
        }
        System.out.println("k=" + min + ", count=" + count + ", t=" + t + ", t2=" + t2);
    }
    public static void runOne(String path)
    {
        Parser l = new Parser(path);
        Tree[] trees  = l.parse();
        TreeOperations to = new TreeOperations();
        to.reduceCommonCherries(trees[0], trees[1]);
        to.suppressDeg2Vertex(trees[0], -2);
        to.suppressDeg2Vertex(trees[1], -2);
        // System.out.println(trees[0]);
        for(int i = 0; i <= 15; i++){
            boolean result = to.MAF(trees[0], trees[1], new int[0][0], i);
            if(result){
                System.out.println("k=" + i);
                break;
            }
        }
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


        TreeOperations to = new TreeOperations();
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
        TreeOperations to = new TreeOperations();
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
        TreeOperations to = new TreeOperations();
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
