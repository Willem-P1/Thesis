package code;
import java.util.*;
import code.TreeOperationsRedo.Operation;

public class Main {
    public static void main(String[] args) {
        // testPathFinding();
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
}
