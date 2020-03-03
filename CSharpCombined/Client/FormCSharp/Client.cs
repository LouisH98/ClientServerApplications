using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace ClientSolution
{
    public class Gamestate
    {
        public List<int> players { get; private set; }
        public int playerWithBall { get; private set; }

        public Gamestate(List<int> players, int playerWithBall)
        {
            this.players = players;
            this.playerWithBall = playerWithBall;
        }

    }

    public class Client : IDisposable
    {
        private const string URL = "localhost";
        private const int PORT = 8000;

        public StreamReader reader { get; private set; }
        public StreamWriter writer { get; private set; }

        public int playerID { get; set; }
        public int playerWithBall { get; set; }

        public GUI gui { get; set; }

        private List<int> players = new List<int>();

        private CountdownEvent receivedGamestateLatch;

        public Client(CountdownEvent countdown)
        {

            TcpClient client = new TcpClient(URL, PORT);
            NetworkStream stream = client.GetStream();

            receivedGamestateLatch = countdown;

            reader = new StreamReader(stream);
            writer = new StreamWriter(stream);
            writer.AutoFlush = true;

            //write a blank line to pass reconnect info
            writer.WriteLine("");

            string connectionResponse = reader.ReadLine();
            String playerIDString = connectionResponse.Split(' ')[1];
            playerID = int.Parse(playerIDString);
            Console.WriteLine("Connected to server: " + playerID);
            

        }

        public bool HasBall() { return playerID == playerWithBall; }

        public void SendMessageToServer(string message)
        {
            writer.WriteLine(message);
        }

        public void SendBallTo(int sendingID)
        {
            SendMessageToServer($"SEND_BALL {sendingID}");
        }

        public void ShowErrorToClient(string errorMessage)
        {
            gui.AddEventText(errorMessage);
        }

        public void AddNewPlayer(String playerID)
        {
            int playerIDInt = int.Parse(playerID);

            if (!players.Contains(playerIDInt))
            {
                Console.WriteLine($"New player: {playerIDInt}");

                gui.AddPlayerPanel(playerIDInt, playerIDInt == playerWithBall);
                gui.AddEventText($"Player {playerIDInt} joined the game.");
                players.Add(playerIDInt);
            }
        }

        public void RemovePlayer(String leavingPlayerID)
        {
            int leavingIDInt = int.Parse(leavingPlayerID);

            if (players.Contains(leavingIDInt))
            {
                players.Remove(leavingIDInt);
                gui.RemovePlayerPanel(leavingIDInt);
                gui.AddEventText($"Player {leavingIDInt} left the game.");
                players.Remove(leavingIDInt);
            }
        }


        public void SetGamestate(String playersCSV, String playerWithBall)
        {
            string[] playerList = playersCSV.Split(',');

            lock (playerList)
            {
                foreach (String playerID in playerList)
                {
                    players.Add(int.Parse(playerID));
                }
            }
            this.playerWithBall = int.Parse(playerWithBall);
            receivedGamestateLatch.Signal();
        }

        public void NewBallPosition(int newBallID)
        {
            if(newBallID == this.playerID)
            {
                gui.SetPlayerWithBall(newBallID);
                gui.SetAllButtonsEnabled(true);
                gui.AddEventText("You now have the ball!");
            }
            else
            {
                gui.SetPlayerWithBall(newBallID);
                gui.SetAllButtonsEnabled(false);
                gui.AddEventText($"Player {newBallID} now has the ball.");
            }

            
            this.playerWithBall = newBallID;
        }

        public string GetGamestateAsString()
        {
            StringBuilder builder = new StringBuilder();

            lock (players)
            {
                foreach (int player in players)
                {
                    builder.Append(player);
                    builder.Append(',');
                }
            }

            builder.Remove(builder.Length - 1, 1);

            builder.Append($" ({playerWithBall} has the ball)");

            return builder.ToString();
        }

        public Gamestate GetGamestate()
        {
            return new Gamestate(players, playerWithBall);
        }

        public void Dispose()
        {
            reader.Close();
            writer.Close();
        }
    }
}
