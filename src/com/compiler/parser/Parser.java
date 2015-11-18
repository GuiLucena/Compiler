package com.compiler.parser;

import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;
import com.compiler.semantic.*;
import com.compiler.util.CharacterValidations;
import com.compiler.util.IdGenerator;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by guilherme on 22/09/15.
 */
public class Parser {

    private Scanner scanner;
    private Token token;
    private SymbolTable symbolTable;
    private IdGenerator variables;
    private IdGenerator labels;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.symbolTable = new SymbolTable();
        this.variables = new IdGenerator("T");
        this.labels = new IdGenerator("L");
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
        String ifLabel = labels.generateId();
        if (token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw  builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        if (!TokenClassification.isRelationalExpression(token.getClassfication())){
            throw builException("RELATIONAL_EXPRESSION");
        }
        TypedLexem relational = relationalExpressionEvaluation();
        if (token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        System.out.println(String.format("if %s == 0 goto %s",relational.getLexem(),ifLabel) );
        lookAhead();
        if(!TokenClassification.isCommand(token.getClassfication())){
            throw builException("COMMAND");
        }
        commandEvaluation();
        if (token.getClassfication() == TokenClassification.ELSE){
            String elseLabel = labels.generateId();
            System.out.println("goto " + elseLabel);
            System.out.println(ifLabel + ":");
            lookAhead();
            if(!TokenClassification.isCommand(token.getClassfication())){
                throw builException("COMMAND");
            }
            commandEvaluation();
            System.out.println(elseLabel + ":");
        }
        else{
            System.out.println(ifLabel + ":");
        }

    }

    private TypedLexem relationalExpressionEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isArithimeticExpression(token.getClassfication())){
            throw builException("ARITHIMETIC_EXPRESSION");
        }
        TypedLexem leftVar =  arithmeticExpressionEvaluation();
        if (!TokenClassification.isRelationalOperator(token.getClassfication())){
            throw builException("RELATIONAL_OPERATOR");
        }
        TokenClassification signal = token.getClassfication();
        lookAhead();
        if(!TokenClassification.isArithimeticExpression(token.getClassfication())){
            throw builException("ARITHIMETIC_EXPRESSION");
        }
        TypedLexem rightVar  = arithmeticExpressionEvaluation();
        return operateTypes(leftVar,signal,rightVar);
    }

    private TypedLexem arithmeticExpressionEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isTerm(token.getClassfication())){
            throw builException("TERM");
        }
        TypedLexem firstValue = termEvaluation();
        TypedLexem secondValue = arithmeticContinuation();
        if(secondValue == null){
            return  firstValue;
        }
        else {
            TypedLexem result = operateTypes(firstValue,secondValue.operator,secondValue);
            return  result;
        }
    }

    private TypedLexem arithmeticContinuation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(token.getClassfication() == TokenClassification.PLUS || token.getClassfication() == TokenClassification.SUB){
            Token operator = token;
            lookAhead();
            TypedLexem firstValue = termEvaluation();
            TypedLexem secondValue = arithmeticContinuation();
            if(secondValue == null){
                firstValue.operator = operator.getClassfication();
                return  firstValue;
            }
            else {
                TypedLexem result = operateTypes(firstValue,secondValue.operator,secondValue);
                result.operator = operator.getClassfication();
                return  result;
            }
        }
        return  null;
    }

    private TypedLexem termEvaluation() throws InvalidExpressionException, IOException, InvalidTokenException, SemanticException {
        if(!TokenClassification.isFactor(token.getClassfication())){
            throw  builException("FACTOR");
        }
        TypedLexem firstValue = factorEvaluation();
        TypedLexem secondValue = termContinuation();
        if(secondValue == null){
            return  firstValue;
        }
        else {
            TypedLexem result = operateTypes(firstValue,secondValue.operator,secondValue);
            return  result;
        }
    }

    private TypedLexem termContinuation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(token.getClassfication() == TokenClassification.DIV || token.getClassfication() == TokenClassification.MULT){
            Token operator = token;
            lookAhead();
            TypedLexem firstValue = factorEvaluation();
            TypedLexem secondValue = termContinuation();
            if(secondValue == null){
                firstValue.operator = operator.getClassfication();
                return  firstValue;
            }
            else {
                TypedLexem result = operateTypes(firstValue,secondValue.operator,secondValue);
                result.operator = operator.getClassfication();
                return  result;
            }
        }
        return null;
    }

    private TypedLexem factorEvaluation() throws IOException, InvalidTokenException, InvalidExpressionException, SemanticException {
        if(!TokenClassification.isFactor(token.getClassfication())){
            throw  builException("FACTOR");
        }
        TypedLexem lexem = null;
        if(token.getClassfication() == TokenClassification.PARENTESIS_OPEN){
            lookAhead();
            lexem = arithmeticExpressionEvaluation();
            if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
                throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
            }
            lookAhead();
        }
        else {
            if(token.getClassfication() == TokenClassification.ID){
               lexem =  getTypedId(token.getLexeme());
            }
            else{
                lexem = TypedLexem.parseLiteralValueTokenToLexem(token);
            }
            lookAhead();
        }
        return lexem;
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
        String startLabel = labels.generateId();
        System.out.println(startLabel+":");
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
        TypedLexem variable = relationalExpressionEvaluation();
        System.out.println(String.format("IF %s != 0 GOTO %s",variable.getLexem(),startLabel));
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
        String startLabel = labels.generateId();
        String finaltLabel = labels.generateId();
        System.out.println(startLabel+":");
        lookAhead();
        if(token.getClassfication() != TokenClassification.PARENTESIS_OPEN){
            throw builException(TokenClassification.PARENTESIS_OPEN.toString());
        }
        lookAhead();
        TypedLexem variable = relationalExpressionEvaluation();
        System.out.println(String.format("IF %s == 0 GOTO %s",variable.getLexem(),finaltLabel));
        if(token.getClassfication() != TokenClassification.PARENTESIS_CLOSE){
            throw builException(TokenClassification.PARENTESIS_CLOSE.toString());
        }
        lookAhead();
        commandEvaluation();
        System.out.println("GOTO "+startLabel);
        System.out.println(finaltLabel+":");
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
        TypedLexem var = arithmeticExpressionEvaluation();
        operateTypes(lexem,TokenClassification.ASSIGNING,var);
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

    private TypedLexem operateTypes(TypedLexem firstVariable,TokenClassification signal,TypedLexem secondVariable) throws SemanticException{
        TypedLexem result = null;
        if(firstVariable.getType() == TokenClassification.CHAR){
            if(secondVariable.getType() != TokenClassification.CHAR){
                throw new  IncompatibleTypesException(firstVariable.getType(),secondVariable.getType(),scanner.getLastLine(),scanner.getLastColumn());
            }
        }
        else if(firstVariable.getType() == TokenClassification.INT){
            if(secondVariable.getType() == TokenClassification.CHAR){
                throw new  IncompatibleTypesException(firstVariable.getType(),secondVariable.getType(),scanner.getLastLine(),scanner.getLastColumn());
            }
            else if(secondVariable.getType() == TokenClassification.FLOAT){
                if(signal == TokenClassification.ASSIGNING){
                    throw new  IncompatibleTypesException(firstVariable.getType(),secondVariable.getType(),scanner.getLastLine(),scanner.getLastColumn());
                }
                firstVariable = convertVariable(firstVariable,TokenClassification.FLOAT);
            }
            else if(secondVariable.getType() == TokenClassification.INT && signal == TokenClassification.DIV) {
                result = new TypedLexem(TokenClassification.FLOAT,variables.generateId());
            }
        }
        else if(firstVariable.getType() == TokenClassification.FLOAT){
            if(secondVariable.getType() == TokenClassification.CHAR){
                throw new  IncompatibleTypesException(firstVariable.getType(),secondVariable.getType(),scanner.getLastLine(),scanner.getLastColumn());
            }
            else if(secondVariable.getType() == TokenClassification.INT){
                secondVariable = convertVariable(secondVariable,TokenClassification.FLOAT);
            }
        }

        if(result == null && signal != TokenClassification.ASSIGNING){
            result = new TypedLexem(firstVariable.getType(),variables.generateId());
        }
        if(signal == TokenClassification.ASSIGNING){
            System.out.println(String.format("%s = %s",firstVariable.getLexem(),secondVariable.getLexem()));
            return null;
        }
        else {
            System.out.println(String.format("%s = %s %s %s",result.getLexem(),
                    firstVariable.getLexem(),parseSignalToString(signal),secondVariable.getLexem()));
            return  result;
        }
    }

    private TypedLexem convertVariable(TypedLexem lexem,TokenClassification type){
        TypedLexem result = new TypedLexem(type,variables.generateId());
        System.out.println(String.format("%s = %s %s",result.getLexem(),type.toString(),lexem.getLexem()));
        return result;
    }

    private InvalidExpressionException builException(String expected) {
        return new InvalidExpressionException(token, expected, scanner.getLastLine(), scanner.getLastColumn());
    }

    private String parseSignalToString(TokenClassification signal){
        switch (signal){
            case PLUS:
                return "+";
            case SUB:
                return "-";
            case MULT:
                return "*";
            case DIV:
                return "/";
            case BIGGER:
                return ">";
            case BIGGER_EQUAL:
                return ">=";
            case SMALLER:
                return "<";
            case SMALLER_EQUAL:
                return "<=";
            case DIFFERENT:
                return "!=";
            case EQUAL:
                return "==";
            default:
                throw new IllegalStateException("eita carai deu merda");
        }
    }


}
