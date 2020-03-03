package Client;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientListener implements Runnable {

    private Client client;
    private boolean autoRestart = true;

    public ClientListener(Client client) {
        this.client = client;
    }

    //constructor for testing -- allows disable of auto-restart
    public ClientListener(Client client, boolean autoRestart) {
        this.client = client;
        this.autoRestart = autoRestart;
    }

    @Override
    public void run() {
        Scanner reader = client.getReader();

        while (true) {
            try {
                String line = reader.nextLine();
                String[] substrings = line.split(" ");
                String command = substrings[0];
                String firstParameter = substrings[1];

                switch (command.toUpperCase()) {
                    case "SEND_GAMESTATE":
                        String players = substrings[1];
                        String playerWithBall = substrings[2];
                        client.setGamestate(players, playerWithBall);
                        break;
                    case "MOVE_BALL":
                        client.newBallPosition(Integer.parseInt(firstParameter));
                        break;
                    case "PLAYER_JOIN":
                        client.addNewPlayer(firstParameter);
                        break;
                    case "PLAYER_LEAVE":
                        client.removePlayer(firstParameter);
                        break;
                    case "ERROR":
                        client.showErrorToClient(line);
                        break;
                }
            }
            //Thrown when connection is lost
            catch (NoSuchElementException e) {
                System.out.println("Server disconnected.");
                if (autoRestart) {
                    try {
                        client.startNewServer();
                    } catch (IOException ex) {
                        System.out.println("Failed to start new server.");
                        ex.printStackTrace();
                    }
                }
                return;
            }
        }
    }
}
