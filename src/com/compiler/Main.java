package com.compiler;

import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;
import com.compiler.util.FileReader;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InvalidTokenException {
        FileReader fileReader = new FileReader();
        Token token = null;
        fileReader.open(args[0]);
        Scanner scanner = new Scanner(fileReader);
        try {
            do {
                token = scanner.getNextToken();
                if (token != null)
                    System.out.println(token.toString());
            } while (token.getClassfication() != TokenClassification.EOF);
        }catch (InvalidTokenException ex){
            System.out.println(ex.getMessage());
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        finally {
            fileReader.close();
        }
    }
}
