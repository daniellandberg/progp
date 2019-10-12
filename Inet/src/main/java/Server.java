import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 * Klass för servern.
 * Startar en serversocket, och för varje client som ansluter skapas en serverthread samt att räknaren för antalet spelare plussas på
 */

public class Server {
    private int players;     //Antalet spelare
    private static HashMap<String, ServerThread> clientInfo;    //Hashmap med info om de anslutna clienterna. Key är spelarens numer t.ex. "player1", value är dennes ServerThread.


    public Server() throws IOException {
        players = 0;
        clientInfo = new HashMap<String, ServerThread>();
    }

    /**
     * Frågar efter port som servern ska lyssna till, och startar sedan en ServerSocket.
     * När en client anropar och accepteras, skapas en serverthread     *
     *
     * @throws IOException
     */
    public void startServer() throws IOException {
        System.out.println("Enter port for server to listen to");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String port = br.readLine();
        br.close();
        //Ifall inputen är fel, anropa startServer igen.
        if (port.length() != 1) {
            System.err.println("Wrong input, try again");
            startServer();
            return;
        }
        int portNumber = Integer.parseInt(port);
        //try (De objekt som skapas här kommer automatiskt att stängas (close) ifall vi stöter på problem eller när funktionen är klar)
        Game game = new Game();
        try (ServerSocket serverSocket = new ServerSocket(portNumber, 0, InetAddress.getLocalHost())) {
            System.out.println(serverSocket);
            while (true) {
                players++;
                System.out.println("väntar på client");
                //När någon klient ansluter (serverSocket.accept()) till servern skapar en ny server-tråd som kan köras paralellt. Som parameter skickas vilken i ordningen den anslutande spelaren är, dvs hens namn
                //.start() anropar functionen run() hos det nyskapade ServerThread-objektet.
                ServerThread serverThread = new ServerThread(serverSocket.accept(), players, game);
                System.out.println("client acceptad");
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    /**
     * returnerar Hashmapen med clientInfo
     *
     * @return
     */
    public static HashMap<String, ServerThread> getClientInfo() {
        return clientInfo;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }
}


