package code;

import java.util.*;

public class Tree {
    Map<Integer, List<Integer>> leaves;

    public Tree()
    {
        leaves = new HashMap<>();
    }

    public void addNode(String label)
    {
        System.out.println(label);
    }

}
