package code;

import java.util.*;
import code.TreeOperations.Operation;

public class MCTS {
    final float c = 1.41f;
    TreeOperations to = new TreeOperations();
    private Tree tree;
    private Tree forest;
    int k = 0;

    public class Node{
        Node parent;
        List<Node> children;
        int visits;
        int score;

        public Node(){
            children = new ArrayList<>();
        }
    }

    public int mctsMain(Tree tree, Tree forest, int k, int maxT)
    {
        this.tree = tree;
        this.forest = forest;
        Node root = new Node();
        while(tree.size() > 2)
        {
            int move = monte_carlo_tree_search(root, k, maxT);
            root = root.children.get(move);
            List<Operation> operations = new ArrayList<>();
            System.out.println(move);
            k = to.doOp(tree, forest, move, k, operations);
        }
        return k;
    }

    public int monte_carlo_tree_search(Node root, int startK, int t){
        long startTime = System.nanoTime();
        long endTime = startTime;
        while((endTime - startTime) < t * 1e9){
            List<Operation> operations = new ArrayList<>();
            k = startK;
            Node leaf = traverse(root, operations);
            
            //run simulation for all new children
            rollout(leaf);

            to.reverseOperations(operations);
            endTime = System.nanoTime();
        }
        return best_child(root);
    }
 
    // function for node traversal
    public Node traverse(Node node, List<Operation> operations){
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
    public void rollout(Node node){
        //TODO: create all children and stuffnstuff
        if(tree.size() <= 2){return;}
        int[] ab = to.findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        List<Integer> path = to.findPath(forest, a, b);

        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();

        createChildAndBackpropagate(node, new int[][]{{a,parentA}});

        createChildAndBackpropagate(node, new int[][]{{b,parentB}});

        if(path == null){return;}//if path is null we do not need to chech the pendant nodes for the mcts tree

        List<int[]> pendant = to.getPendantNodes(forest, path);
        
        for(int i = 0; i < pendant.size();i++)
        {
            int[][] edges = new int[pendant.size()-1][2];
            int index = 0;
            for(int l = 0; l < pendant.size();l++)
            {
                if(l == i) continue;
                edges[index++] = pendant.get(l);
            }
            createChildAndBackpropagate(node, edges);
        }
    }
    
    public void createChildAndBackpropagate(Node parent, int[][] edgesToRemove)
    {
        List<Operation> operations = new ArrayList<>();
        for(int i = 0; i < edgesToRemove.length; i++)
        {
            operations.add(to.removeEdge(forest, edgesToRemove[i][0], edgesToRemove[i][1]));

            if(edgesToRemove[i][0] < 0){operations.add(to.suppressDeg2Vertex(forest, edgesToRemove[i][0]));}
            if(edgesToRemove[i][1] < 0){operations.add(to.suppressDeg2Vertex(forest, edgesToRemove[i][1]));}
        }
        k += edgesToRemove.length;

        operations.addAll(to.reduce(tree, forest));

        Tree T = tree.copy();
        Tree F = forest.copy();
        int res = to.MCTBR(T, F, new int[0][0], k);
        Node node = new Node();
        parent.children.add(node);
        node.parent = parent;
        backpropagate(node, res);

        to.reverseOperations(operations);
        k -= edgesToRemove.length;
    }
    // function for backpropagation
    public void backpropagate(Node node, int result){
        node.visits++;
        node.score += result;
        if (node.parent == null)
            return;
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
