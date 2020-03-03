namespace ClientSolution
{
    partial class PlayerPanel
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.hasBallLabel = new System.Windows.Forms.Label();
            this.playerIDLabel = new System.Windows.Forms.Label();
            this.playerIDText = new System.Windows.Forms.Label();
            this.sendBallButton = new System.Windows.Forms.Button();
            this.tableLayoutPanel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 1;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle());
            this.tableLayoutPanel1.Controls.Add(this.hasBallLabel, 0, 2);
            this.tableLayoutPanel1.Controls.Add(this.playerIDLabel, 0, 1);
            this.tableLayoutPanel1.Controls.Add(this.playerIDText, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.sendBallButton, 0, 3);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(0, 0);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 4;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 25F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(140, 160);
            this.tableLayoutPanel1.TabIndex = 0;
            // 
            // hasBallLabel
            // 
            this.hasBallLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.hasBallLabel.AutoSize = true;
            this.hasBallLabel.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
            this.hasBallLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 14.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.hasBallLabel.ForeColor = System.Drawing.Color.LimeGreen;
            this.hasBallLabel.Location = new System.Drawing.Point(3, 80);
            this.hasBallLabel.Name = "hasBallLabel";
            this.hasBallLabel.Size = new System.Drawing.Size(134, 40);
            this.hasBallLabel.TabIndex = 2;
            this.hasBallLabel.Text = "Has ball!";
            this.hasBallLabel.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // playerIDLabel
            // 
            this.playerIDLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.playerIDLabel.AutoSize = true;
            this.playerIDLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 14.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.playerIDLabel.ForeColor = System.Drawing.Color.Blue;
            this.playerIDLabel.Location = new System.Drawing.Point(3, 40);
            this.playerIDLabel.Name = "playerIDLabel";
            this.playerIDLabel.Size = new System.Drawing.Size(134, 40);
            this.playerIDLabel.TabIndex = 1;
            this.playerIDLabel.Text = "0";
            this.playerIDLabel.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // playerIDText
            // 
            this.playerIDText.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.playerIDText.AutoSize = true;
            this.playerIDText.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.playerIDText.Location = new System.Drawing.Point(3, 0);
            this.playerIDText.Name = "playerIDText";
            this.playerIDText.Size = new System.Drawing.Size(134, 40);
            this.playerIDText.TabIndex = 0;
            this.playerIDText.Text = "Player ID:";
            this.playerIDText.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // sendBallButton
            // 
            this.sendBallButton.Dock = System.Windows.Forms.DockStyle.Fill;
            this.sendBallButton.Location = new System.Drawing.Point(3, 123);
            this.sendBallButton.Name = "sendBallButton";
            this.sendBallButton.Size = new System.Drawing.Size(134, 34);
            this.sendBallButton.TabIndex = 3;
            this.sendBallButton.Text = "Send Ball!";
            this.sendBallButton.UseVisualStyleBackColor = true;
            this.sendBallButton.Click += new System.EventHandler(this.sendBallButton_Click);
            // 
            // PlayerPanel
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.Control;
            this.Controls.Add(this.tableLayoutPanel1);
            this.Margin = new System.Windows.Forms.Padding(20);
            this.MaximumSize = new System.Drawing.Size(140, 160);
            this.MinimumSize = new System.Drawing.Size(140, 160);
            this.Name = "PlayerPanel";
            this.Size = new System.Drawing.Size(140, 160);
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.Label playerIDText;
        private System.Windows.Forms.Label hasBallLabel;
        private System.Windows.Forms.Label playerIDLabel;
        private System.Windows.Forms.Button sendBallButton;
    }
}
