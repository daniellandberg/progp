/* Författare: Fredrik Diffner & Daniel Landberg
 * Koden är skriven utifrån det skelett som givits för Lab S3
 */

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;

public class DFA {
    //Lista med accepterade tillstånd
    ArrayList<Integer> acceptedStates = new ArrayList<>();
    //En array med ett index för varje tillstånd. Varje index innehåller en hashmap.
    //Keys i hashmaparna är vilka övergångar som leder till det aktuella tillståndet (indexet i arrayen), value är vilket/vilka värden övergången har
    HashMap<Integer, ArrayList<String>>[] transitions;
    private int startState;
    private int count;

    public DFA(int stateCount, int start) {
        startState = start;
        transitions = new HashMap[stateCount];
        //Fyller arrayen för alla tillstånd med hashmaps
        for (int i = 0; i < stateCount; i++) {
            HashMap<Integer, ArrayList<String>> map = new HashMap<>();
            transitions[i] = map;
        }
    }

    //Funktion som adderar varje accepterat tillstånd till en lista
    public void setAccepting(int state) {
        acceptedStates.add(state);
    }

    //Lägger till övergångar i hashmapen för respektive till-tillstånd
    public void addTransition(int from, int to, char c) {
        //om det inte finns någon övergång registrerad från "from" till "to", skapa en ny arraylist med inkommen char (omgjord till String) och lägg in den i hashmapen.
        if (!transitions[to].containsKey(from)) {
            ArrayList<String> valueList = new ArrayList<>(Arrays.asList(Character.toString(c)));
            transitions[to].put(from, valueList);
        }
        //Annars, om det redan finns en lista med registrerade values för denna övergång
        else {
            //Hämta ut listan med värden på den aktuella övergången, lägg till det nya värdet, och lägg tillbaka listan i hashmapen igen
            ArrayList<String> valueList = transitions[to].get(from);
            valueList.add(Character.toString(c));
            transitions[to].put(from, valueList);
        }
    }

    //Funktion som hämtar ut exempel på alla accepterade strängar
    //Använder sig av hjälpfunktionen searchTransition
    //Börjar i de accepterade tillstånden, gör sedan en bred sökning "bakåt", ett steg i taget för varje tillstånd.
    //Har en länkad lista där vartannat element är ett nästa tillstånd, vartannat element är 2D listor. Varje lista innehåller de värden som varje övergång till detta tillstånd håller.
    public ArrayList<String> getAcceptingStrings(int maxCount) {
        LinkedList<Object> currentStates = new LinkedList<>();
        //Börjar med att lägga in de accepterade tillstånden i den länkade listan. Efter varje tillstånd adderas en tom lista, eftersom vi är i sista tillståndet
        for (int state : acceptedStates) {
            currentStates.addLast(state);
            currentStates.addLast(new ArrayList<ArrayList<String>>(0));
        }
        ArrayList<String> ans = searchTransition(maxCount, currentStates);
        return ans;
    }

    //Hjälpfunktion till getAcceptedStrings
    //Går igenom den länkade listan från början till slut.
    //Tar ett tillstånd och den 2D lista som innehåller värden på alla de övergångar som tagits för att ta sig till tillståndet.
    //Kontrollerar alla övergångar som leder till det aktuella tillståndet,
    //och adderar de med dess värden på övergången (adderat till listan med värden från tidigare övergångar) till den länkade listan.

    //Om ett en övergång går från ett starttillstånd, dvs om vi har funnit en accepterad väg, används hjälpfunktionen permutions för att permutera ihop
    // tillräckligt många accepterade strängar utifrån de listor med värden från de tagna övergångarna.

    private ArrayList<String> searchTransition(int maxCount, LinkedList currentStates) {
        count = 1;                  //Räknare som håller koll på hur många accepterade strängar vi har hittat
        int maxLength = 5000;       //Maxlängden på de strängar som returneras
        ArrayList<String> ans = new ArrayList<>(maxCount);
        //Tydligen ska tomma strängen vara med som ett svar enligt kattis...
        if (acceptedStates.contains(startState)) {
            ans.add("");
            count++;
        }
        while (currentStates.size() > 0) {        //Kör sålänge det finns några element kvar i den länkade listan
            Integer state = (Integer) currentStates.removeFirst();        //nuvarande tillstånd
            ArrayList<ArrayList<String>> earlierValues = (ArrayList<ArrayList<String>>) currentStates.removeFirst();          //Listor med värden från alla tagna övergångar
            //Hämtar ut hashmapen med alla tillstånd och  dess värden som leder till det nuvarande tillståndet. Itererar sedan genom övergångarna
            HashMap<Integer, ArrayList<String>> hashmap = transitions[state];
            if (!hashmap.isEmpty()) {
                for (Map.Entry<Integer, ArrayList<String>> next : hashmap.entrySet()) {
                    int nextState = next.getKey();      //Tillstånd som leder till nuvarande tillstånd
                    ArrayList<String> values = next.getValue(); //Övergångens alla värden
                    ArrayList<ArrayList<String>> newList = new ArrayList(earlierValues);
                    newList.add(values);                //Addera värden på övergången till listan med värden från tidigare övergångar
                    //Om övergången är från ett starttillstånd har vi hittat en accepterad väg. Tar fram permutationer av listorna med värden från övergångar
                    if (nextState == startState && newList.size() < maxLength) {
                        ans = (permutations(newList, ans, 0, "", maxCount));
                        if (count > maxCount) return ans;   //Om vi har tillräckligt med svar, returnera
                    }
                    //Lägg till tillståndet som leder till nuvarande tillståndet, samt värdet på övergången, sist i den länkade listan
                    if (earlierValues.size() < maxLength) {
                        currentStates.addLast(nextState);
                        currentStates.addLast(newList);
                    }

                }
            }
        }
        return ans;
    }

    //Rekursiv hjälpfunktion till searchTransactions. Permuterar flera listor med värden från övergångar
    private ArrayList<String> permutations(ArrayList<ArrayList<String>> Lists, ArrayList<String> result, int depth, String current, int maxCount) {
        //Om vi nått tillräckligt djupt, addera nuvarande sträng till resultatet och returnera
        if (depth == Lists.size()) {
            result.add(current);
            count++;
            return result;
        }

        //För första positionen i första listan, addera alla första index
        //Addera sedan första indexet i första listan med andra indexet i alla andra listor osv
        //Avbryt när vi har tillräckligt många resultat
        for (int i = 0; i < Lists.get(depth).size(); ++i) {
            permutations(Lists, result, depth + 1, (Lists.get(depth).get(i)) + current, maxCount);
            if (count > maxCount) return result;
        }
        return result;
    }
}
