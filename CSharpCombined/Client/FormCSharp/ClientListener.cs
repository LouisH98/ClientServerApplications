using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace ClientSolution
{
    class ClientListener
    {
        private Client client;

        public ClientListener(Client client) { this.client = client; }

        public void Run()
        {
            StreamReader reader = client.reader;

            while (true)
            {
                try
                {
                    string line = reader.ReadLine();
                    if(line == null)
                    {
                        line = "";
                    }

                    string[] substrings = line.Split(' ');

                    string command = substrings[0];
                    string firstParameter = substrings[1];

                    switch (command.ToUpper())
                    {
                        case "ASSIGN_ID":
                            int id = int.Parse(firstParameter);
                            client.playerID = id;
                            break;

                        case "SEND_GAMESTATE":
                            string players = firstParameter;
                            string playerWithBall = substrings[2];
                            Console.WriteLine("Got gamestate");
                            client.SetGamestate(players, playerWithBall);
                            break;
                        case "MOVE_BALL":
                            client.NewBallPosition(int.Parse(firstParameter));
                            break;

                        case "PLAYER_JOIN":
                            client.AddNewPlayer(firstParameter);
                            break;

                        case "PLAYER_LEAVE":
                            client.RemovePlayer(firstParameter);
                            break;

                        case "ERROR":
                            client.ShowErrorToClient(line);
                            break;
                    }
                }
                catch(Exception)
                {
                    Console.WriteLine("Server disconnected");
                    Console.ReadLine();
                    break;
                }
            }

        }
    }
}
