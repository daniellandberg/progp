/**
 * En rekursiv medåknings-parser för binära träd
 *
 * Författare: Per Austrin
 */
import java.lang.*;

public class Parser {
    private Lexer lexer;

    public Parser(Lexer lexer) throws SyntaxError{
        this.lexer = lexer;
    }

    public ParseTree parse() throws SyntaxError {
        // Startsymbol är Commando
        ParseTree result = Commands();
        // Borde inte finnas något kvar av indata när vi parsat
        if (lexer.hasMoreTokens())
            throw new SyntaxError(lexer.peekToken().getRow());
        return result;
    }

    private ParseTree Commands() throws SyntaxError {
        ParseTree result = Command();
        if (lexer.hasMoreTokens()) return new CommandNode(result, Commands());
        return result;
    }

    private ParseTree Command() throws SyntaxError {
        TokenType tType = lexer.peekToken().getType();
        //ParseTree parsetree = new MoveNode(tType, 0);   //Bara dummy, kommer aldrig att returneras
        if (tType == TokenType.forw || tType == TokenType.back || tType == TokenType.left || tType == TokenType.right) {
            return Move();
        } else if (tType == TokenType.down || tType == TokenType.up || tType == TokenType.color) {
            return State();
        } else if (tType == TokenType.rep) {
            lexer.nextToken();
            //Om nästa token inte är ett nummer, kasta SyntaxError
            Token rep= lexer.nextToken();
            if (rep.getType() != TokenType.number) throw new SyntaxError(rep.getRow());
            //Kontrollera så det är ett mellanrum efter
            String reps = rep.getData().toString();
            if (!reps.matches(".*\\s+")) throw new SyntaxError(rep.getRow());
            int numReps = Integer.parseInt(reps.replaceAll("[^0-9]+","")); //Tar bort alla icke-siffror och gör om till int
            Token next = lexer.peekToken();
            if (next.getType() == TokenType.quote) {
                lexer.nextToken(); //Hoppa över quote
                ParseTree result = CommandRep();
                if (lexer.hasMoreTokens()) {
                    if (lexer.peekToken().getType() != TokenType.quote) throw new SyntaxError(lexer.nextToken().getRow());
                    lexer.nextToken(); //Hoppa över quote
                    return new RepNode(numReps, result);
                }
            }
            else {
                ParseTree result = Command();
                return new RepNode(numReps, result);
            }
        }
        if (lexer.hasMoreTokens()) throw new SyntaxError(lexer.nextToken().getRow());
        else {
            throw new SyntaxError(lexer.thisToken().getRow());
        }

    }

    private ParseTree CommandRep() throws SyntaxError {
        ParseTree result = Command();
        if (lexer.hasMoreTokens()) {
            if (lexer.peekToken().getType() == TokenType.quote) {
                return result;
            }
            else {
                return new CommandNode(result, CommandRep());
            }
        }
        return result;
    }


    private ParseTree Move() throws SyntaxError {
        TokenType move = lexer.nextToken().getType();
        //Om nästa token inte är ett nummer, kasta SyntaxError
        Token number = lexer.nextToken();
        if (number.getType() != TokenType.number) throw new SyntaxError(number.getRow());
        //Om nästa inte är en period-token, kasta SyntaxError
        Token next = lexer.nextToken();
        if (next.getType() != TokenType.period) throw new SyntaxError(next.getRow());
        int number2 = Integer.parseInt(number.getData().toString().replaceAll("\\D+",""));
        return new MoveNode(move, number2);
    }

    private ParseTree State() throws SyntaxError {
        TokenType state = lexer.nextToken().getType();
        //Ifall det är ett Up/Down-kommando
        if (state == TokenType.up || state == TokenType.down){
            //Ifall nästa token inte är en period, kasta SyntaxError
            Token next = lexer.nextToken();
            if (next.getType() != TokenType.period) throw new SyntaxError(next.getRow());
            return new UpDownNode(state);
        }
        else { //Ifall det är en color-token, returnera en ColorNode med hex.
            if (lexer.peekToken().getType() != TokenType.hex) throw new SyntaxError(lexer.peekToken().getRow());
            Object hex = lexer.nextToken().getData();
            //Ifall nästa token inte är en period, kasta SyntaxError
            Token next = lexer.nextToken();
            if (next.getType() != TokenType.period) throw new SyntaxError(next.getRow());
            return new ColorNode((String) hex);
        }
    }



}
