package Tests;

import Client.Client;
import Client.ClientListener;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    static Process createServerProcess() throws IOException {
        return Runtime.getRuntime().exec(new String[]{"java", "-jar", "Server.jar"});
    }

    static void killServerProcess(Process serverProcess) {
        if (serverProcess != null) {
            try {
                serverProcess.destroy();
                serverProcess.waitFor();
            } catch (InterruptedException e) {
                fail();
            }
        }
    }

    static Client createNewClient(boolean autoRestart) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Client client = new Client(latch);
            new Thread(new ClientListener(client, autoRestart)).start();
            latch.await();

            return client;
        } catch (IOException | InterruptedException e) {
            fail();
        }

        return null;
    }
}
