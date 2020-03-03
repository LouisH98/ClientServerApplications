package Client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
JPanel for each player in the room
 */
class PlayerPanel extends JPanel {
    private JLabel playerIDLabel;
    private JLabel hasBallLabel;
    private JButton sendBallButton;
    private int playerID;
    private Client client;

    PlayerPanel(int id, boolean playerHasBall, Client client) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.client = client;
        this.playerID = id;


        playerIDLabel = new JLabel("", SwingConstants.CENTER);
        playerIDLabel.setFont(playerIDLabel.getFont().deriveFont(16f));
        hasBallLabel = new JLabel("", SwingConstants.CENTER);
        hasBallLabel.setFont(hasBallLabel.getFont().deriveFont(16f));

        sendBallButton = new JButton("Send Ball!");

        sendBallButton.setEnabled(client.hasBall());
        setPlayerHasBall(playerHasBall);

        playerIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hasBallLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendBallButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        String playerIDString;
        if (playerID == client.getPlayerID()) {
            playerIDString = playerID + " (you)";
        } else {
            playerIDString = Integer.toString(playerID);
        }

        playerIDLabel.setText("<html><p align=\"center\">Player ID: <br/><font color=blue>" + playerIDString + "</font></p></html>");


        add(Box.createHorizontalStrut(1));
        add(playerIDLabel);
        add(Box.createHorizontalStrut(1));
        add(hasBallLabel);
        add(Box.createHorizontalStrut(1));
        add(sendBallButton);
        add(Box.createHorizontalStrut(1));


        setPreferredSize(new Dimension(140, 160));
        repaint();
        revalidate();

        sendBallButton.addActionListener((event) -> {
            client.sendBallTo(playerID);
        });

    }

    int getID() {
        return this.playerID;
    }

    void setButtonEnabled(boolean enabled) {
        sendBallButton.setEnabled(enabled);
    }

    void setPlayerHasBall(boolean hasBall) {
        if (hasBall) {
            hasBallLabel.setText("Has ball!");
            hasBallLabel.setForeground(Color.green);
        } else {
            hasBallLabel.setText("No ball");
            hasBallLabel.setForeground(Color.red);
        }
    }


    /*
    Override paintComponent to add a custom background.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(10, 10);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //Draws the rounded panel with borders.
        graphics.setColor(new Color(139, 139, 139));
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}

public class GUI {
    private final Client client;
    private JFrame frame;
    private JPanel panel;
    private JPanel clientsPanel;
    private JLabel playerIDLabel;
    private JTextArea eventHistory;

    public GUI(Client client) {
        this.client = client;
        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        clientsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        String labelString = "<html><h2>Your ID: <font color=blue>" + client.getPlayerID() + "</font></h2></html>";
        playerIDLabel = new JLabel(labelString, SwingConstants.CENTER);

        eventHistory = new JTextArea();

        JScrollPane scrollPane = new JScrollPane(eventHistory);
        scrollPane.setPreferredSize(new Dimension(290, 0));

        //automatically scroll down
        DefaultCaret caret = (DefaultCaret) eventHistory.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        eventHistory.setEditable(false);

        frame.add(panel);
        panel.add(clientsPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.EAST);
        panel.add(playerIDLabel, BorderLayout.NORTH);

        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //get gamestate from client
        Gamestate gamestate = client.getGamestate();

        frame.setTitle("CE303 Ass1 (lh16556) - In game - ID: " + client.getPlayerID());

        addEventText("Welcome! There is " + gamestate.getPlayers().size() + " player/s in the game.");

        for (Integer playerID : gamestate.getPlayers()) {
            boolean playerHasBall = client.getPlayerWithBall() == playerID;
            addPlayerPanel(playerID, playerHasBall);
        }

        /*
        Handle window close - close the server if we are hosting one, and end the client process.
         */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.isClosing(true);
                client.closeServer();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        try {
            //latch is here so that the program waits until the clientListener receives the game state
            CountDownLatch gamestateReceivedLatch = new CountDownLatch(1);

            Client client = new Client(gamestateReceivedLatch);
            new Thread(new ClientListener(client)).start();

            try {
                gamestateReceivedLatch.await();
            } catch (InterruptedException e) {
                System.out.println("Countdown latch interrupted");
            }

            SwingUtilities.invokeLater(() -> {
                GUI gui = new GUI(client);
                client.setGUI(gui);
            });
        } catch (IOException e) {
            System.out.println("Could not connect to server");
        }
    }

    void addEventText(String text) {
        SwingUtilities.invokeLater(() -> eventHistory.append(text + "\n"));

    }

    void addPlayerPanel(int id, boolean hasBall) {
        if (!playerPanelExists(id)) {
            clientsPanel.add(new PlayerPanel(id, hasBall, client));
            clientsPanel.repaint();
            clientsPanel.revalidate();
        }
    }

    private boolean playerPanelExists(int id) {
        for (Component component : clientsPanel.getComponents()) {
            if (component.getClass() == PlayerPanel.class) {
                PlayerPanel panel = (PlayerPanel) component;
                if (panel.getID() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    void removePlayerPanel(int id) {
        //go through panels and remove when the ID matches the panel ID
        for (Component component : clientsPanel.getComponents()) {
            if (component.getClass() == PlayerPanel.class) {
                PlayerPanel panel = (PlayerPanel) component;
                if (panel.getID() == id) {
                    clientsPanel.remove(component);
                    clientsPanel.repaint();
                    clientsPanel.revalidate();
                }
            }
        }
    }

    void setPlayerWithBall(int newBallID) {
        for (Component component : clientsPanel.getComponents()) {
            if (component.getClass() == PlayerPanel.class) {
                PlayerPanel panel = (PlayerPanel) component;
                panel.setPlayerHasBall(panel.getID() == newBallID);
            }
        }
    }

    void setAllButtonsEnabled(boolean enabled) {
        for (Component component : clientsPanel.getComponents()) {
            if (component.getClass() == PlayerPanel.class) {
                PlayerPanel panel = (PlayerPanel) component;
                panel.setButtonEnabled(enabled);
            }
        }
    }

    void startNewListener() {
        new Thread(new ClientListener(client)).start();
    }
}
