import java.net.*;
import java.io.*;
import java.util.HashMap;

public class ServerThread extends Thread {
    private Socket socket;
    private int player;
    private HashMap<String, ServerThread> clientInfo;
    private PrintWriter out;    //För att skicka ut till socketen
    private BufferedReader in;  //För at läsa det som kommer in från socketen
    private Game game;

    /**
     * En server-tråd för varje ansluten spelare
     *
     * @param socket: En socket vars "remote address and remote port set to that of the client"
     * @param player: spelarens nummer
     */
    public ServerThread(Socket socket, int player, Game game) {
        super("ServerThread");
        this.socket = socket;
        this.player = player;
        this.game = game;
        clientInfo = Server.getClientInfo(); //Information om alla spelare
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //En ny tråd skapas och startas
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            String inputLine;
            String outputLine;
            //Lägger in information om spelaren i clientinfo
            clientInfo.put("player" + player, this);
            //Skriver ut att spelaren anslutit till alla spelare
            sendToAllClients("player" + player + " " + "Connected");
            //lägger till spelaren
            game.newPlayer(player);

            //Ifall tillräckligt många spelare, kör igång spelet och skickar ut gameInfo
            if (player == 2) {
                //startar spelet
                //Skickar kartan
                sendToAllClients("SendingMap");
                String []map = game.getStringMap();   //Första elementet är storleken, andra själva spelplanen
                sendToAllClients(map[0]);
                sendToAllClients(map[1]);
                game.startGame();
                sendToAllClients("startGame");
                String gameInfo = game.getGameInfo();
                sendToAllClients(gameInfo);
            }
            while (true) {
                //När clienterna avslutar skickar de "escape" till socketen. Avsluta då threaden
                inputLine = in.readLine();
                if (inputLine.equals("escape")) {
                    break;
                }
                outputLine = game.move(inputLine, player);
                //Om arrayen som skickas tillbaka från game bara har längden 1, så har vi en vinnare
                if (outputLine.length() == 1) {
                    sendToAllClients(outputLine);
                }
                //Annars, skicka ut gameinfo till alla spelare
                else {
                    sendToAllClients(outputLine);
                }
            }

            //Stänger printwritern, bufferedreadern och socketen
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Funktion som tar ett meddelande och skickar till alla clienter
     *
     * @param message
     */
    private void sendToAllClients(String message) {
        for (ServerThread client : clientInfo.values()) {
            client.out.println(message);
        }
    }
}