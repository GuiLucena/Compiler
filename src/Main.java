import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InvalidTokenException {
        FileReader fileReader = new FileReader();
        try {
            fileReader.open(args[0]);
            Token token;
            Scanner scanner = new Scanner(fileReader);
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
