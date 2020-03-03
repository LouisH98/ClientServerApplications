using System;
using System.Windows.Forms;

namespace ClientSolution
{
    public partial class GUI : Form
    {
        private Client client;

        public GUI(Client client)
        {
            InitializeComponent();

            this.client = client;

            
        }


        public void UpdateGamestate()
        {

            Text = $"CE303 Ass1 (lh16556) - In game - ID: {client.playerID}";

            Gamestate gamestate = client.GetGamestate();
            foreach (int playerID in gamestate.players)
            {
                bool playerHasBall = client.playerWithBall == playerID;
                AddPlayerPanel(playerID, playerHasBall);
            }

            AddEventText($"Welcome! There is {gamestate.players.Count} player/s in the game.");

            playerIDLabel.Text += client.playerID;
        }
        public void AddEventText(string text)
        {
            eventText.BeginInvoke(new Action(() => eventText.Text += (text + Environment.NewLine)));
        }

        public void AddPlayerPanel(int id, bool hasBall)
        {
            if (!PlayerPanelExists(id))
            {
                flowLayoutPanel.BeginInvoke(new Action(() => {
                    flowLayoutPanel.Controls.Add(new PlayerPanel(id, hasBall, client));
                }));
            }
        }

        private bool PlayerPanelExists(int id)
        {
            foreach(Control control in flowLayoutPanel.Controls)
            {
                if(control.GetType() == typeof(PlayerPanel))
                {
                    PlayerPanel panel = (PlayerPanel)control;

                    if(panel.playerID == id)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

       public void RemovePlayerPanel(int id)
        {
            foreach (Control control in flowLayoutPanel.Controls)
            {
                if (control.GetType() == typeof(PlayerPanel))
                {
                    PlayerPanel panel = (PlayerPanel)control;
                    if(panel.playerID == id)
                    {
                        flowLayoutPanel.BeginInvoke(new Action(() => {
                            flowLayoutPanel.Controls.Remove(panel);
                        }));
                    }
                }
            }
        }

        public void SetPlayerWithBall(int newBallID)
        {
            foreach (Control control in flowLayoutPanel.Controls)
            {
                if (control.GetType() == typeof(PlayerPanel))
                {
                    PlayerPanel panel = (PlayerPanel)control;
                    panel.SetPlayerHasBall(panel.playerID == newBallID);
                }
            }
        }

        public void SetAllButtonsEnabled(bool enabled)
        {
            foreach (Control control in flowLayoutPanel.Controls)
            {
                if (control.GetType() == typeof(PlayerPanel))
                {
                    PlayerPanel panel = (PlayerPanel)control;
                    panel.SetButtonEnabled(enabled);
                }
            }
        }

        private void GUI_Load(object sender, EventArgs e)
        {
            UpdateGamestate();
        }
    }
}
