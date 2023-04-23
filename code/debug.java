package code;

public class debug {
    public static void main(String[] args) {
        // [1, 42, 47, -49][1, -4, 42, 47]
        Tree tree = new Tree();

        tree.addNode(1);
        tree.addNode(42);
        tree.addNode(-1);
        tree.addNode(2);
        tree.addEdge(42, -1);
        tree.addEdge(1, -1);
        tree.addEdge(2, -1);

        // TreeOperations to = new TreeOperations();
        System.out.println(tree);


        // to.reduceCommonCherries(tree, forest);
        
        Tree copy = tree.copy();

        System.out.println(copy);

    }
}
