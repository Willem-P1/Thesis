package code;

import java.security.Principal;
import java.util.*;
import code.TreeOperations.Operation;

public class MCTS {
    final float defaultC = 1.41f;
    TreeOperations to = new TreeOperations();
    private Tree tree;
    private Tree forest;
    int k = 0;

    public class Node{
        Node parent;
        List<Node> children;
        int visits;
        float score;
        float sqScore;
        int move;
        float max;
        float min;
        public Node(int move){
            min = 100000;
            max = -1;
            children = new ArrayList<>();
            this.move = move;
        }
    }

    public int mctsMain(Tree tree, Tree forest, int k, double maxT)
    {
        this.tree = tree;
        this.forest = forest;
        Node root = new Node(-1);
        double maxTime = 5;
        while(tree.size() > 2)
        {
            // System.out.println("Size: " + tree.size());

            int move = monte_carlo_tree_search(root, k, maxT);
            root = root.children.get(move);
            List<Operation> operations = new ArrayList<>();
            // System.out.println("move: " + move);
            k = to.doOp(tree, forest, move, k, operations);
            maxTime *= 0.975;
        }
        return k;
    }

    public int monte_carlo_tree_search(Node root, int startK, double t){
        long startTime = System.nanoTime();
        long endTime = startTime;
        //starting with 24 seconds and reducing it by a factor of 0.8 will result in a total time of 120 seconds after 30 iterations
        //TODO: figure out best formulation for this, might be that 0.5 or 0.9 is better
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
            // System.out.println("node: " + node.move + ", index: " + index);
            k = to.doOp(tree, forest, index, k, operations);
        }
        // in case no children are present / node is terminal
        return node;
    }

    public int best_uct(Node node)
    {
        int index = -1;
        float best = -Float.MAX_VALUE;
        //TODO: find best c value
        float c = 10f;//(node.max - node.min)/2;
        for(int i = 0;i < node.children.size();i++)
        {
            float uct = 0;
            uct = calcUct(node.children.get(i), c);
            // System.out.print("?uct");
            if(uct > best)
            {
                index = i;
                best = uct;
            }
        }
        if(index == -1){System.out.println(best);}
        return index;
    }

    public float calcUct(Node node, float c)
    {
        float avg = node.score/node.visits;
        // float s = node.sqScore/node.visits - (avg * avg);
        float uct = -avg;
        float div = (float) Math.log(node.parent.visits)/node.visits;
        uct += defaultC * c * Math.sqrt(div);// * Math.min(0.25f, s + 2 * div));
        // uct += Math.sqrt( div * Math.min(0.25f, s + 2 * div));
        // System.out.println(uct);

        return uct;
    }

    // function for the result of the simulation
    public void rollout(Node node){
        //If its a terminal node, backpropagate immediately since it cannot be expanded
        if(tree.size() <= 2)
        {
            backpropagate(node, k, -1);
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
        // if(res < 5)
        //     System.out.println(res);
        Node node = new Node(move);
        parent.children.add(node);
        node.parent = parent;
        backpropagate(node, res, -1);

        to.reverseOperations(operations);
        k -= edgesToRemove.length;
    }
    // function for backpropagation
    public void backpropagate(Node node, int result, float avg){
        node.visits++;
        node.score += result;
        node.sqScore += result*result;
        if(avg == -1)
        {
            avg = result;
        }
        if(node.max < avg)
        {
            node.max = avg;
        }
        if(node.min > avg)
        {
            node.min = avg;
        }
        if (node.parent == null)
            return;
        backpropagate(node.parent, result, node.score/node.visits);
    }
    // function for selecting the best child
    // node with highest number of visits
    public int best_child(Node node){
        // pick child with highest number of visits
        int max = -1;
        int count = -1;
        for(int i = 0;i < node.children.size();i++)
        {
            float avg = (node.children.get(i).score)/(node.children.get(i).visits);
            // System.out.print("[" + node.children.get(i).visits + ", " + avg + "] ");
            if(node.children.get(i).visits > count)
            {
                max = i;
                count = node.children.get(i).visits;
            }
        }
        return max;
    }
}
