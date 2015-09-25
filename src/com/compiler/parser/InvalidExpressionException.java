package com.compiler.parser;

import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;

/**
 * Created by guilherme on 23/09/15.
 */
public class InvalidExpressionException extends Exception {

    public InvalidExpressionException(Token token,String expected,int line,int column) {
        super(String.format("Unexpected token %s at line: %d column: %d  , EXPECTED: %s",
                token.getLexeme(),line,column,expected));
    }
}
