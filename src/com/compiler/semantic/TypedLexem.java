package com.compiler.semantic;

import com.compiler.scanner.TokenClassification;

/**
 * Created by guilherme on 08/11/15.
 */
public class TypedLexem {
    public TokenClassification type;
    public String lexem;

    public TypedLexem(TokenClassification type, String lexem) {
        this.type = type;
        this.lexem = lexem;
    }

    public TokenClassification getType() {
        return type;
    }

    public void setType(TokenClassification type) {
        this.type = type;
    }

    public String getLexem() {
        return lexem;
    }

    public void setLexem(String lexem) {
        this.lexem = lexem;
    }
}
