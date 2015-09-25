package com.compiler.parser;

import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;

import java.io.IOException;

/**
 * Created by guilherme on 22/09/15.
 */
public class Parser {

    private Scanner scanner;
    private Token token;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void parse() throws IOException, InvalidTokenException, InvalidExpressionException {
        lookAhead();
        programEvaluation();
    }

    private void programEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException {
        if (token.getClassfication() != TokenClassification.INT){
            throw builException(TokenClassification.INT.toString());
        }
        lookAhead();
        if (token.getClassfication() != TokenClassification.MAIN){
            throw builException(TokenClassification.MAIN.toString());
        }
        lookAhead();
        if (token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        blockEvaluation();
    }

    private void blockEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException {
        if (token.getClassfication() != TokenClassification.BRACKETS_OPEN){
            throw builException(TokenClassification.BRACKETS_OPEN.toString());
        }
        lookAhead();

        while (TokenClassification.isType(token.getClassfication())){
            typeEvaluation();
        }

        while (TokenClassification.isCommand(token.getClassfication())){
            commandEvaluation();
        }

        if (token.getClassfication() != TokenClassification.BRACKETS_CLOSE){
            throw  builException(TokenClassification.BRACKETS_CLOSE.toString());
        }
    }

    private void typeEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException {
        if (!TokenClassification.isType(token.getClassfication())){
            throw new IllegalStateException("function should be called when the token is a type");
        }
        lookAhead();
        if (token.getClassfication() != TokenClassification.ID){
            throw builException(TokenClassification.ID.toString());
        }
        lookAhead();
        while (token.getClassfication() != TokenClassification.SEMICOLON){
            if(token.getClassfication() != TokenClassification.COMMA){
                throw builException(TokenClassification.SEMICOLON.toString());
            }
            lookAhead();
            if (token.getClassfication() != TokenClassification.ID){
                throw  builException(TokenClassification.ID.toString());
            }
            lookAhead();
        }
        lookAhead();
    }

    private void commandEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException {
        if (TokenClassification.isBasicCommand(token.getClassfication())){
            basicCommandEvaluation();
        }
        else if(TokenClassification.isIteration(token.getClassfication())){
            iterationEvaluation();
        }
        else if(TokenClassification.isConditional(token.getClassfication())){
            conditionalEvaluation();
        }
        else{
            throw new IllegalStateException("Function should be called when the token can be a command");
        }
    }

    private void conditionalEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException {
        if(token.getClassfication() != TokenClassification.IF){
            throw builException(TokenClassification.IF.toString());
        }
        lookAhead();
        if (token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw  builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        if (TokenClassification.isRelationalExpression(token.getClassfication())){
            relationalExpressionEvaluation();
        }
        else{
            throw builException("RELATIONAL_EXPRESSION");
        }
        if (token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        if(!TokenClassification.isCommand(token.getClassfication())){
            throw builException("COMMAND");
        }
        commandEvaluation();
    }

    private void relationalExpressionEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException {
        if(TokenClassification.isArithimeticExpression(token.getClassfication())){
            arithimeticExpressionEvaluation();
        }
        else {
            throw builException("ARITHIMETIC_EXPRESSION");
        }
        if (!TokenClassification.isRelationalOperator(token.getClassfication())){
            throw builException("RELATIONAL_OPERATOR");
        }
        lookAhead();
        if(TokenClassification.isArithimeticExpression(token.getClassfication())){
            arithimeticExpressionEvaluation();
        }
        else {
            throw builException("ARITHIMETIC_EXPRESSION");
        }
    }

    private void arithimeticExpressionEvaluation() {
        //TODO: FAZER ESSA PORRA QUANDO ABRIR DINOVO CARALHO
        .AJDSLKJLSÇAJFLÇSA
    }

    private void iterationEvaluation() {

    }

    private void basicCommandEvaluation() {

    }

    private void lookAhead() throws IOException, InvalidTokenException {
        token = scanner.getNextToken();
    }

    private InvalidExpressionException builException(String expected) {
        return new InvalidExpressionException(token, expected, scanner.getLastLine(), scanner.getLastColumn());
    }


}
