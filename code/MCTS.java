package code;

import java.util.*;

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
            Node leaf = traverse(root);
            int res = rollout(leaf);
            backpropagate(leaf, res);
            endTime = System.nanoTime();
        }
        return best_child(root);
    }
 
    // function for node traversal
    public Node traverse(Node node){
        while(node.children.size() > 0){
            node = best_uct(node);
        }
        // in case no children are present / node is terminal
        return pick_unvisited(node.children);
    }

    public Node best_uct(Node node)
    {
        Node max = null;
        int best = -1;
        for(int i = 0;i < node.children.size();i++)
        {
            if(calcUct(node.children.get(i)) > best)
            {
                max = node.children.get(i);
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
        while(node.children.size() > 0){
            node = rollout_policy(node);
        }
        Tree T = tree.copy();
        Tree F = forest.copy();
        return to.MCTBR(T, F, new int[0][0], 0);
    }
    // function for randomly selecting a child node
    public void rollout_policy(Node node){
        return pick_random(node.children);
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
