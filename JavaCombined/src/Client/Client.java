package Client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;


public class Client implements AutoCloseable {
    private static final String URL = "localhost";
    private static final int PORT = 8000;

    private GUI gui;

    private Process serverProcess = null;
    private boolean isClosing = false;

    private Scanner reader;
    private PrintWriter writer;

    private int playerID;
    private int playerWithBall;
    private List<Integer> players = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch receivedGamestateLatch;

    public Client(CountDownLatch latch) throws IOException {

        Socket socket = new Socket(URL, PORT);
        reader = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);

        //write a blank line to skip rejoin info on server
        writer.println("");

        //Can get ID here as we know it is going to be the first message from the server.
        String connectionResponse = reader.nextLine();
        String playerID = connectionResponse.split(" ")[1];

        this.playerID = Integer.parseInt(playerID);

        receivedGamestateLatch = latch;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public int getPlayerID() {
        return this.playerID;
    }

    public boolean hasBall() {
        return playerID == playerWithBall;
    }

    public int getPlayerWithBall() {
        return playerWithBall;
    }

    private void sendMessageToServer(String message) {
        writer.println(message);
    }

    public void sendBallTo(int playerID) {
        sendMessageToServer("SEND_BALL " + playerID);
    }

    public void showErrorToClient(String error) {
        SwingUtilities.invokeLater(() -> gui.addEventText(error));
    }

    /*
    This function is called from ClientListener when a 'PLAYER_JOIN' message is received.
    It adds the player ID to the array and updates the GUI.
     */
    public void addNewPlayer(String playerID) {
        int playerIDInt = Integer.parseInt(playerID);

        if (!players.contains(playerIDInt)) {
            System.out.println("New player: " + playerID);
            players.add(playerIDInt);

            if (gui != null) {
                SwingUtilities.invokeLater(() -> {
                    gui.addPlayerPanel(playerIDInt, playerIDInt == playerWithBall);
                    gui.addEventText("Player " + playerIDInt + " joined the game.");
                });
            }
        }
    }

    public void removePlayer(String leavingPlayerID) {
        Integer playerIDInteger = Integer.parseInt(leavingPlayerID);

        if (players.contains(playerIDInteger)) {
            SwingUtilities.invokeLater(() -> gui.removePlayerPanel(playerIDInteger));
            players.remove(playerIDInteger);
            gui.addEventText("Player " + playerIDInteger + " left the game.");
        }
    }

    public void setID(int id) {
        this.playerID = id;
    }


    public Gamestate getGamestate() {
        return new Gamestate(players, playerWithBall);
    }

    public void setGamestate(String playersCSV, String playerWithBall) {
        if (players.isEmpty()) {
            String[] playersList = playersCSV.split(",");

            for (int i = 0; i < playersList.length; i++) {
                players.add(Integer.parseInt(playersList[i]));
            }


            this.playerWithBall = Integer.parseInt(playerWithBall);

//            // allow main to continue
            receivedGamestateLatch.countDown();

        }
    }

    public void newBallPosition(int newBallID) {

        //update label
        SwingUtilities.invokeLater(() -> gui.setPlayerWithBall(newBallID));

        if (newBallID == this.playerID) {
            SwingUtilities.invokeLater(() -> {
                gui.setAllButtonsEnabled(true);
                gui.addEventText("You now have the ball!");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                gui.setAllButtonsEnabled(false);
                gui.addEventText("Player " + newBallID + " now has the ball.");
            });
        }
        playerWithBall = newBallID;
    }

    private void reconnectToServer() {
        try {
            Thread.sleep(1000);
            Socket socket = new Socket(URL, PORT);
            reader = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("REJOIN " + playerID);
            SwingUtilities.invokeLater(() -> gui.startNewListener());
            gui.addEventText("Reconnected to server.");

        } catch (Exception e) {
            e.printStackTrace();
            gui.addEventText("Failed to reconnect to server.");
        }

    }

    public void closeServer() {
        if (serverProcess != null) {
            serverProcess.destroy();
            try {
                serverProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    This function starts a new process of the server.jar.
    If the server has been started, the output is the redirected to the client (host)
     */
    public void startNewServer() throws IOException {
        if (!isClosing()) {

            gui.addEventText("Connection to server lost... Attempting to reconnect:");

            boolean serverFileExists = Files.exists(Paths.get("Server.jar"));
            System.out.println(serverFileExists ? "Found server file" : "Server file not found");
            serverProcess = Runtime.getRuntime().exec(new String[]{"java", "-jar", "Server.jar", getGamestate().toString()});

            //read output from server process and put it in the window.
            new Thread(() -> {
                try {
                    String lastLine = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
                    String line;

                    //get the output from the server
                    while ((line = reader.readLine()) != null) {
                        if (!line.equals(lastLine)) {
                            gui.addEventText("Server: " + line);
                        }
                        lastLine = line;
                    }
                    System.out.println("Disconnected from server output");
                } catch (IOException e) {
                    System.out.println("Disconnected from server output");
                }
            }).start();
            reconnectToServer();
        }
    }

    public boolean isClosing() {
        return this.isClosing;
    }

    public void isClosing(boolean closing) {
        this.isClosing = closing;
    }

    public Scanner getReader() {
        return this.reader;
    }

    @Override
    public void close() {
        reader.close();
        writer.close();
    }
}
