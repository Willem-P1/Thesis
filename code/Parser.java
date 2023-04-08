package code;
import code.Lexer.Token;
import code.Lexer.TokenType;

public class Parser {
    
    private Lexer l;
    private Token token;
    private Token tokenPeek;
    private Tree currTree;
    private int currNode;//current internal node
    private int nodeGenerator;

    public Parser(String file)
    {
        l = new Lexer(file);
        token = l.next();
        currNode = -1;//initial root node
        nodeGenerator = -2;
    }

    public Tree[] parse()
    {
        return tree();
    }

    private Tree[] tree()
    {
        Tree tree1 = new Tree();
        Tree tree2 = new Tree();
        currTree = tree1;
        currTree.addNode(currNode);//add initial root node to make parsing easier
        
        subtree();
        match(TokenType.Semicolon);
        match(TokenType.newLine);
        
        currTree.removeNode(-1);//remove added root node
        currTree.setMinNode(nodeGenerator);
        //reset node numbers
        currNode = -1;
        nodeGenerator = -2;

        currTree = tree2;
        currTree.addNode(currNode);//add initial root node to make parsing easier

        subtree();
        match(TokenType.Semicolon);
        match(TokenType.EOI);

        currTree.removeNode(-1);//remove added root node
        currTree.setMinNode(nodeGenerator);

        return new Tree[]{tree1, tree2};
    }

    private void subtree()
    {
        if(token.type == TokenType.ParentisiesL)
        {
            internal();            
        }else
        {
            leaf();
        }
    }

    private void leaf()
    {
        name();
    }

    private void internal()
    {
        int prevNode = currNode;
        currNode = nodeGenerator--;

        currTree.addNode(currNode);
        currTree.addEdge(prevNode, currNode);
        
        match(TokenType.ParentisiesL);
        branchSet();
        match(TokenType.ParentisiesR);
        name();
        currNode = prevNode;
    }

    private void branchSet()
    {
        branch();
        if(matchIf(TokenType.Comma))
        {
            branchSet();
        }
    }

    private void branch()
    {
        subtree();
    }

    private void name()
    {
        if(token.type != TokenType.Label){return;}//can be empty

        int leaf = Integer.parseInt(this.token.lexeme);
        currTree.addNode(leaf);
        currTree.addEdge(currNode, leaf);
        match(TokenType.Label);
    }

    private void match(TokenType token)
    {
        if(this.token.type == token)
        {
            if(tokenPeek == null)
            {
                this.token = l.next();
            }else
            {
                this.token = tokenPeek;
                tokenPeek = null;
            }
        }else
        {
            throw new RuntimeException("Invalid token found: " + this.token);
        }
    }

    private boolean matchIf(TokenType token)
    {
        if(this.token.type == token)
        {
           match(token);
           return true;
        }
        return false;
    }

    private Token peek()
    {
        if(tokenPeek == null){tokenPeek = l.next();}
        return tokenPeek;
    }
}
