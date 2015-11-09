package com.compiler.semantic;

import com.compiler.scanner.TokenClassification;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by guilherme on 08/11/15.
 */
public class SymbolTable {

    Stack<HashMap<String, TypedLexem>> scopeStack;

    public SymbolTable() {
        this.scopeStack = new Stack<>();
    }

    public void createScope(){
        scopeStack.push(new HashMap<>());
    }

    public void deleteScope(){
        scopeStack.pop();
    }

    public TypedLexem searchLexem(String lexemName){
        for(int i = scopeStack.size() -1 ; i >= 0 ; i--){
            HashMap<String, TypedLexem> scope = scopeStack.get(i);
            TypedLexem lexem = scope.get(lexemName);
            if(lexem != null){
                return  lexem;
            }
        }
        return null;
    }

    public void insertLexem(String lexem, TokenClassification type){
        HashMap<String,TypedLexem> scope = scopeStack.peek();
        scope.put(lexem,new TypedLexem(type,lexem));
    }

    public boolean lexemExistInActualScope(String lexem){
        HashMap<String,TypedLexem> scope = scopeStack.peek();
        TypedLexem typedLexem = scope.get(lexem);
        if(typedLexem == null){
            return false;
        }
        else {
            return true;
        }
    }

}