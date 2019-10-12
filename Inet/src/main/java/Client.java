import java.io.*;
import java.net.*;

/**
 * Klass för klienten.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        //Första elementet i input är datornamnet där servern finns. Andra är portnumret servern lyssnar på.
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        startSocket(hostName, portNumber);
    }

    /**
     * Startar socketen
     * Anropar sedan setUpGame
     * @param hostName  datornamn/IP servern finns på
     * @param portNumber    Den port servern lyssnar på
     */
    private static void startSocket(String hostName, int portNumber) {
        //Socketen, PrintWritern och Bufferedreadern stängs automatiskt om något i try-satsen misslyckas eller när funktionen avslutas
        try (
                //Skapar en socket kopplad till datornamnet servern finns på samt porten den lyssnar på
                Socket socket = new Socket(hostName , portNumber);
                //Printwriter som skickar till socketen
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //En bufferedreader som läser från socketen
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            setUpGame(out, in);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    /**
     * Sätter upp spelet. Tar emot utskrifter om nya spelare, kartan
     * Om servern skickar att spelet börjar anropas runGame
     * @param out PrintWriter som skickar till servern
     * @param in  BufferedReader som läser från servern
     * @throws IOException
     */
    private static void setUpGame(PrintWriter out, BufferedReader in) throws IOException {
        String inputString;
        //Startar ett grafik-objekt
        Lanterna grafik = new Lanterna();
        //While-loop som väntar på information om att nya (inklusive denna client) ansluter, kartan, eller att spelet börjar
        while (true) {
            inputString = in.readLine();
            //Om kartan skickas, skicka denna till grafiken
            if (inputString.equals("SendingMap")) {
                String mapSize = in.readLine();
                String map = in.readLine();
                grafik.StartMap(mapSize, map);

                //Om spelet startar, anropa runGame
                //Avslutar funktionen när runGame, dvs spelet, är klart
            } else if (inputString.equals("startGame")) {
                System.out.println(inputString);
                String gameInfo = in.readLine();
                grafik.upDateScreen(gameInfo);
                runGame(out, in, grafik);
                return;
            }
            //Annars printas att en till spelare anropas
            else System.out.println(inputString);
        }
    }

    /**
     * Skickar input från grafiken till servern, och skickar tillbaka returen från servern till grafiken
     * Avslutas när inputen från servern bara innehåller ett element, dvs när vi har en vinnare och spelet avslutas
     * @param out   PrintWriter som skickar till servern
     * @param in    BufferedReader som läser det som returneras från servern
     * @param grafik    Grafik-objektet
     * @throws IOException
     */
    private static void runGame(PrintWriter out, BufferedReader in, Lanterna grafik) throws IOException {
        //while-loop som är igång sålänge spelet pågår
        String keyStroke, inputString;
        while (true) {
            keyStroke = grafik.checkInput();
            //Om spelaren skickar in en fråga om förflyttning
            if (keyStroke != null) {
                out.println(keyStroke); //Skickar förflyttningen till game
                inputString = in.readLine();
                //Om vinst är returnerade listan bara av längden 1
                if (inputString.length() == 1) {
                    grafik.upDateScreen(inputString);
                    out.println("escape");
                    break;
                } else {
                    //Annars skickas gameInfo till grafiken
                    grafik.upDateScreen(inputString);
                }
            }
            //Annars, om det kommer något från socketen
            else if (in.ready()) {
                inputString = in.readLine();
                //Om vinst är returnerade listan bara av längden 1
                if (inputString.length() == 1) {
                    grafik.upDateScreen(inputString);
                    out.println("escape");
                    break;
                } else {
                    //Annars skickas gameInfo till grafiken
                    grafik.upDateScreen(inputString);
                }
            }
        }
    }
}