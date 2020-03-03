using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClientSolution
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            CountdownEvent receivedGamestateLatch = new CountdownEvent(1);
            using (Client client = new Client(receivedGamestateLatch))
            {


                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                GUI gui = new GUI(client);

                client.gui = gui;

                ClientListener listener = new ClientListener(client);
                new Thread(new ThreadStart(listener.Run)).Start();

                receivedGamestateLatch.Wait();

                Application.Run(gui);
            }
        }
    }
}
