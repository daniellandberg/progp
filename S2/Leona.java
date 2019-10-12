import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Leona {
    private Double x;
    private Double y;
    private String hex;
    private Boolean up;
    private Double v;

    public Leona(){
        x = 0.0;
        y = 0.0;
        hex = "#0000FF";
        up = true;
        v = 0.0;
    }

    public String ForwBack(TokenType move, int step) {
        double newX;
        double newY;
        String returnString = "";
        DecimalFormat df = new DecimalFormat("0.0000");
        df.setRoundingMode(RoundingMode.CEILING);
        if (move == TokenType.forw){
            newX = x + (step * (Math.cos(Math.PI*v/180)));
            newY = y + (step * (Math.sin(Math.PI*v/180)));
        }
        else {  //Eller om kommandot är back
            newX = x + (step * (Math.cos(Math.PI * (v+180) / 180)));
            newY = y + (step * (Math.sin(Math.PI * (v+180) / 180)));
        }

        if (!up){
            String xString = df.format(x);
            String yString = df.format(y);
            String newXString = df.format(newX);
            String newYString = df.format(newY);
            String[] pos = new String[]{xString, yString, newXString, newYString};
            for (int i = 0; i < pos.length; i++) {
                if (pos[i].matches("-0,0000")) {
                    pos[i] = "0.0000";
                }
                }
            String string = (hex + " " + pos[0] + " " + pos[1] + " " + pos[2] + " " + pos[3] + "\n");
            returnString = string.replaceAll(",",".");
        }
        this.x = newX;
        this.y = newY;
        return returnString;
    }

    public void LeftRight(TokenType move, int degree){
        if (move == TokenType.left) {
            this.v = (v + degree % 360);
        }
        if (move == TokenType.right){
            this.v = (v + 360 - degree % 360);
        }
    }

    public void UpDown (TokenType upDown) {
        if (upDown == TokenType.up){
            if (!up) up = true;
        }
        else {
            if (up) up = false;
        }
    }

    public void Color (String hex) {
        this.hex = hex;
    }
}


