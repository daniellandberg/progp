// Författare: Per Austrin

// Ett syntaxträd
abstract class ParseTree {
    abstract public String process(Leona leona);
}

// Ett syntaxträd som representerar en "CommandNod(... , ...)"
class CommandNode extends ParseTree {
    ParseTree left, right;
    public CommandNode(ParseTree left, ParseTree right) {
        this.left = left;
        this.right = right;
    }
    public String process(Leona leona) {
        return left.process(leona) + right.process(leona);
    }
}

// Ett syntaxträd som representerar en "MoveNode(... , ....)"
class MoveNode extends ParseTree {
    TokenType move;
    int number;

    public MoveNode(TokenType move, int number) {
        this.move = move;
        this.number = number;
    }

    public String process(Leona leona) {
        if (move == TokenType.forw || move == TokenType.back) {
            return leona.ForwBack(move, number);
        } else {
            leona.LeftRight(move, number);
            return "";
        }

    }
}

// Ett syntaxträd som representerar en "UpDownNode(...)"
class UpDownNode extends ParseTree {
    TokenType upDown;

    public UpDownNode(TokenType UpDown) {
        this.upDown = UpDown;
    }

    public String process(Leona leona) {
        leona.UpDown(upDown);
        return "";
    }
}

// Ett syntaxträd som representerar en "ColorNode(...)"
class ColorNode extends ParseTree {
    String hex;

    public ColorNode(String hex) {
        this.hex = hex;
    }

    public String process(Leona leona) {
        leona.Color(hex);
        return "";
    }
}

// Ett syntaxträd som representerar en "RepNode(...)"
class RepNode extends ParseTree {
    int reps;
    ParseTree parsetree;

    public RepNode(int reps, ParseTree parsetree) {
        this.reps = reps;
        this.parsetree = parsetree;
    }

    public String process(Leona leona) {
        String returnString = "";
        for (int i = 0; i < reps; i++) {
            returnString = returnString + parsetree.process(leona);
        }
        return returnString;
    }
}



