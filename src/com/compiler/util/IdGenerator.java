package com.compiler.util;

/**
 * Created by guilherme on 10/11/15.
 */
public class IdGenerator {
    private  int id;
    private  String complement;

    public IdGenerator(String complement) {
        this.complement = complement;
    }

    public String generateId(){
        return complement+(id++);
    }
}
