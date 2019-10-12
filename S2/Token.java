// Författare: Per Austrin

// De olika token-typer vi har i grammatiken
enum TokenType {
    forw, back, left, right, down, up, color, rep, period, quote, number, hex, error
}

// Klass för att representera en token
// I praktiken vill man nog även spara info om vilken rad/position i
// indata som varje token kommer ifrån, för att kunna ge bättre
// felmeddelanden
class Token {
    private TokenType type;
    private Object data;
    private int row;

    public Token(TokenType type, int row) {
        this.type = type;
        this.data = null;
        this.row = row;
    }

    public Token(TokenType type, Object data, int row) {
        this.type = type;
        this.data = data;
        this.row = row;
    }

    public TokenType getType() { return type; }
    public Object getData() { return data; }
    public int getRow() { return row; }

}
