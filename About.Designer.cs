namespace DroidPad
{
    partial class About
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(About));
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.label1 = new System.Windows.Forms.Label();
            this.VersionLabel = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.PWebsiteButton = new System.Windows.Forms.Button();
            this.PDocsButton = new System.Windows.Forms.Button();
            this.WebsiteButton = new System.Windows.Forms.Button();
            this.licenseButton = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(12, 12);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(120, 120);
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 20F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(138, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(127, 31);
            this.label1.TabIndex = 1;
            this.label1.Text = "DroidPad";
            // 
            // VersionLabel
            // 
            this.VersionLabel.AutoSize = true;
            this.VersionLabel.Location = new System.Drawing.Point(141, 40);
            this.VersionLabel.Name = "VersionLabel";
            this.VersionLabel.Size = new System.Drawing.Size(41, 13);
            this.VersionLabel.TabIndex = 2;
            this.VersionLabel.Text = "version";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(141, 53);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(146, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "Use your phone as a Joystick";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Microsoft Sans Serif", 16F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label3.Location = new System.Drawing.Point(6, 16);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(76, 26);
            this.label3.TabIndex = 4;
            this.label3.Text = "PPJoy";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label4.Location = new System.Drawing.Point(7, 42);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(110, 20);
            this.label4.TabIndex = 5;
            this.label4.Text = "Joystick Driver";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(6, 65);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(159, 52);
            this.label5.TabIndex = 6;
            this.label5.Text = "This application makes us of the\r\nPPJoy virtual joystick functions.\r\nPPJoy is cop" +
                "yrighted by\r\nDeon van der Westhuysen";
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.PWebsiteButton);
            this.groupBox1.Controls.Add(this.PDocsButton);
            this.groupBox1.Controls.Add(this.label3);
            this.groupBox1.Controls.Add(this.label4);
            this.groupBox1.Controls.Add(this.label5);
            this.groupBox1.Location = new System.Drawing.Point(12, 138);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(171, 161);
            this.groupBox1.TabIndex = 10;
            this.groupBox1.TabStop = false;
            // 
            // PWebsiteButton
            // 
            this.PWebsiteButton.Location = new System.Drawing.Point(71, 120);
            this.PWebsiteButton.Name = "PWebsiteButton";
            this.PWebsiteButton.Size = new System.Drawing.Size(54, 20);
            this.PWebsiteButton.TabIndex = 8;
            this.PWebsiteButton.Text = "Website";
            this.PWebsiteButton.UseVisualStyleBackColor = true;
            this.PWebsiteButton.Click += new System.EventHandler(this.PWebsiteButton_Click);
            // 
            // PDocsButton
            // 
            this.PDocsButton.Location = new System.Drawing.Point(11, 120);
            this.PDocsButton.Name = "PDocsButton";
            this.PDocsButton.Size = new System.Drawing.Size(54, 20);
            this.PDocsButton.TabIndex = 7;
            this.PDocsButton.Text = "Docs";
            this.PDocsButton.UseVisualStyleBackColor = true;
            this.PDocsButton.Click += new System.EventHandler(this.button1_Click);
            // 
            // WebsiteButton
            // 
            this.WebsiteButton.Location = new System.Drawing.Point(195, 69);
            this.WebsiteButton.Name = "WebsiteButton";
            this.WebsiteButton.Size = new System.Drawing.Size(87, 39);
            this.WebsiteButton.TabIndex = 11;
            this.WebsiteButton.Text = "DroidPad Website";
            this.WebsiteButton.UseVisualStyleBackColor = true;
            this.WebsiteButton.Click += new System.EventHandler(this.WebsiteButton_Click);
            // 
            // licenseButton
            // 
            this.licenseButton.Location = new System.Drawing.Point(195, 114);
            this.licenseButton.Name = "licenseButton";
            this.licenseButton.Size = new System.Drawing.Size(87, 29);
            this.licenseButton.TabIndex = 12;
            this.licenseButton.Text = "License";
            this.licenseButton.UseVisualStyleBackColor = true;
            this.licenseButton.Click += new System.EventHandler(this.licenseButton_Click);
            // 
            // About
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(294, 313);
            this.Controls.Add(this.licenseButton);
            this.Controls.Add(this.WebsiteButton);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.VersionLabel);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.pictureBox1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MaximumSize = new System.Drawing.Size(300, 345);
            this.MinimizeBox = false;
            this.MinimumSize = new System.Drawing.Size(300, 345);
            this.Name = "About";
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.Text = "About";
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label VersionLabel;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button PWebsiteButton;
        private System.Windows.Forms.Button PDocsButton;
        private System.Windows.Forms.Button WebsiteButton;
        private System.Windows.Forms.Button licenseButton;

    }
}