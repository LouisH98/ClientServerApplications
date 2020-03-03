package Tests;

import Client.Client;
import Client.ClientListener;
import Client.GUI;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static Tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestServer {


    @Test
    void testSendingBall() {
        Process serverProcess = null;
        try {
            serverProcess = createServerProcess();

            Thread.sleep(1000);

            Client client1 = createNewClient(false);
            Client client2 = createNewClient(false);

            assertNotNull(client1);
            assertNotNull(client2);

            GUI gui = new GUI(client1);
            GUI gui2 = new GUI(client2);

            client1.setGUI(gui);
            client2.setGUI(gui2);

            client1.sendBallTo(2);

            //Sleep for a little while for the messages to be sent
            Thread.sleep(300);

            assertEquals(2, client1.getPlayerWithBall());
            assertEquals(2, client2.getPlayerWithBall());

            //send the ball back
            client2.sendBallTo(1);

            //Sleep for a little while for the messages to be sent
            Thread.sleep(300);

            assertEquals(1, client1.getPlayerWithBall());
            assertEquals(1, client2.getPlayerWithBall());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            killServerProcess(serverProcess);
        }
    }


    @Test
    void testGamestateReceived() {
        Process serverProcess = null;
        try {
            serverProcess = createServerProcess();

            Thread.sleep(500);

            CountDownLatch latch = new CountDownLatch(1);
            Client client = new Client(latch);
            new Thread(new ClientListener(client, false)).start();
            latch.await();

            //This is expected as the server will include the joining player in the gamestate
            String expectedGamestate = "1 1";
            String receivedGamestate = client.getGamestate().toString();

            assertEquals(expectedGamestate, receivedGamestate);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            killServerProcess(serverProcess);
        }
    }
}
