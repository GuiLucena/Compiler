package com.compiler.semantic;

import com.compiler.scanner.TokenClassification;

/**
 * Created by guilherme on 10/11/15.
 */
public class IncompatibleTypesException extends SemanticException {
    public IncompatibleTypesException(TokenClassification expected,TokenClassification found,int line ,int column ) {
        super(String.format("incompatible types %s and %s at line:%d and column:%d"
                ,expected.toString(),found.toString(),line,column));
    }
}
