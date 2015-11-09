package com.compiler.semantic;

/**
 * Created by guilherme on 08/11/15.
 */
public class DuplicatedIdException extends SemanticException {
    public DuplicatedIdException(String lexem, int line,int column) {
        super(String.format("duplicated id: %s at line: %d column %d",lexem,line,column));
    }
}
