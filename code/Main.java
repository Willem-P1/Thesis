package code;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        // testPathFinding();
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperations to = new TreeOperations();

        System.out.println(trees[0]);
        trees[0].removeNode(-3);
        System.out.println(to.findPath(trees[0], 16, 12));
        System.out.println(to.findPath(trees[1], 16, 12));
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
