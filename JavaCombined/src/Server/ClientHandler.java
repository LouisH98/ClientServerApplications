package Server;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ClientHandler implements Runnable {
    private Socket socket;
    private Game game;
    private Player player;

    public ClientHandler(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;

    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            //Look for rejoin message, and if received, get that player and assign the reader/writer
            String rejoinMessage = scanner.nextLine();
            String[] rejoinMessageSplit = rejoinMessage.split(" ");
            if (!rejoinMessage.isEmpty()) {
                int rejoinID = Integer.parseInt(rejoinMessageSplit[1]);

                this.player = game.getPlayerFromID(rejoinID);
                this.player.setScanner(scanner);
                this.player.setWriter(writer);

                game.rejoinPlayer(this.player);
            } else {
                this.player = new Player(game.getNewClientID(), game.isEmpty(), scanner, writer);
                //add player to game
                game.addPlayer(player);


                if (game.isEmpty()) {
                    System.out.println("Client connected with ID: " + player.getID() + " and has received the ball.");
                } else {
                    System.out.println("Client connected with ID: " + player.getID());
                }
            }

            //send the client their ID & the gamestate.
            writer.println("ASSIGN_ID " + player.getID());

            writer.println("SEND_GAMESTATE " + game.getPlayersAsString() + " " + game.getPlayerWithBall().getID());

            while (true) {
                try {
                    String line = scanner.nextLine();
                    String[] substrings = line.split(" ");

                    String command = substrings[0];

                    switch (command) {
                        case "SEND_BALL":
                            int playerID = Integer.parseInt(substrings[1]);
                            Player receivingPlayer = game.getPlayerFromID(playerID);

                            if (receivingPlayer != null) {
                                game.sendBallTo(player, receivingPlayer, true);
                            }
                            break;
                    }
                }
                //this exception is thrown when the client disconnects
                catch (NoSuchElementException e) {
                    System.out.println("Client with ID: " + player.getID() + " has disconnected");
                    game.removePlayer(player);
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {

        }
    }
}
