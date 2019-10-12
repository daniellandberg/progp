import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.util.List;
        import java.util.ArrayList;
        import java.util.regex.Pattern;
        import java.util.regex.Matcher;

/**
 * En klass för att göra lexikal analys, konvertera indataströmmen
 * till en sekvens av tokens.  Den här klassen läser in hela
 * indatasträngen och konverterar den på en gång i konstruktorn.  Man
 * kan tänka sig en variant som läser indataströmmen allt eftersom
 * tokens efterfrågas av parsern, men det blir lite mer komplicerat.
 *
 * Författare: Per Austrin
 */
public class Lexer {
    private String input;
    private List<Token> tokens;
    private int currentToken;
    private int row;
    int a;

    // Hjälpmetod som läser in innehållet i en inputstream till en
    // sträng
    private static String readInput(InputStream f) throws java.io.IOException {
        Reader stdin = new InputStreamReader(f);
        StringBuilder buf = new StringBuilder();
        char input[] = new char[1024];
        int read = 0;
        while ((read = stdin.read(input)) != -1) {
            buf.append(input, 0, read);
        }
        return buf.toString();
    }


    public Lexer(InputStream in) throws java.io.IOException {
        String input = Lexer.readInput(in).toUpperCase();//Ta in input, gör om till små bokstäver
        // Ett regex som beskriver hur ett token kan se ut. Plus whitespace, new line och kommentarer (som vi här vill ignorera helt)
        input = input.replaceAll("%.*\n"," \n ");
        //System.out.println(input);
        //System.out.println("3");
        input = input.replaceAll("\n"," \n "); //Lägger till mellanslag innan och efter varje radbyte
        if (input.trim().length() == 0) {   //Ifall indata är "tom"
            System.exit(0);
        }
        String[] inputArray = input.split("\n");
        Pattern tokenPattern = Pattern.compile("FORW\\s|BACK\\s|LEFT\\s|RIGHT\\s|DOWN|UP|COLOR\\s|REP\\s|\\.|\"|[1-9][0-9]*\\s+|[1-9][0-9]*|#[A-Z0-9]{6}|\\s+");
        //System.out.println(tokenPattern);
        tokens = new ArrayList<Token>();
        currentToken = 0;
        for (int i = 0; i < inputArray.length ; i++ ) {
            Matcher m = tokenPattern.matcher(inputArray[i]);
            int inputPos = 0;
            row = i + 1;
            // Hitta förekomster av tokens/whitespace i indata
            while (m.find()) {

                // Om matchningen inte börjar där den borde har vi hoppat
                // över något skräp i indata, markera detta som ett
                if (m.start() != inputPos) {
                    tokens.add(new Token(TokenType.error, row));
                }
                // Kolla vad det var som matchade
                if (m.group().matches("FORW\\s")){
                    tokens.add(new Token(TokenType.forw, row));
                }
                else if (m.group().matches("BACK\\s"))   {
                    tokens.add(new Token(TokenType.back, row));
                }
                else if (m.group().matches("LEFT\\s")) {
                    tokens.add(new Token(TokenType.left, row));
                }
                else if (m.group().matches("RIGHT\\s")) {
                    tokens.add(new Token(TokenType.right, row));
                }
                else if (m.group().matches("DOWN")){
                    tokens.add(new Token(TokenType.down, row));
                }
                else if (m.group().matches("UP"))  {
                    tokens.add(new Token(TokenType.up, row));
                }
                else if (m.group().matches("COLOR\\s"))  {
                    tokens.add(new Token(TokenType.color, row));
                }
                else if (m.group().contains("REP")){
                    tokens.add(new Token(TokenType.rep, row));
                }
                else if (m.group().matches("\\."))       {
                    tokens.add(new Token(TokenType.period, row));
                }
                else if (m.group().matches("\"")) {
                    tokens.add(new Token(TokenType.quote, row));
                }
                else if (Character.isDigit(m.group().charAt(0))) {
                    tokens.add(new Token(TokenType.number, m.group(), row));
                }
                else if (m.group().contains("#")) { //Innehåller # men inte är en kommentar
                    tokens.add(new Token(TokenType.hex, m.group(), row));
                }
                inputPos = m.end();
            }

// Kolla om det fanns något kvar av indata som inte var ett token
            if (inputPos != inputArray[i].length()) {
                tokens.add(new Token(TokenType.error, row));
            }
        }

    }

    // Kika på nästa token i indata, utan att gå vidare
    public Token peekToken() throws SyntaxError {
        // Slut på indataströmmen
        if (!hasMoreTokens())
            throw new SyntaxError(tokens.get(currentToken-1).getRow()); //Skickar med raden för aktuell token -1.
        return tokens.get(currentToken);
    }

    // Hämta nästa token i indata och gå framåt i indata
    public Token nextToken() throws SyntaxError {
        Token res = peekToken();
        ++currentToken;
        return res;
    }

    public Token thisToken() {
        return tokens.get(currentToken-1);
    }

    public boolean hasMoreTokens() {
        return currentToken < tokens.size();
    }
}

