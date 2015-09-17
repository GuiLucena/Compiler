package com.compiler.scanner;

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
    EOF



}
