package com.compiler.scanner;

/**
 * Created by guilherme on 01/09/15.
 */
public class Token {

    private TokenClassification classfication;
    private String lexeme;

    public Token(TokenClassification classfication,String lexeme) {
        this.lexeme = lexeme;
        this.classfication = classfication;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenClassification getClassfication() {
        return classfication;
    }

    @Override
    public String toString(){
        return "lexeme :" + this.lexeme + " classification: " + this.classfication.toString();
    }
}
