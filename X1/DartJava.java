//Daniel Landberg & Fredrik Diffner
// Kattis id: 4201974


import java.util.Scanner;

public class DartJava {

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        int score = scanner.nextInt();

        int[] a = highdart(score);
        int g = a[0];
        int g1 = a[1];
        int g2 = a[2];
        //basfall som inte täcks av printa funktionen
        if (g == 0 && g1 == 0 && g2 == 0) {
            System.out.println("impossible");
        } else {
            printa(g);
            printa(g1);
            printa(g2);
        }
    }
    //kollar om man ska printa som triple, double eller single eller impossible
    private static void printa(int p) {
        if(p==0){

        }
        else if(p % 3 ==0) {
            System.out.println("triple " + p/3);
        }else if(p % 2 ==0) {
            System.out.println("double " + p/2);
        }else if(p <=20){
            System.out.println("single " + p);
        }
        else{
            System.out.println("impossible");
        }
    }

    private static int[] highdart(int score) {
        int g = 0;
        int h = 0;
        int l = 0;
        //basfall som inte täcks av for looparna nedan
        if (score==1){
            return new int[]{1, 0, 0};
        }
        if (score==2){
            return new int[]{1, 1, 0};
        }
        //for loop som kollar alla utfall och tar ut svaret om den hittar en lösning

        int d=0;
        int[] f ={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,24,26,27,28,30,32,33,34,36,38,39,40,42,45,48,51,54,57,60};
        for (int i = 0; i < 41; i++) {
            g = f[i];
            for (int j = 0; j < 41; j++) {
                h = f[j];
                for (int k = 0; k < 41; k++) {
                    l = f[k];
                    d= l+h+g;
                    if(d==score){
                        return new int[]{g, l, h};

                    }
                }

            }
        }
        if(d != score){
            g=0;
            l=0;
            h=0;

        }

        return new int[]{g, l, h};
    }

}