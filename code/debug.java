package code;

public class debug {
    public static void main(String[] args) {
        // [1, 42, 47, -49][1, -4, 42, 47]
        Tree tree = new Tree();
        Tree forest = new Tree();

        tree.addNode(1);
        tree.addNode(42);
        tree.addNode(47);
        tree.addNode(-49);
        tree.addEdge(-49, 1);
        tree.addEdge(-49, 42);
        tree.addEdge(-49, 47);



        forest.addNode(1);
        forest.addNode(-4);
        forest.addNode(42);
        forest.addNode(47);

        forest.addEdge(-4, 1);
        forest.addEdge(-4, 42);
        forest.addEdge(-4, 47);

        TreeOperationsRedo to = new TreeOperationsRedo();

        to.reduceCommonCherries(tree, forest);
    }
}
