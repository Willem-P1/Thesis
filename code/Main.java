package code;

public class Main {
    public static void main(String[] args) {
        Parser l = new Parser("code/test.txt");
        Tree[] trees  = l.parse();
        System.out.println(trees[0]);
    }
}
