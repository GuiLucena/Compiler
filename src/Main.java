import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader();
        try {
            fileReader.open(args[0]);
            Scanner scanner = new Scanner(fileReader);
            Parser parser = new Parser(scanner);
            parser.parse();
            System.out.println("PARSE COMCLUIDO COM SUCESSO");
        }catch (InvalidExpressionException |InvalidTokenException | IOException ex){
            System.out.println(ex.getMessage());
        }
        finally {
            fileReader.close();
        }
    }
}
