package com.compiler.util;

/**
 * Created by guilherme on 04/09/15.
 */
public final class CharacterValidations {

    private CharacterValidations() {
    }


    public static boolean isEmpty(char character){
        if(character == 0){
            return  true;
        }
        else {
            return  false;
        }
    }

    public static boolean isWthiteSpace(char character){
        if (character == ' '){
            return  true;
        }
        else if (character == '\n'){
            return  true;
        }
        else if (character == '\t'){
            return  true;
        }
        else  if (character == '\n'){
            return  true;
        }
        else {
            return  false;
        }
    }

    public static boolean isUnderscore(char character) {
        if (character == '_') {
            return true;
        }
        return false;
    }

    public static boolean isDot(char character){
        if (character == '.'){
            return  true;
        }
        return  false;
    }

    public static boolean isSingleQuote(char character){
        if (character == '\''){
            return  true;
        }
        return  false;
    }

    public static boolean isLineEnd(char character){
        if (character == '\n' || character == '\r'){
            return  true;
        }
        return  false;
    }

}
