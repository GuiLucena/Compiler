/*created by guilherme lucena*/
package com.compiler;
import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;
import com.compiler.util.FileReader;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader();
        try {
            fileReader.open(args[0]);
            Token token;
            Scanner scanner = new Scanner(fileReader);
            do {
                token = scanner.getNextToken();
            } while (token.getClassfication() != TokenClassification.EOF);
            System.out.println("scan completed");
        }catch (InvalidTokenException ex){
            System.out.println(ex.getMessage());
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        finally {
            fileReader.close();
        }
    }
}
