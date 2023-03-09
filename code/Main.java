package code;

import code.Lexer.Token;
import code.Lexer.TokenType;

public class Main {
    public static void main(String[] args) {
        Lexer l = new Lexer("code/test.txt");
        Token token = null;
        do
        {
            token = l.next();
            System.out.println(token.type.name() + " " + token.lexeme);
        }
        while(token.type != TokenType.EOI);
    }
}
