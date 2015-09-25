package com.compiler.scanner;

import java.lang.reflect.Type;

/**
 * Created by guilherme on 01/09/15.
 */
public enum TokenClassification {
    //literal values
    CHAR_VALUE,
    FLOAT_VALUE,
    INT_VALUE,

    //reserved words token
    MAIN,
    IF,
    ELSE,
    WHILE,
    DO,
    FOR,
    INT,
    FLOAT,
    CHAR,

    //especial tokens
    PARENTESIS_OPEN,
    PARENTESIS_CLOSE,
    BRACKETS_OPEN,
    BRACKETS_CLOSE,
    COMMA,
    SEMICOLON,

    //ARITMETIC
    PLUS,
    SUB,
    MULT,
    DIV,
    ASSIGNING,

    //OPERATIONAL
    BIGGER,
    BIGGER_EQUAL,
    SMALLER,
    SMALLER_EQUAL,
    DIFFERENT,
    EQUAL,

    //VARIABLES
    ID,

    //file end
    EOF;




    public static boolean isType(TokenClassification token){
        if(token == FLOAT || token ==  INT || token == CHAR){
            return  true;
        }
        return  false;
    }

    public static boolean isCommand(TokenClassification token){
        if(isBasicCommand(token) || isConditional(token) || isIteration(token)){
            return true;
        }
        return false;
    }

    public static boolean isBasicCommand(TokenClassification token){
        if (token == ID || token == BRACKETS_OPEN){
            return  true;
        }
        return false;
    }

    public static boolean isIteration(TokenClassification token){
        if(token == WHILE || token == DO){
            return true;
        }
        return false;
    }

    public static boolean isConditional(TokenClassification token){
        if(token == IF){
            return true;
        }
        return false;
    }

    public static boolean isFactor(TokenClassification token ){
        if(token == PARENTESIS_OPEN ||
                token == ID ||
                token == FLOAT_VALUE ||
                token == INT_VALUE ||
                token == CHAR_VALUE){
            return  true;
        }
        return false;
    }


    public static boolean isRelationalExpression(TokenClassification classfication) {
        return isFactor(classfication);
    }

    public static boolean isArithimeticExpression(TokenClassification classfication) {
        return isFactor(classfication);
    }

    public static boolean isRelationalOperator(TokenClassification classfication) {
        if (classfication == BIGGER ||
                classfication == BIGGER_EQUAL||
                classfication == SMALLER ||
                classfication == SMALLER_EQUAL||
                classfication == DIFFERENT ||
                classfication == EQUAL){
            return true;
        }
        return false;
    }
}
