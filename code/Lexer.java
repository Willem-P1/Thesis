package code;
import java.io.*;

public class Lexer
{
    private char c;
    private BufferedReader br;
    private boolean eof = false;
    public Lexer(String filepath)
    {
        try{
        FileReader fr = new FileReader(new File(filepath));
        br = new BufferedReader(fr);
        readNextChar();
        }
        catch(Exception e)
        {
            System.out.println("Error reading file: " + filepath);
        }
    }

    public enum TokenType
    {
        Semicolon,
        ParentisiesL,
        ParentisiesR,
        Comma,
        Label,
        newLine,
        EOI
    }

    public class Token
    {
        public TokenType type;
        public String lexeme;

        public Token(TokenType tokenType, String lexeme)
        {
            this.type = tokenType;
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return lexeme;
        }
    }

    public void readNextChar()
    {
        try{
            int cint;
            if((cint = br.read()) != -1)
            {
                c = (char) cint;
                // System.out.println(cint);
            }
            else
            {
                System.out.println("End of file!!");
                eof = true;
                c = '\n';
            }
        }
        catch(Exception e)
        {
            System.out.println("Error reading char");
        }
    }

    public Token next()
    {
        Token token;
        if(eof == true)
        {
            token = new Token(TokenType.EOI, "EOI");
        }else if(c == '\r') //skip this demon
        {   
            readNextChar();
            token = next();
        }
        else if(c == '\n')
        {
            token = new Token(TokenType.newLine, "\n");
            readNextChar();
        }
        else if(c == ';')
        {
            token = new Token(TokenType.Semicolon, ";");
            readNextChar();
        }else if(c == '(')
        {
            token = new Token(TokenType.ParentisiesL, "(");
            readNextChar();
        }else if(c == ')')
        {
            token = new Token(TokenType.ParentisiesR, ")");
            readNextChar();
        }else if(c == ',')
        {
            token = new Token(TokenType.Comma, ",");
            readNextChar();
        }else
        {
            token = label();
        }
        
        return token;
    }

    public Token label()
    {
        String labelString = "";
        while(c >= 48 && c < 58)
        {
            labelString += c;
            readNextChar();
        }
        return new Token(TokenType.Label, labelString);
    }
}