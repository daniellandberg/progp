/**
 *
 *
 * (Det här programmet skrevs av personer som normalt inte
 * skriver så enkel Java, så ha överseende om delar av koden är väldigt svår.
 * )
 *
 * Författare: Daniel Landberg och Fredrik Diffner
 */
public class Main {
    public static void main(String args[]) throws java.io.IOException, SyntaxError {
        Lexer lexer = new Lexer(System.in);
        Parser parser = new Parser(lexer);
        ParseTree result = parser.parse();
        // Parsning klar, gör vad vi nu vill göra med syntaxträdet
        Leona leona = new Leona();
        String output = result.process(leona);
        System.out.println(output);

    }
}
