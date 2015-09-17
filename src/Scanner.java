import java.io.IOException;

/**
 * Created by guilherme on 01/09/15.
 */
public class Scanner {

    private Token lastToken;
    private String buffer;
    private FileReader file;
    private char actualCharacter;
    private int lastLine;
    private int lastColumn;

    public Scanner(FileReader file) {
        this.file = file;
        this.buffer = "";
    }

    private void resetBuffer(){
        this.buffer = "";
    }


    private void lookAhead() throws  IOException{
        this.lastLine = file.getActualLine();
        this.lastColumn = file.getActualColumn();
        this.actualCharacter = file.getNextChar();
    }

    private void initializeCharacter() throws IOException {
        if(CharacterValidations.isEmpty(this.actualCharacter)){
            lookAhead();
        }
    }

    public Token getNextToken() throws IOException, InvalidTokenException {
        initializeCharacter();
        while (CharacterValidations.isWthiteSpace(this.actualCharacter)){
            lookAhead();
        }
        if(canBeIdOrReservedWord()){
            return verifyIdOrReservedWord();
        }
        else if(Character.isDigit(this.actualCharacter)){
            return verifyDigit();
        }
        else if(CharacterValidations.isDot(this.actualCharacter)){
            return verifyDot();
        }
        else if(CharacterValidations.isSingleQuote(this.actualCharacter)){
            return verifyCharacter();
        }
        else if(this.actualCharacter ==  '('){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.PARENTESIS_OPEN);
        }
        else if(this.actualCharacter ==  ')'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.PARENTESIS_CLOSE);
        }
        else if(this.actualCharacter ==  '{'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.BRACKETS_OPEN);
        }
        else if(this.actualCharacter ==  '}'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.BRACKETS_CLOSE);
        }
        else if(this.actualCharacter ==  '+'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.PLUS);
        }
        else if(this.actualCharacter ==  '*'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.MULT);
        }
        else if(this.actualCharacter ==  '-'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.SUB);
        }
        else if(this.actualCharacter ==  ';'){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.SEMICOLON);
        }
        else if(this.actualCharacter ==  ','){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.COMMA);
        }
        else if(this.actualCharacter ==  '='){
            return verifyEqual();
        }
        else if(this.actualCharacter == '>'){
            return verifyBigger();
        }
        else if (this.actualCharacter == '<'){
            return  verifySmaller();
        }
        else if (this.actualCharacter == '!'){
            return  verifyExclamation();
        }
        else if (this.actualCharacter == '/'){
            return  verifySlash();
        }
        else if(file.isEndOfFile(this.actualCharacter)){
            return new Token(TokenClassification.EOF,"EOF");
        }
        else{
            this.buffer += this.actualCharacter;
            throw  buildInvalidTokenException("Invalid Character");
        }
    }

    private Token verifySlash() throws IOException, InvalidTokenException {
        this.buffer += this.actualCharacter;
        lookAhead();

        if (this.actualCharacter == '/'){
            resetBuffer();
            return lineComment();
        }
        else if(this.actualCharacter == '*'){
            return textComment();
        }
        else{
            return buildToken(TokenClassification.DIV);
        }
    }

    private Token textComment() throws IOException, InvalidTokenException {
        String commentaryPattern = "\\/\\*(.|\\n|\\r)*\\*\\/";
        do {
            buffer += this.actualCharacter;
            lookAhead();
        }while (!file.isEndOfFile(this.actualCharacter) && !this.buffer.matches(commentaryPattern));

        if(this.buffer.matches(commentaryPattern)){
            resetBuffer();
            return getNextToken();
        }
        else{
            throw buildInvalidTokenException("open commentary");
        }
    }

    private Token lineComment() throws IOException, InvalidTokenException {
        do {
            lookAhead();
        }while (!file.isEndOfFile(this.actualCharacter) && !CharacterValidations.isLineEnd(this.actualCharacter));
        if(!file.isEndOfFile(this.actualCharacter)){
            lookAhead();
        }
        return getNextToken();
    }

    private Token verifyExclamation() throws IOException, InvalidTokenException {
        this.buffer += this.actualCharacter;
        lookAhead();
        this.buffer += this.actualCharacter;
        if (this.actualCharacter == '='){
            lookAhead();
            return buildToken(TokenClassification.DIFFERENT);
        }
        else{
            InvalidTokenException exception = buildInvalidTokenException("invalid exclamation");
            resetBuffer();
            throw exception;
        }
    }

    private Token verifySmaller() throws IOException {
        this.buffer += this.actualCharacter;
        lookAhead();
        if (this.actualCharacter == '='){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.SMALLER_EQUAL);
        }
        else{
            return buildToken(TokenClassification.SMALLER);
        }
    }

    private Token verifyBigger() throws IOException {
        this.buffer += this.actualCharacter;
        lookAhead();
        if (this.actualCharacter == '='){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.BIGGER_EQUAL);
        }
        else{
            return buildToken(TokenClassification.BIGGER);
        }
    }
    
    private Token verifyEqual() throws IOException {
        this.buffer += this.actualCharacter;
        lookAhead();
        if (this.actualCharacter == '='){
            this.buffer += this.actualCharacter;
            lookAhead();
            return buildToken(TokenClassification.EQUAL);
        }
        else{
            return buildToken(TokenClassification.ASSIGNING);
        }
        
    }

    private Token verifyCharacter() throws IOException, InvalidTokenException {
        this.buffer += this.actualCharacter;
        lookAhead();
        this.buffer += this.actualCharacter;
        if (Character.isLetterOrDigit(this.actualCharacter)){
            lookAhead();
            this.buffer += this.actualCharacter;
            if (CharacterValidations.isSingleQuote(this.actualCharacter)){
                lookAhead();
                return buildToken(TokenClassification.CHAR_VALUE);
            }
            else {
                InvalidTokenException exception = buildInvalidTokenException("invalid character");
                resetBuffer();
                throw exception;
            }
        }
        else {
            InvalidTokenException exception = buildInvalidTokenException("invalid character");
            resetBuffer();
            throw exception;
        }
    }

    private Token verifyDigit() throws IOException, InvalidTokenException {
        do{
            this.buffer += this.actualCharacter;
            lookAhead();
        }while (Character.isDigit(this.actualCharacter));

        if (this.actualCharacter == '.'){
            return verifyDot();
        }
        else{
            Token token = buildToken(TokenClassification.INT_VALUE);
            resetBuffer();
            return  token;
        }
    }

    private Token verifyDot() throws IOException, InvalidTokenException {
        this.buffer += this.actualCharacter;
        lookAhead();
        if (Character.isDigit(this.actualCharacter)){
            return verifyFloat();
        }
        else {
            this.buffer += this.actualCharacter;
            InvalidTokenException ex = buildInvalidTokenException("invalid Float");
            this.resetBuffer();
            throw  ex;
        }
    }

    private Token verifyFloat()throws IOException{
        do{
            this.buffer += this.actualCharacter;
            lookAhead();
        }while (Character.isDigit(this.actualCharacter));

        Token token = buildToken(TokenClassification.FLOAT_VALUE);
        resetBuffer();
        return  token;
    }

    private Token verifyIdOrReservedWord() throws IOException{
        Token token;
        do{
            this.buffer += this.actualCharacter;
            lookAhead();
        }while (isValidCharacer(this.actualCharacter));

        switch (this.buffer){
            case "main":
                token = buildToken(TokenClassification.MAIN);
                break;
            case "if":
                token = buildToken(TokenClassification.IF);
                break;
            case "else":
                token = buildToken(TokenClassification.ELSE);
                break;
            case "while":
                token = buildToken(TokenClassification.WHILE);
                break;
            case "do":
                token = buildToken(TokenClassification.DO);
                break;
            case "for":
                token = buildToken(TokenClassification.FOR);
                break;
            case "int":
                token = buildToken(TokenClassification.INT);
                break;
            case "float":
                token = buildToken(TokenClassification.FLOAT);
                break;
            case "char":
                token = buildToken(TokenClassification.CHAR);
                break;
            default:
                token = buildToken(TokenClassification.ID);
                break;
        }
        resetBuffer();
        return  token;
    }


    private Token buildToken(TokenClassification tokenClassification){
        this.lastToken = new Token(tokenClassification,this.buffer);
        resetBuffer();
        return  this.lastToken;
    }


    private InvalidTokenException buildInvalidTokenException(String message) throws InvalidTokenException {
        return new InvalidTokenException(message,lastToken,this.buffer,this.lastLine,
                this.lastColumn);
    }

    private boolean canBeIdOrReservedWord(){
        if (Character.isLetter(this.actualCharacter)
                || CharacterValidations.isUnderscore(this.actualCharacter)){
            return  true;
        }
        return false;
    }

    private boolean isValidCharacer(char character){
        if(Character.isLetter(character)){
            return true;
        }
        else if (Character.isDigit(character)){
            return  true;
        }
        else if (character == '_'){
            return  true;
        }
        else {
            return  false;
        }
    }
}
