using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClientSolution
{
    public partial class PlayerPanel : UserControl
    {
        private Client client;
        public int playerID { get; private set; }

        public PlayerPanel(int id, bool playerHasBall, Client client)
        {
            InitializeComponent();

            this.client = client;
            this.playerID = id;

            sendBallButton.Enabled = client.HasBall();

            hasBallLabel.Text = playerHasBall ? "Has ball!" : "No ball.";
            hasBallLabel.ForeColor = playerHasBall ? Color.LimeGreen : Color.Red;

            String playerIDString;

            if(playerID == client.playerID)
            {
                playerIDString = playerID + " (you)";
            }
            else
            {
                playerIDString = playerID + "";
            }

            playerIDLabel.Text = playerIDString;
        }

        public PlayerPanel()
        {
            InitializeComponent();
        }


        private void sendBallButton_Click(object sender, EventArgs e)
        {
            client.SendBallTo(playerID);
        }

        public void SetButtonEnabled(bool enabled)
        {
            sendBallButton.BeginInvoke(new Action(() =>
            {
                sendBallButton.Enabled = enabled;
            }));
        }

        public void SetPlayerHasBall(bool hasBall)
        {
            if (hasBall)
            {
                hasBallLabel.BeginInvoke(new Action(() => {
                    hasBallLabel.Text = "Has ball!";
                    hasBallLabel.ForeColor = Color.LimeGreen;
                }));
                
            }
            else
            {
                hasBallLabel.BeginInvoke(new Action(() => {
                    hasBallLabel.ForeColor = Color.Red;
                    hasBallLabel.Text = "No ball";
                }));
            }
        }
    }
}
