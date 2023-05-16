package code;

import java.util.*;
import code.TreeOperations.Operation;

public class MCTS {
    final float c = 1.41f;
    TreeOperations to = new TreeOperations();
    private Tree tree;
    private Tree forest;

    public class Node{
        Node parent;
        List<Node> children;
        int visits;
        int score;
    }

    public int mctsMain(Tree tree, Tree forest, int k, int maxT)
    {
        this.tree = tree;
        this.forest = forest;
        return 0;
    }

    public int monte_carlo_tree_search(Node root, int t){
        long startTime = System.nanoTime();
        long endTime = startTime;
        while((endTime - startTime) < t * 1e9){
            List<Operation> operations = new ArrayList<>();
            int k = 0;
            Node leaf = traverse(root, k, operations);
            
            //run simulation for all new children
            int res = rollout(leaf);

            backpropagate(leaf, res);
            to.reverseOperations(operations);
            endTime = System.nanoTime();
        }
        return best_child(root);
    }
 
    // function for node traversal
    public Node traverse(Node node, int k, List<Operation> operations){
        while(node.children.size() > 0){
            int index = best_uct(node);
            node = node.children.get(index);
            to.doOp(tree, forest, index, k, operations);
        }
        // in case no children are present / node is terminal
        return node;
    }

    public int best_uct(Node node)
    {
        int max = -1;
        int best = -1;
        for(int i = 0;i < node.children.size();i++)
        {
            if(calcUct(node.children.get(i)) > best)
            {
                max = i;
                best = node.children.get(i).visits;
            }
        }
        return max;
    }

    public float calcUct(Node node)
    {
        float uct = ((float)node.score)/node.visits;
        uct += c * Math.sqrt(Math.log(node.parent.visits)/node.visits);
        return uct;
    }

    // function for the result of the simulation
    public int rollout(Node node){
        //TODO: create all children and stuffnstuff
        Tree T = tree.copy();
        Tree F = forest.copy();
        return to.MCTBR(T, F, new int[0][0], 0);
    }
    
    // function for backpropagation
    public void backpropagate(Node node, int result){
        if (node.parent == null)
            return;
        node.visits++;
        node.score += result;
        backpropagate(node.parent, result);
    }
    // function for selecting the best child
    // node with highest number of visits
    public int best_child(Node node){
        // pick child with highest number of visits
        int max = -1;
        int count = -1;
        for(int i = 0;i < node.children.size();i++)
        {
            if(node.children.get(i).visits > count)
            {
                max = i;
                count = node.children.get(i).visits;
            }
        }
        return max;
    }
}
