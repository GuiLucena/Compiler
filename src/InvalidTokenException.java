/**
 * Created by guilherme on 07/09/15.
 */
public class InvalidTokenException extends Exception {

    private Token lastToken;
    private String lexeme;
    private int column;
    private int line;

    public InvalidTokenException(String message, Token lastToken, String lexeme, int line, int column) {
        super(message);
        this.lastToken = lastToken;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public String getMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append(super.getMessage())
                .append(" ")
                .append(lexeme)
                .append(" at line: ")
                .append(this.line)
                .append(" column: ")
                .append(this.column);
        if(this.lastToken != null){
            builder.append(" last token: ")
                   .append(this.lastToken.toString());
        }
        return builder.toString();
    }

    public Token getLastToken() {
        return lastToken;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
}
