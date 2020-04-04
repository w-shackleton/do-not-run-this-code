namespace DroidPad
{
    partial class StartUSB
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

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(StartUSB));
            this.status1 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.status2 = new System.Windows.Forms.Label();
            this.OKButton = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // status1
            // 
            this.status1.AutoSize = true;
            this.status1.Font = new System.Drawing.Font("Microsoft Sans Serif", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.status1.Location = new System.Drawing.Point(12, 9);
            this.status1.Name = "status1";
            this.status1.Size = new System.Drawing.Size(107, 25);
            this.status1.TabIndex = 0;
            this.status1.Text = "Loading...";
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(218, 82);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(81, 25);
            this.button1.TabIndex = 1;
            this.button1.Text = "Cancel";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // status2
            // 
            this.status2.AutoSize = true;
            this.status2.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.status2.Location = new System.Drawing.Point(12, 47);
            this.status2.Name = "status2";
            this.status2.Size = new System.Drawing.Size(17, 16);
            this.status2.TabIndex = 2;
            this.status2.Text = "...";
            // 
            // OKButton
            // 
            this.OKButton.Enabled = false;
            this.OKButton.Location = new System.Drawing.Point(12, 82);
            this.OKButton.Name = "OKButton";
            this.OKButton.Size = new System.Drawing.Size(81, 25);
            this.OKButton.TabIndex = 3;
            this.OKButton.Text = "OK";
            this.OKButton.UseVisualStyleBackColor = true;
            this.OKButton.Click += new System.EventHandler(this.OKButton_Click);
            // 
            // StartUSB
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(311, 119);
            this.ControlBox = false;
            this.Controls.Add(this.OKButton);
            this.Controls.Add(this.status2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.status1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MaximumSize = new System.Drawing.Size(317, 151);
            this.MinimizeBox = false;
            this.MinimumSize = new System.Drawing.Size(317, 151);
            this.Name = "StartUSB";
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.Text = "USB";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label status1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Label status2;
        private System.Windows.Forms.Button OKButton;
    }
}