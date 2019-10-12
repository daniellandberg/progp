import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.googlecode.lanterna.input.KeyType.Escape;


/**
 * Klass för grafiken
 */
public class Lanterna {
    private Terminal terminal;
    private Screen screen;
    private TextGraphics tg;
    private ArrayList<Integer> oldPos;
    private int players; //Just nu kodat för 2 st. Kan utökas


    public Lanterna() throws IOException {
        //Skapar terminal och screen, startar en welcome-skärm
        terminal = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(60, 30)).createTerminal();
        screen = new TerminalScreen(terminal);
        tg = screen.newTextGraphics();
        screen.startScreen();
        tg.putString(screen.getTerminalSize().getColumns() / 2, screen.getTerminalSize().getRows() / 2 - 2, "Welcome!", SGR.BOLD);
        tg.putString(screen.getTerminalSize().getColumns() / 2, screen.getTerminalSize().getRows() / 2, "Waiting for comrades", SGR.BOLD);
        screen.refresh();
        oldPos = new ArrayList<>();
        players = 2;
    }

    /**
     * Funktion för att skriva ut kartan.
     *
     * @param mapSizeTemp Storleken på kartan som en sträng på formatet "col,row"
     * @param mapTemp     Tar in kartan som en lång sträng. 1:a symboliserar vägg, 0:a tomt utrymme
     * @throws IOException
     */
    public void StartMap(String mapSizeTemp, String mapTemp) throws IOException {
        try {
            int[] mapSize = Arrays.stream(mapSizeTemp.split(",")).mapToInt(Integer::parseInt).toArray();   //Gör om till en array med int
            String[] map = mapTemp.split("");       //Gör om till en Array med tecken för att enkelt kunna iterera igenom
            screen.clear();
            int counter = 0;
            for (int row = 0; row < mapSize[1]; row++) {
                for (int col = 0; col < mapSize[0]; col++) {
                    if (map[counter].equals("1")) {             //Om det på positionen finns en etta, rita ut en vägg, annars ingenting
                        tg.putString(col, row, String.valueOf(Symbols.BLOCK_SOLID));
                    }
                    counter++;
                }
            }
            screen.refresh();
        } catch (Exception e) {
            System.out.println("Something went wrong reading the map");
            System.exit(1);
        }
    }

    /**
     * Tar input från terminalen. Om input är piltangenterna returneras rätt sträng
     *
     * @return null om användaren inte tryckt på något eller tryckt på något förutom piltangenterna. Annars Strängar som representerar piltangenterna
     * @throws IOException
     */
    public String checkInput() throws IOException {
        KeyStroke keyPressed = terminal.pollInput(); //pollInput blockar ej
        if (keyPressed != null) {
            switch (keyPressed.getKeyType()) {
                case ArrowRight:
                    return "right";
                case ArrowLeft:
                    return "left";
                case ArrowUp:
                    return "up";
                case ArrowDown:
                    return "down";
            }
        }
        return null;
    }

    /**
     * Uppdaterar positionerna av spelare och eventuella vapen utifrån de positioner som skickas in
     *
     * @param gameInfoTemp positioner för spelare samt om de har vapen (1) eller ej (0). På den upprepande formen "col,row,0,col,row,1" (2 spelare där första är vapenlös men andra har vapen)
     *                     Om vapen finns utlagda på kartan ligger positionerna för de med sist i listan på formen "col,row"
     * @throws IOException
     */
    public void upDateScreen(String gameInfoTemp) throws IOException {
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        //Tar bort spelare och vapen för att lägga in de nya positionerna
        if (oldPos.size() > 0) {
            for (int i = 0; i < oldPos.size(); i = i + 2) {
                tg.putString(oldPos.get(i), oldPos.get(i + 1), " ");
            }
            oldPos.clear();
        }
        try {
            int[] gameInfo = Arrays.stream(gameInfoTemp.split(",")).mapToInt(Integer::parseInt).toArray(); //Gör om strängen till en int[]
            //Ifall längden bara är ett har vi en vinnare. Anropar funktionen winner() och avbryter sedan
            if (gameInfo.length == 1) {
                winner(gameInfo[0]);
                return;
            } else {
                //Går igenom listan med spelarInfo och uppdaterar kartan. De 6 första är information om spelaren, de andra är positioner för vapnen
                for (int i = 0; i < players * 3; i = i + 3) {
                    tg.setForegroundColor(TextColor.ANSI.DEFAULT);  //Behöver nollställa färgen så att båda spelarna inte görs röda om första har ett vapen
                    int col = gameInfo[i];
                    int row = gameInfo[i + 1];
                    int hasWeapon = gameInfo[i + 2];
                    //Printa vanlig spelare om denne inte har vapen. Annars en röd spelare
                    if (hasWeapon == 0) {
                        tg.putString(col, row, String.valueOf(Symbols.FACE_BLACK));
                    } else {
                        tg.setForegroundColor(TextColor.ANSI.RED);
                        tg.putString(col, row, String.valueOf(Symbols.FACE_BLACK), SGR.FRAKTUR);
                    }
                    //Addera positionerna till listan med gamla positioner
                    oldPos.add(col);
                    oldPos.add(row);
                }
                //Om det ska finnas vapen ute på planen, rita ut de.
                if (gameInfo.length > players * 3) {
                    for (int i = players * 3; i < gameInfo.length; i++) {
                        int col = gameInfo[i];
                        int row = gameInfo[i + 1];
                        tg.setForegroundColor(TextColor.ANSI.RED);
                        tg.putString(col, row, String.valueOf(Symbols.HEART));
                        oldPos.add(col);
                        oldPos.add(row);
                        i++;
                    }
                }
            }
            screen.refresh();
        } catch (Exception e) {
            System.out.println("Something went wrong updating the map");
            return;
        }
    }

    /**
     * Funktion för att skriva ut vinnare. Väntar med att avsluta tills användaren trycker på Escape
     *
     * @param winner Den vinnande spelaren
     * @throws IOException
     */
    private void winner(int winner) throws IOException {
        screen.clear();
        tg.setForegroundColor(TextColor.ANSI.CYAN);
        tg.putString(screen.getTerminalSize().getColumns() / 2, screen.getTerminalSize().getRows() / 2 - 2, "We have a winner!", SGR.BOLD);
        tg.putString(screen.getTerminalSize().getColumns() / 2, screen.getTerminalSize().getRows() / 2, "Congrats to player" + winner, SGR.BOLD, SGR.BLINK);
        tg.setForegroundColor(TextColor.ANSI.CYAN);
        tg.putString(screen.getTerminalSize().getColumns() / 2, screen.getTerminalSize().getRows() / 2 + 2, "Press ESC to exit", SGR.BOLD);
        screen.refresh();
        //Väntar tills användaren trycker på escape
        while (true) {
            KeyStroke keyPressed = terminal.readInput(); //blockar
            if (keyPressed.getKeyType() == Escape) break;
        }
        screen.stopScreen();
    }
}