package code;

public class debug {
    public static void main(String[] args) {
        // [1, 42, 47, -49][1, -4, 42, 47]
        Tree tree = new Tree();
        Tree forest = new Tree();

        tree.addNode(1);
        tree.addNode(42);
        tree.addNode(-1);
        tree.addNode(2);
        tree.addEdge(42, -1);
        tree.addEdge(1, -1);
        tree.addEdge(2, -1);



        forest.addNode(1);
        forest.addNode(42);
        forest.addNode(2);
        forest.addEdge(1, 42);

        TreeOperationsRedo to = new TreeOperationsRedo();
        tree.printDeg();
        forest.printDeg();

        to.reduceCommonCherries(tree, forest);
        
        tree.printDeg();
        forest.printDeg();

    }
}
