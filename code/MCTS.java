package code;

import java.util.*;
import code.TreeOperations.Operation;

public class MCTS {
    final float c = 1.41f*10;
    TreeOperations to = new TreeOperations();
    private Tree tree;
    private Tree forest;
    int k = 0;

    public class Node{
        Node parent;
        List<Node> children;
        int visits;
        int score;
        int move;
        public Node(int move){
            children = new ArrayList<>();
            this.move = move;
        }
    }

    public int mctsMain(Tree tree, Tree forest, int k, int maxT)
    {
        this.tree = tree;
        this.forest = forest;
        Node root = new Node(-1);
        int count= 0;
        while(tree.size() > 2)
        {
            System.out.println("Size: " + tree.size());

            int move = monte_carlo_tree_search(root, k, maxT);
            root = root.children.get(move);
            List<Operation> operations = new ArrayList<>();
            System.out.println("end of mcts exploration: " + move + ", " + count++);
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
            // System.out.println("reset");
            Node leaf = traverse(root, operations);
            
            //run simulation for all new children
            rollout(leaf);

            to.reverseOperations(operations);
            // System.out.println(forest.size());

            endTime = System.nanoTime();
        }
        return best_child(root);
    }
 
    // function for node traversal
    public Node traverse(Node node, List<Operation> operations){
        while(node.children.size() > 0){
            int index = best_uct(node);
            // System.out.println("Problem here? " + node.children.size());
            node = node.children.get(index);
            // System.out.println("node: " + node.move);
            to.doOp(tree, forest, index, k, operations);
        }
        // in case no children are present / node is terminal
        return node;
    }

    public int best_uct(Node node)
    {
        int index = -1;
        float best = -Float.MAX_VALUE;
        for(int i = 0;i < node.children.size();i++)
        {
            float uct = calcUct(node.children.get(i));
            if(uct > best)
            {
                index = i;
                best = uct;
            }
        }
        return index;
    }

    public float calcUct(Node node)
    {
        float uct = ((float)node.score)/node.visits;
        uct += c * Math.sqrt(Math.log(node.parent.visits)/node.visits);
        return uct;
    }

    // function for the result of the simulation
    public void rollout(Node node){
        //If its a terminal node, backpropagate immediately since it cannot be expanded
        if(tree.size() <= 2)
        {
            backpropagate(node, k);
            return;
        }

        int[] ab = to.findCherry(tree);
        int a = ab[0];
        int b = ab[1];
        List<Integer> path = to.findPath(forest, a, b);

        int parentA = forest.getNode(a).get(0).getVertex();
        int parentB = forest.getNode(b).get(0).getVertex();
        int move = 0;
        createChildAndBackpropagate(node, new int[][]{{a,parentA}}, move);
        move++;
        createChildAndBackpropagate(node, new int[][]{{b,parentB}}, move);
        move++;
        
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
            createChildAndBackpropagate(node, edges, move);
            move++;

        }

    }
    
    public void createChildAndBackpropagate(Node parent, int[][] edgesToRemove, int move)
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
        Node node = new Node(move);
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
            System.out.print((node.children.get(i).score/node.children.get(i).visits) + " ");
            if(node.children.get(i).visits > count)
            {
                max = i;
                count = node.children.get(i).visits;
            }
        }
        return max;
    }
}
