package Client;

import java.util.List;

/*
Small class to allow easy sending of gamestate to GUI.
 */
public class Gamestate {
    private List<Integer> players;
    private int playerWithBall;

    public Gamestate(List<Integer> players, int playerWithBall) {
        this.players = players;
        this.playerWithBall = playerWithBall;
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public int getPlayerWithBall() {
        return playerWithBall;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Integer playerID : players) {
            builder.append(playerID);
            builder.append(",");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));

        builder.append(" ");
        builder.append(playerWithBall);
        return builder.toString();
    }
}