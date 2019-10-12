//Daniel Landberg & Fredrik Diffner
// Kattis id: 4201969

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class BusstopJava {

    public static void main(String args[]){

        Scanner scanner = new Scanner(System.in);
        int busses = scanner.nextInt();
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        while (scanner.hasNextInt()) {
            inputs.add(scanner.nextInt());
        }
        System.out.println(buss(inputs, busses));
    }

    public static String buss(ArrayList<Integer> listan, int nrbusses) {
        StringBuilder builder = new StringBuilder();
        Collections.sort(listan);
        int counter = 0;
        int savenumber = 0;
        for (int i = 1; i < nrbusses; i++) {
            int b = listan.get(i);
            int c = listan.get(i - 1);
            //tittar om det finns en buss som är 1 nummer över, isf spara första bussen
            if (c == b - 1 && counter == 0 && nrbusses-1 != i) {
                savenumber = c;
                counter++;
            }
            //tittar samma som ovan fast om counter är större än 1, dvs vi har redan en buss som kommer efter varandra (102, 103)
            else if (c == b - 1 && counter > 0 && nrbusses-1 != i) {
                counter++;
                if (counter == 2) {
                    builder.append(savenumber + "-");
                }
                //avslutar en hoppsättning med specialfallet sista bussen i listan i den första näslade if satsen
            } else {
                if (i == nrbusses - 1) {
                    if(counter >=2 && c == b - 1){
                        builder.append(b);
                    }
                    else if (counter == 1 && c == b - 1) {
                        builder.append(savenumber + "-" + b);
                    } else if (counter == 1) {
                        builder.append(savenumber + " " + c + " " + b);
                    } else {
                        builder.append(c + " " + b);
                    }
                }
                //vanliga bussar, dvs antingen 2 st i rad (102, 103) eller endast en som inte har någon bredvid sig
                else if (counter == 1) {
                    builder.append(savenumber + " " + c + " ");
                    counter =0;
                } else {
                    builder.append(c + " ");
                    counter = 0;
                }
            }
        }
        return builder.toString();
    }
}