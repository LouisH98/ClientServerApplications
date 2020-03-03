using System;
using System.IO;
using System.Net.Sockets;

namespace Server
{
    class ClientHandler
    {

        private TcpClient client;
        private Game game;
        private Player player;

        public ClientHandler(TcpClient client, Game game)
        {
            this.client = client;
            this.game = game;
        }

        public void Run()
        {

            using (Stream stream = client.GetStream())
            {
                StreamReader reader = new StreamReader(stream);
                StreamWriter writer = new StreamWriter(stream);

                writer.AutoFlush = true;

                //ignore rejoin message as functionality has not been implemented in C#
                reader.ReadLine();

                player = new Player(game.getNewClientID(), game.IsEmpty(), writer, reader);

                

                if (game.IsEmpty())
                {
                    Console.WriteLine($"Player connected with ID: {player.ID} and recieved the ball");
                }
                else
                {
                    Console.WriteLine($"Player connected with ID: {player.ID}");
                }

                game.AddPlayer(player);

                writer.WriteLine($"ASSIGN_ID {player.ID}");

                writer.WriteLine($"SEND_GAMESTATE {game.GetPlayersAsString()} {game.GetPlayerWithBall().ID}");



                try
                {
                    while (true)
                    {
                        string line = reader.ReadLine();
                        string[] substrings = line.Split(' ');

                        string command = substrings[0];

                        switch (command.ToUpper())
                        {
                            case "SEND_BALL":
                                int playerID = int.Parse(substrings[1]);
                                Player recievingPlayer = game.GetPlayerFromID(playerID);

                                if (recievingPlayer != null)
                                {
                                    game.SendBallTo(player, recievingPlayer, true);
                                }
                                break;
                        }

                    }
                }
                catch (Exception)
                {
                    client.Close();
                }
                finally
                {
                    Console.WriteLine($"Player {player.ID} disconnected.");
                    game.RemovePlayer(player);
                }
            }
        }
    }
}
