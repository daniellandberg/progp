import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Game {
    //TODO: utöka spelet för flera spelare
    private int mapSizeCol;
    private int mapSizeRow;
    private String stringMap;
    private HashMap<Integer, String[]> map;
    private HashMap<Integer, int[]> players;    //index 0: kolumn-position, index 1: rad-position, index 2: har vapen eller ej (0 = har vapen, 1 = har ej vapen)
    private ArrayList<int[]> weapons;
    private boolean playing;
    private Random random;

    /**
     * Sätter upp spelet.
     * Hämtar kartan från sparad fil, gör om den till en enda lång sträng och skickar den till clienterna
     * Gör om kartan till en hashmap för att enkelt kontrollera senare i check-move
     */
    public Game() {
        System.out.println("startar game");
        random = new Random();
        players = new HashMap<>();
        playing = false;
        mapSizeCol = 60;
        mapSizeRow = 30;
        stringMap = "";
        weapons = new ArrayList<>();
        //Hämtar in kartan från sparad fil och sparar den till en lång sträng
        try {
            stringMap = new String(Files.readAllBytes(Paths.get("src/main/java/Map.txt")));
        } catch (Exception e) {
            System.out.println("Something went wrong when reading the map");
            System.exit(1);
        }
        //Tar bort nya rader
        stringMap = stringMap.replace("\n", "").replace("\r", "");
        //Initierar spelplanen
        map = new HashMap<>();
        for (int col = 0; col < mapSizeCol; col++) {
            map.put(col, new String[mapSizeRow]);
        }
        //Fyller hashmapen för kartan med symboler
        String[] tempMap = stringMap.split("");
        int counter = 0;
        for (int row = 0; row < mapSizeRow; row++) {
            for (int col = 0; col < mapSizeCol; col++) {
                String[] column = map.get(col);
                column[row] = tempMap[counter];
                map.put(col, column);
                counter++;
            }
        }
    }

    /**
     * returnerar en array med storleken på spelplanen på formen "col,row" samt spelplanen som en enda lång sträng
     *
     * @return
     */
    public String[] getStringMap() {
        return new String[]{mapSizeCol + "," + mapSizeRow, stringMap};
    }

    /**
     * Funktion som tar emot en ny spelare.
     * Randomiserar start-position, kollar så att det är en funktion som går att stå på och inte upptagen, och lägger in spelaren och position i players
     *
     * @param player
     */
    public void newPlayer(int player) {
        while (true) {
            int[] temp = new int[]{random.nextInt(mapSizeCol), random.nextInt(mapSizeRow)};
            boolean haswall = hasPositionWall(temp);
            boolean hasplayer = hasPositionPlayer(temp) != -1;
            if (!haswall && !hasplayer) {
                int[] playerInfo = new int[]{temp[0], temp[1], 0}; //0 = har ej vapen
                players.put(player, playerInfo);
                break;
            }
        }
    }

    /**
     * För antalet spelare randomiseras positionen för vapen. Om positionen inte är en vägg eller finns registrerad som en spelar- eller vapen-position läggs positionen till listan för vapen-positioner.
     */
    private void generatePositionWeapons() {
        for (int i = 0; i < players.size(); i++) {
            while (true) {
                int[] temp = new int[]{random.nextInt(mapSizeCol), random.nextInt(mapSizeRow)};
                boolean wall = hasPositionWall(temp);
                boolean player = hasPositionPlayer(temp) != -1;
                boolean weapon = hasPositionWeapon(temp);
                if (!wall && !player && !weapon) {
                    weapons.add(temp);
                    break;
                }
            }
        }
    }

    /**
     * Startar spelet och genererar vapenpositioner
     */
    public void startGame() {
        playing = true;
        generatePositionWeapons();
    }

    /**
     * Tar emot en förfrågan om förflyttning från klienterna.
     * Gör om den skickade förfrågan till koordinater beroende på spelarens nuvarande position
     * Returnerar sedan eventuellt uppdaterad gameInfo från getGameInfo
     * Om clienten mot förmodan har skickat något felaktigt returneras bara nuvarande gameInfo
     *
     * @param direction En sträng bestående av "left"|"right"|"up"|"down"
     * @param player    Vilken spelare som skickar förfrågan
     * @return En array med information om spelarna(plats samt om de har vapen eller ej) samt eventuella vapens positioner.
     */
    public String move(String direction, int player) {
        int[] newPose = new int[2];
        if (direction.equals("left")) {
            //kolumn
            newPose[0] = players.get(player)[0] - 1;
            //rad
            newPose[1] = players.get(player)[1];
            return checkMove(newPose, player);
        } else if (direction.equals("right")) {
            newPose[0] = players.get(player)[0] + 1;
            newPose[1] = players.get(player)[1];
            return checkMove(newPose, player);
        } else if (direction.equals("up")) {
            newPose[0] = players.get(player)[0];
            newPose[1] = players.get(player)[1] - 1;
            return checkMove(newPose, player);
        } else if (direction.equals("down")) {
            newPose[0] = players.get(player)[0];
            newPose[1] = players.get(player)[1] + 1;
            return checkMove(newPose, player);
        }
        //ifall något random av ngn anledning skickas in
        else return getGameInfo();
    }


    /**
     * Tar en ny koordinat för en spelare, och beroende på spelets nuvarande tillstånd returneras om det är ett giltigt drag eller ej
     *
     * @param newPos på formen [col, row]
     * @param player vilken spelare draget gäller
     * @return String[]. Om det finns en vinnare innehåller arreyen bara denne spelares index.
     * Annars är första index information om spelare (position samt om de har vapen), andra index är vapnens position
     */
    private String checkMove(int[] newPos, int player) {
        int otherPlayer = hasPositionPlayer(newPos); //Om en spelare finns på den nya positionen returnerar denne spelare. Annars -1
        //ifall går in i en spelare
        if (otherPlayer != -1) {
            //Om den andra spelaren inte har ett vapen
            if (players.get(otherPlayer)[2] == 0) {
                //Om ingen av spelarna har ett vapen, gör ingenting
                // Om den förflyttande spelaren har ett vapen, är den en vinnande
                if (players.get(player)[2] == 1) {
                    playing = false;
                    return Integer.toString(player);
                }
            }
            //Om den andra spelaren har ett vapen
            else if (players.get(otherPlayer)[2] == 1) {
                //Om båda spelarna har vapen, DUELL!
                //Generera nya vapen och sätt spelarnas vapen-info till 0
                if (players.get(player)[2] == 1) {
                    generatePositionWeapons();
                    players.get(player)[2] = 0;
                    players.get(otherPlayer)[2] = 0;
                }
                //Annars, om den andra spelaren har vapen men ej den förflyttade, skicka tillbaka den andra spelaren som vinnare
                else {
                    playing = false;
                    return Integer.toString(otherPlayer);
                }
            }
        }
        //Ifall går in i en vägg, returnera bara gamla positionen
        else if (hasPositionWall(newPos)) {
            return getGameInfo();
        }
        //Om spelaren går in i ett vapen
        else if (hasPositionWeapon(newPos)) {
            //Om spelaren inte har ett vapen. plocka upp vapnet
            if (players.get(player)[2] == 0) {
                //Tar bort vapnet från vapen-positions-listan
                for (int weaponIndex = 0; weaponIndex < weapons.size(); weaponIndex++) {
                    if(Arrays.equals(weapons.get(weaponIndex), newPos)) weapons.remove(weaponIndex);
                }
                for (int[] weaponPos : weapons) {
                    if (Arrays.equals(weaponPos, newPos)) weapons.remove(weaponPos);
                }
                //Uppdaterar positionen på spelaren
                int[] newPlayerInfo = new int[]{newPos[0], newPos[1], 1};
                players.put(player, newPlayerInfo);
            }
            //Annars, förflytta bara spelaren
            else {
                int[] newPlayerInfo = new int[]{newPos[0], newPos[1], 1};
                players.put(player, newPlayerInfo);
            }
        }
        //Annars, förslytta bara spelaren
        else {
            players.get(player)[0] = newPos[0];
            players.get(player)[1] = newPos[1];
        }
        return getGameInfo();
    }

    /**
     * Ifall given position innehåller en spelare returneras denne spelare, annars -1
     *
     * @param position på formen [col, row]
     * @return true om en spelare finns på positionen, false annars
     */
    private int hasPositionPlayer(int[] position) {
        int[] temp1 = new int[]{position[0], position[1], 0};
        int[] temp2 = new int[]{position[0], position[1], 1};
        for (int player = 1; player < players.size() + 1; player++) {
            if (Arrays.equals(players.get(player), temp1) || Arrays.equals(players.get(player), temp2)) return player;
        }
        return -1;
    }

    /**
     * Kontrollerar om given position är en vägg
     *
     * @param position på formen [col, row]
     * @return true om given position är en vägg
     */
    private boolean hasPositionWall(int[] position) {
        return map.get(position[0])[position[1]].equals("1");
    }

    /**
     * Kontrollerar om given position har ett vapen
     *
     * @param position på formen [col, row]
     * @return true om given position har ett vapen
     */
    private boolean hasPositionWeapon(int[] position) {
        for (int[] weaponPos : weapons) {
            if (Arrays.equals(weaponPos, position)) return true;
        }
        return false;
    }

    /**
     * Returnerar positionen på alla spelare samt om spelaren har ett vapen eller ej, samt eventuella wapens position.
     *
     * @return: En array där första elementet är information om spelare på formen "col,row,0" där 0 = har ej vapen, 1 om spelaren har vapen
     * Andra elementet är en sträng med positioner för vapen på formen "col,row".
     */
    public String getGameInfo() {
        //Lägger först in info om spelare
        StringBuilder gameInfo = new StringBuilder();
        for (int[] playerInfo : players.values()) {
            gameInfo.append(playerInfo[0]);
            gameInfo.append(",");
            gameInfo.append(playerInfo[1]);
            gameInfo.append(",");
            gameInfo.append(playerInfo[2]);
            gameInfo.append(",");
        }
        //Lägger sedan till positioner för vapen om listan med vapenpositioner innehåller element.
        if (weapons.size() > 0) {
            for (int[] weapon : weapons) {
                gameInfo.append(weapon[0]);
                gameInfo.append(",");
                gameInfo.append(weapon[1]);
                gameInfo.append(",");
            }
        }
        return gameInfo.toString();
    }
}
