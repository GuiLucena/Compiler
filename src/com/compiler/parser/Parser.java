package com.compiler.parser;

import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;
import com.compiler.semantic.*;

import java.io.IOException;

/**
 * Created by guilherme on 22/09/15.
 */
public class Parser {

    private Scanner scanner;
    private Token token;
    private SymbolTable symbolTable;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.symbolTable = new SymbolTable();
    }

    public void parse() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        lookAhead();
        programEvaluation();
        if(token.getClassfication() != TokenClassification.EOF){
            throw builException("END OF FILE");
        }
    }

    private void programEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
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

    private void blockEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if (token.getClassfication() != TokenClassification.BRACKETS_OPEN){
            throw builException(TokenClassification.BRACKETS_OPEN.toString());
        }
        symbolTable.createScope();
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
        symbolTable.deleteScope();
        lookAhead();
    }

    private void typeEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if (!TokenClassification.isType(token.getClassfication())){
            throw builException("TYPE");
        }
        TokenClassification type = token.getClassfication();
        lookAhead();
        if (token.getClassfication() != TokenClassification.ID){
            throw builException(TokenClassification.ID.toString());
        }
        insertIdInSymbolTable(token.getLexeme(), type);
        lookAhead();
        while (token.getClassfication() != TokenClassification.SEMICOLON){
            if(token.getClassfication() != TokenClassification.COMMA){
                throw builException(TokenClassification.SEMICOLON.toString());
            }
            lookAhead();
            if (token.getClassfication() != TokenClassification.ID){
                throw  builException(TokenClassification.ID.toString());
            }
            insertIdInSymbolTable(token.getLexeme(), type);
            lookAhead();
        }
        lookAhead();
    }

    private void commandEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
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
            throw builException("COMMAND");
        }
    }

    private void conditionalEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(token.getClassfication() != TokenClassification.IF){
            throw builException(TokenClassification.IF.toString());
        }
        lookAhead();
        if (token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw  builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        if (!TokenClassification.isRelationalExpression(token.getClassfication())){
            throw builException("RELATIONAL_EXPRESSION");
        }
        relationalExpressionEvaluation();
        if (token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        if(!TokenClassification.isCommand(token.getClassfication())){
            throw builException("COMMAND");
        }
        commandEvaluation();
        if (token.getClassfication() == TokenClassification.ELSE){
            lookAhead();
            if(!TokenClassification.isCommand(token.getClassfication())){
                throw builException("COMMAND");
            }
            commandEvaluation();
        }

    }

    private void relationalExpressionEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isArithimeticExpression(token.getClassfication())){
            throw builException("ARITHIMETIC_EXPRESSION");
        }
        arithmeticExpressionEvaluation();
        if (!TokenClassification.isRelationalOperator(token.getClassfication())){
            throw builException("RELATIONAL_OPERATOR");
        }
        lookAhead();
        if(!TokenClassification.isArithimeticExpression(token.getClassfication())){
            throw builException("ARITHIMETIC_EXPRESSION");
        }
        arithmeticExpressionEvaluation();
    }

    private void arithmeticExpressionEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isTerm(token.getClassfication())){
            throw builException("TERM");
        }
        termEvaluation();
        arithmeticContinuation();
    }

    private void arithmeticContinuation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(token.getClassfication() == TokenClassification.PLUS || token.getClassfication() == TokenClassification.SUB){
            lookAhead();
            termEvaluation();
            arithmeticContinuation();
        }
    }

    private void termEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isFactor(token.getClassfication())){
            throw  builException("FACTOR");
        }
        factorEvaluation();
        termContinuation();
    }

    private void termContinuation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(token.getClassfication() == TokenClassification.DIV || token.getClassfication() == TokenClassification.MULT){
            lookAhead();
            factorEvaluation();
            termContinuation();
        }
    }

    private void factorEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(!TokenClassification.isFactor(token.getClassfication())){
            throw  builException("FACTOR");
        }
        if(token.getClassfication() == TokenClassification.PARENTESIS_OPEN){
            lookAhead();
            arithmeticExpressionEvaluation();
            if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
                throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
            }
            lookAhead();
        }
        else {
            if(token.getClassfication() == TokenClassification.ID){
                getTypedId(token.getLexeme());
            }
            lookAhead();
        }
    }

    private void iterationEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isIteration(token.getClassfication())){
            throw builException("ITERATION");
        }
        if(token.getClassfication() == TokenClassification.WHILE){
           whileEValuation();
        }
        else {
            doWhileEvaluation();
        }
    }

    private void doWhileEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(token.getClassfication() != TokenClassification.DO){
            throw builException(TokenClassification.DO.toString());
        }
        lookAhead();
        commandEvaluation();
        if(token.getClassfication() != TokenClassification.WHILE){
            throw builException(TokenClassification.WHILE.toString());
        }
        lookAhead();
        if(token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        relationalExpressionEvaluation();
        if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        if(token.getClassfication() != TokenClassification.SEMICOLON){
            throw builException(TokenClassification.SEMICOLON.toString());
        }
        lookAhead();
    }

    private void whileEValuation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(token.getClassfication() != TokenClassification.WHILE){
            throw builException(TokenClassification.WHILE.toString());
        }
        lookAhead();
        if(token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        relationalExpressionEvaluation();
        if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        commandEvaluation();
    }

    private void basicCommandEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isBasicCommand(token.getClassfication())){
            throw builException("BASIC_COMMAND");
        }
        if(token.getClassfication() == TokenClassification.ID){
            assignmentEvaluation();
        }
        else {
            blockEvaluation();
        }
    }

    private void assignmentEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(token.getClassfication() != TokenClassification.ID){
            throw builException(TokenClassification.ID.toString());
        }
        TypedLexem lexem = getTypedId(token.getLexeme());
        lookAhead();
        if(token.getClassfication() != TokenClassification.ASSIGNING){
            throw builException(TokenClassification.ASSIGNING.toString());
        }
        lookAhead();
        arithmeticExpressionEvaluation();
        if(token.getClassfication() != TokenClassification.SEMICOLON){
           throw  builException("SEMICOLON");
        }
        lookAhead();
    }

    private void lookAhead() throws IOException, InvalidTokenException {
        token = scanner.getNextToken();
    }

    private void insertIdInSymbolTable(String lexem, TokenClassification type) throws SemanticException {
        if(symbolTable.lexemExistInActualScope(lexem)){
            throw new DuplicatedIdException(lexem,scanner.getLastLine(),scanner.getLastColumn());
        }
        symbolTable.insertLexem(lexem,type);
    }

    private TypedLexem getTypedId(String id) throws SemanticException{
        TypedLexem lexem = symbolTable.searchLexem(id);
        if(lexem == null){
            throw new  UndeclaredVariableException(id,scanner.getLastLine(),scanner.getLastColumn());
        }
        return lexem;
    }

    private InvalidExpressionException builException(String expected) {
        return new InvalidExpressionException(token, expected, scanner.getLastLine(), scanner.getLastColumn());
    }


}
