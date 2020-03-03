using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Server
{
    class Server
    {

        private const int PORT = 8000;

        private static readonly Game game = new Game();
        static void Main(string[] args)
        {
            StartServer();
        }


        private static void StartServer()
        {
            try
            {
                TcpListener listener = new TcpListener(IPAddress.Loopback, PORT);
                listener.Start();
                Console.WriteLine("Server started - listening for connections");

                while (true)
                {
                    TcpClient client = listener.AcceptTcpClient();
                    ClientHandler handler = new ClientHandler(client, game);
                    new Thread(new ThreadStart(handler.Run)).Start();


                }
            }
            catch(SocketException)
            {
                Console.WriteLine("This port is already being used. Maybe another server is already running?");
            }
        }
    }
}
