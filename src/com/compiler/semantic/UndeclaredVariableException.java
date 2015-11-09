package com.compiler.semantic;

/**
 * Created by guilherme on 08/11/15.
 */
public class UndeclaredVariableException extends SemanticException {
    public UndeclaredVariableException(String lexem,int line,int column) {
        super(String.format("Undeclared variable %s at line:%d and column:%d",lexem,line,column));
    }
}
