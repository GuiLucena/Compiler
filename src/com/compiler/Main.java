package com.compiler;
import com.compiler.parser.InvalidExpressionException;
import com.compiler.parser.Parser;
import com.compiler.scanner.InvalidTokenException;
import com.compiler.scanner.Scanner;
import com.compiler.scanner.Token;
import com.compiler.scanner.TokenClassification;
import com.compiler.semantic.SemanticException;
import com.compiler.util.FileReader;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader();
        try {
            fileReader.open(args[0]);
            Scanner scanner = new Scanner(fileReader);
            Parser parser = new Parser(scanner);
            parser.parse();
            System.out.println("compilação concluida com sucesso");
        }catch (InvalidExpressionException |InvalidTokenException |SemanticException |IOException ex ){
            System.out.println(ex.getMessage());
        }
        finally {
            fileReader.close();
        }
    }
}
