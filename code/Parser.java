package code;
import code.Lexer.Token;
import code.Lexer.TokenType;



public class Parser {
    private Lexer l;
    private Token token;
    private Token tokenPeek;
    private Tree currTree;
    public Parser(String file)
    {
        l = new Lexer(file);

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
        subtree();
        match(TokenType.Semicolon);
        match(TokenType.newLine);
        currTree = tree2;
        subtree();
        match(TokenType.Semicolon);
        match(TokenType.EOI);
        return new Tree[]{tree1, tree2};
    }

    private void subtree()
    {
        if(peek().type == TokenType.ParentisiesL){
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
        match(TokenType.ParentisiesL);
        branchSet();
        match(TokenType.ParentisiesR);
        name();
    }

    private void branchSet()
    {
        branch();
        if(peek().type == TokenType.Comma)
        {
            match(TokenType.Comma);
            branch();
        }
    }

    private void branch()
    {
        subtree();
    }

    private void name()
    {
        if(peek().type == TokenType.Label)
        {
            //TODO: something with a name
        }
    }

    private void match(TokenType token)
    {
        if(this.token.type == token)
        {
            if(tokenPeek == null)
            {
                this.token = l.next();
            }
            else
            {
                this.token = tokenPeek;
                tokenPeek = null;
            }
        }else
        {
            throw new RuntimeException("Invalid token found: " + this.token);
        }
    }

    private Token peek()
    {
        if (tokenPeek == null)
        {
            tokenPeek = l.next();
        }
        return tokenPeek;
    }
}
