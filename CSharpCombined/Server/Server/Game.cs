using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;

namespace Server
{
    class Game
    {
        private static int currentClientID;

        private static readonly List<Player> players = new List<Player>();

        public Game()
        {
            currentClientID = 0;
        }

        private void SendMessageToAllClients(string message)
        {
            lock (players)
            {
                try
                {
                    foreach (Player player in players)
                    {
                        StreamWriter writer = player.writer;
                        writer.WriteLine(message);

                    }
                }
                catch(ObjectDisposedException e)
                {
                    //thrown when trying to iterate through an element that doesnt 
                }

            }
        }

        private void SendErrorToClient(String errMessage, StreamWriter writer)
        {
            writer.WriteLine("ERROR " + errMessage);
        }

        private void NotifyNewPlayer(Player newPlayer)
        {
            lock (players)
            {
                foreach (Player otherPlayer in players)
                {
                    if (!otherPlayer.Equals(newPlayer))
                    {
                        StreamWriter writer = otherPlayer.writer;
                        writer.WriteLine($"PLAYER_JOIN {newPlayer.ID}");
                    }
                }
            }
        }

        public Player GetPlayerFromID(int id)
        {

            lock (players)
            {
                foreach (Player player in players)
                {
                    if (player.ID == id)
                    {
                        return player;
                    }
                }
            }
            return null;
        }

        public void SendBallTo(Player sendingPlayer, Player recievingPlayer, bool announce)
        {
            if (!sendingPlayer.hasBall)
            {
                SendErrorToClient("Cannot send ball as you do not have it.", sendingPlayer.writer);
            }
            else
            {
                sendingPlayer.hasBall = false;
                recievingPlayer.hasBall = true;
                SendMessageToAllClients($"MOVE_BALL {recievingPlayer.ID}");

                if (announce)
                {
                    Console.WriteLine($"Ball: ({sendingPlayer.ID}) --> ({recievingPlayer.ID})");
                }
            }
        }

        public Player GetPlayerWithBall()
        {
            lock (players)
            {
                foreach (Player player in players)
                {
                    if (player.hasBall)
                    {
                        return player;
                    }
                }
            }
            return null;
        }

        public void AddPlayer(Player player)
        {
            lock (players)
            {
                if (!players.Contains(player))
                {
                    players.Add(player);
                    NotifyNewPlayer(player);
                    ShowPlayers();
                }
            }
        }

        public void RemovePlayer(Player leavingPlayer)
        {
            lock (players)
            {
                if (players.Contains(leavingPlayer))
                {
                    players.Remove(leavingPlayer);
                }

                SendMessageToAllClients($"PLAYER_LEAVE {leavingPlayer.ID}");

                if (leavingPlayer.hasBall && !IsEmpty())
                {
                    Player newBallOwner = players.ElementAt(0);
                    Console.WriteLine($"{leavingPlayer.ID} left and the ball was reassigned to: {newBallOwner.ID}");
                    SendBallTo(leavingPlayer, newBallOwner, false);
                }
                else if (leavingPlayer.hasBall)
                {
                    Console.WriteLine("Last player has left. Waiting for new connections.");
                }

                if (!IsEmpty())
                {
                    ShowPlayers();
                }
            }

        }

        public String GetPlayersAsString()
        {
            if (IsEmpty())
            {
                return "";
            }


            StringBuilder builder = new StringBuilder();

            lock (players)
            {
                foreach(Player player in players)
                {
                    builder.Append(player.ID);
                    builder.Append(',');
                }

                builder.Remove(builder.Length - 1, 1);

                return builder.ToString();
            }
            
        }

        public void ShowPlayers()
        {
            Console.WriteLine($"Players in server right now: {GetPlayersAsString()} ({GetPlayerWithBall().ID} has the ball)");
        }

        public bool IsEmpty()
        {
            return players.Count == 0;
        }

        public int getNewClientID()
        {
            return Interlocked.Increment(ref currentClientID);
        }

    }
}
