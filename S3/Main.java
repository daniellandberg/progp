/* Runner för Labb S3 i DD1361 Programmeringsparadigm
 *
 * Författare: Fredrik Diffner & Daniel Landberg
 * Koden är skriven utifrån det skelett som givits för Lab S3
 */
import java.util.List;

public class Main {
	public static void main(String[] args) {
		Kattio IO = new Kattio(System.in, System.out);
		DFA automaton = new DFA(IO.getInt(), IO.getInt());		//Första int: antalet tillstånd. Andra int: starttillstånd
		int accept = IO.getInt();								//Accept: Antalet accepterade tillstånd
		for (int i = 0; i < accept; ++i)
			automaton.setAccepting(IO.getInt());				//Sätter accepterade tillstånd
		int transitions = IO.getInt();							//Antalet övergångar
		for (int i = 0; i < transitions; ++i) {
			int from = IO.getInt();
			int to = IO.getInt();
			String sym = IO.getWord();
			char lo = sym.charAt(0);
			char hi = sym.charAt(sym.length()-1);
			for (char x = lo; x <= hi; ++x)
				automaton.addTransition(from, to, x);
		}
		int bound = IO.getInt();
		List<String> strings = automaton.getAcceptingStrings(bound);
		IO.println(strings.size());
		for (String s: strings)
			IO.println(s);
		IO.close();
	}
}
