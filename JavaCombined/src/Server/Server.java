package Server;
/*
CE303 Assignment 1
Louis Holdsworth - lh16556
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Server {

    private final static int PORT = 8000;
    private static final CountDownLatch gamestateProcessedLatch = new CountDownLatch(1);
    private static final Game game = new Game(gamestateProcessedLatch);
    private static boolean SERVER_RUNNING = true;

    public static void main(String[] args) {
        startServer(args);
    }

    private static void startServer(String[] args) {
        ServerSocket socketServer;

        if (args.length > 0) {
            game.processGamestateFromCommandLine(args[0]);
        } else {
            gamestateProcessedLatch.countDown();
        }

        //wait for the gamestate to be processed before allowing users to reconnect
        try {
            gamestateProcessedLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            socketServer = new ServerSocket(PORT);
            System.out.println("Server started - Awaiting connections");

            while (SERVER_RUNNING) {
                Socket socket = socketServer.accept();
                new Thread(new ClientHandler(socket, game)).start();
            }
        } catch (IOException e) {
            System.out.println("Port already in use. Maybe another server has already been started?");
            System.exit(1);
        }
    }
}
