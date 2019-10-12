// Klass för att representera syntaxfel.  I praktiken vill man nog
// även ha med ett litet felmeddelande om *vad* som var fel, samt på
// vilken rad/position felet uppstod
// Författare: Per Austrin
public class SyntaxError extends Exception {
    public SyntaxError (int row){
        System.out.println("Syntaxfel på rad " + row);
        System.exit(0);
    }
}
