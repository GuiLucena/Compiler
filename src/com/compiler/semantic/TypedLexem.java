package com.compiler.semantic;

import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;

/**
 * Created by guilherme on 08/11/15.
 */
public class TypedLexem {
    public TokenClassification type;
    public String lexem;
    public TokenClassification operator;

    public TypedLexem(TokenClassification type, String lexem) {
        this.type = type;
        this.lexem = lexem;
        this.operator = null;
    }

    public TypedLexem() {
    }

    public TokenClassification getType() {
        return type;
    }

    public void setType(TokenClassification type) {
        this.type = type;
    }

    public String getLexem() {
        return lexem;
    }

    public void setLexem(String lexem) {
        this.lexem = lexem;
    }

    public static TypedLexem parseLiteralValueTokenToLexem(Token token){
        if(token.getClassfication() == TokenClassification.FLOAT_VALUE){
            return new TypedLexem(TokenClassification.FLOAT,token.getLexeme());
        }
        else if(token.getClassfication() == TokenClassification.INT_VALUE){
            return  new TypedLexem(TokenClassification.INT,token.getLexeme());
        }
        else if(token.getClassfication() == TokenClassification.CHAR_VALUE){
            return new TypedLexem(TokenClassification.CHAR,token.getLexeme());
        }
        else {
            throw new IllegalStateException("only int char and float are allowed");
        }
    }
}
