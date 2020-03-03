package Server;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    //keeps count of clients, is incremented every time a client connects
    private static AtomicInteger currentClientID;
    private final List<Player> playerList;
    private final List<Player> rejoinList;
    private CountDownLatch gameStateProcessedLatch;

    public Game(CountDownLatch gamestateProcessedLatch) {
        currentClientID = new AtomicInteger(1);

        playerList = Collections.synchronizedList(new ArrayList<>());
        rejoinList = Collections.synchronizedList(new ArrayList<>());
        this.gameStateProcessedLatch = gamestateProcessedLatch;
    }

    public void processGamestateFromCommandLine(String gamestate) {

        String[] gamestateParts = gamestate.split(" ");

        String[] players = gamestateParts[0].split(",");

        int playerWithBall = Integer.parseInt(gamestateParts[1]);

        for (String playerID : players) {
            int playerIDInt = Integer.parseInt(playerID);

            if (playerIDInt >= currentClientID.get()) {
                currentClientID.set(playerIDInt + 1);
            }
            boolean hasBall = (playerIDInt == playerWithBall);

            Player player = new Player(playerIDInt, hasBall, null, null);


            rejoinList.add(player);

            gameStateProcessedLatch.countDown();

            System.out.println("Processed gamestate from command-line:" + gamestate);

            /*
            Check if the player's reader and writer are still null. (after 1500ms)
            If they are, then the player disconnected and should be removed.
             */
            TimerTask checkUserConnection = new TimerTask() {
                @Override
                public void run() {
                    if (player.getScanner() == null || player.getWriter() == null) {
                        rejoinList.remove(player);
                        sendMessageToAllClients("PLAYER_LEAVE " + player.getID());

                        System.out.println("Player " + player.getID() + " failed to reconnect.");
                        if (player.hasBall()) {
                            if (!playerList.isEmpty()) {
                                sendBallTo(player, playerList.get(0), false);
                            }
                        }
                    }
                }
            };

            Timer timer = new Timer("Timer");
            timer.schedule(checkUserConnection, 1500L);
        }
    }

    private void sendMessageToAllClients(String message) {
        synchronized (playerList) {
            for (Player player : playerList) {
                PrintWriter writer = player.getWriter();
                writer.println(message);
            }
        }
    }

    private void sendErrorToClient(String errMsg, PrintWriter writer) {
        writer.println("ERROR " + errMsg);
    }

    private void notifyNewPlayer(Player newPlayer) {
        synchronized (playerList) {
            for (Player otherPlayer : playerList) {
                //alert all other players of the new player
                if (!otherPlayer.equals(newPlayer)) {
                    PrintWriter writer = otherPlayer.getWriter();
                    writer.println("PLAYER_JOIN " + newPlayer.getID());
                }
            }
        }
    }

    /*
    Look through the player list and the
    rejoin list to find a player with that ID
     */
    public Player getPlayerFromID(int id) {
        synchronized (playerList) {
            synchronized (rejoinList) {
                for (Player player : playerList) {
                    if (player.getID() == id) {
                        return player;
                    }
                }

                for (Player player : rejoinList) {
                    if (player.getID() == id) {
                        return player;
                    }
                }
                return null;
            }
        }
    }

    public void rejoinPlayer(Player player) {
        synchronized (playerList) {
            synchronized (rejoinList) {
                if (rejoinList.contains(player)) {
                    playerList.add(player);
                    rejoinList.remove(player);
                    System.out.println("Rejoined player: " + player.getID());
                }
            }
        }

    }

    /*
    Function called when the SEND_BALL command is received or if a user disconnects and they have the ball.
     */
    public void sendBallTo(Player sendingPlayer, Player receivingPlayer, boolean announce) {

        if (!sendingPlayer.hasBall()) {
            sendErrorToClient("Cannot send ball (you do not have the ball, " + getPlayerWithBall().getID() + " does)", sendingPlayer.getWriter());
        } else if (!playerList.contains(receivingPlayer)) {
            sendingPlayer.setHasBall(true);
            sendErrorToClient("The player you are sending to is no longer in the game", sendingPlayer.getWriter());
        } else {
            sendingPlayer.setHasBall(false);
            receivingPlayer.setHasBall(true);
            sendMessageToAllClients("MOVE_BALL " + receivingPlayer.getID());

            if (announce) {
                System.out.println("Ball: (" + sendingPlayer.getID() + ") --> (" + receivingPlayer.getID() + ")");
            }
        }
    }


    public Player getPlayerWithBall() {
        synchronized (playerList) {
            synchronized (rejoinList) {
                for (Player player : playerList) {
                    if (player.hasBall()) {
                        return player;
                    }
                }

                for (Player player : rejoinList) {
                    if (player.hasBall()) {
                        return player;
                    }
                }
                return null;
            }
        }
    }


    public void addPlayer(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            notifyNewPlayer(player);
            showPlayers();
        }
    }

    public void removePlayer(Player leavingPlayer) {
        synchronized (playerList){
            playerList.remove(leavingPlayer);

            sendMessageToAllClients("PLAYER_LEAVE " + leavingPlayer.getID());

            //if the player had the ball, reassign it to someone else
            if (leavingPlayer.hasBall() && !isEmpty()) {
                Player newBallOwner = playerList.get(0);
                System.out.println(leavingPlayer.getID() + " had the ball and left.\nReassigned to player: " + newBallOwner.getID());
                sendBallTo(leavingPlayer, newBallOwner, false);
            } else if (leavingPlayer.hasBall()) {
                System.out.println("Last player has left... Waiting for new connections.");
            }

            if (!isEmpty()) {
                showPlayers();
            }
        }
    }

    public String getPlayersAsString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        synchronized (playerList) {
            for (Player player : playerList) {
                builder.append(player.getID());
                builder.append(",");
            }
        }

        builder.deleteCharAt(builder.lastIndexOf(","));

        return builder.toString();
    }

    public void showPlayers() {
        System.out.println("Players in server now: " + getPlayersAsString());
    }

    public boolean isEmpty() {
        return playerList.isEmpty();
    }

    public int getNewClientID() {
        return currentClientID.getAndIncrement();
    }


}
