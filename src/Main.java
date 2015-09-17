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
            System.out.println("compilação completa");
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
