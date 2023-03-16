package code;

public class Main {
    public static void main(String[] args) {
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        TreeOperations to = new TreeOperations();
        System.out.println(trees[0]);
        System.out.println(trees[1]);
        // trees[0].supressDeg2Vertex();
        to.commonCherryReduction(trees[0],trees[1]);
        System.out.println(trees[0]);
        System.out.println(trees[1]);
    }
}
