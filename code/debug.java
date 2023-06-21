package code;
import java.util.*;
import code.TreeOperations.Operation;

public class debug {
    public static void main(String[] args) {
        // [1, 42, 47, -49][1, -4, 42, 47]
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperations to = new TreeOperations();
        to.reduce(trees[0], trees[1]);
        to.suppressDeg2Vertex(trees[0], -2);
        to.suppressDeg2Vertex(trees[1], -2);
        
        MCTS mcts = new MCTS();
        List<Operation> operations = new ArrayList<>();

        System.out.println(mcts.mctsMain(trees[0], trees[1],0,1));
        // to.doOp(trees[0], trees[1], 1, 0, operations);
        // to.doOp(trees[0], trees[1], 3, 0, operations);
        // to.doOp(trees[0], trees[1], 3, 0, operations);
        // to.doOp(trees[0], trees[1], 0, 0, operations);
        // to.doOp(trees[0], trees[1], 0, 0, operations);
        // to.doOp(trees[0], trees[1], 0, 0, operations);
        // to.doOp(trees[0], trees[1], 0, 0, operations);
    }
}
