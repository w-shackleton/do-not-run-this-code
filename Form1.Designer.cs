namespace DroidPad
{
    partial class Form1
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.panel1 = new System.Windows.Forms.Panel();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.statusUInst = new System.Windows.Forms.CheckBox();
            this.statusPConf = new System.Windows.Forms.CheckBox();
            this.statusPInst = new System.Windows.Forms.CheckBox();
            this.label1 = new System.Windows.Forms.Label();
            this.buttonsLabel = new System.Windows.Forms.Label();
            this.labelZ = new System.Windows.Forms.Label();
            this.labelY = new System.Windows.Forms.Label();
            this.labelX = new System.Windows.Forms.Label();
            this.Stopit = new System.Windows.Forms.Button();
            this.USBMode = new System.Windows.Forms.CheckBox();
            this.settingsButton = new System.Windows.Forms.Button();
            this.label8 = new System.Windows.Forms.Label();
            this.IP3 = new System.Windows.Forms.TextBox();
            this.IP4 = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.StartProg = new System.Windows.Forms.Button();
            this.IP2 = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.IP1 = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.StatBox = new System.Windows.Forms.TextBox();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.startToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.stopToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.settingsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.chooseDeviceToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.aboutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.websiteToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.manualToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.checkForUpdatesToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.tableLayoutPanel1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.menuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 1;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel1.Controls.Add(this.panel1, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.StatBox, 0, 1);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(0, 24);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 2;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle());
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle());
            this.tableLayoutPanel1.Size = new System.Drawing.Size(345, 244);
            this.tableLayoutPanel1.TabIndex = 5;
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.groupBox1);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.buttonsLabel);
            this.panel1.Controls.Add(this.labelZ);
            this.panel1.Controls.Add(this.labelY);
            this.panel1.Controls.Add(this.labelX);
            this.panel1.Controls.Add(this.Stopit);
            this.panel1.Controls.Add(this.USBMode);
            this.panel1.Controls.Add(this.settingsButton);
            this.panel1.Controls.Add(this.label8);
            this.panel1.Controls.Add(this.IP3);
            this.panel1.Controls.Add(this.IP4);
            this.panel1.Controls.Add(this.label6);
            this.panel1.Controls.Add(this.StartProg);
            this.panel1.Controls.Add(this.IP2);
            this.panel1.Controls.Add(this.label5);
            this.panel1.Controls.Add(this.IP1);
            this.panel1.Controls.Add(this.label4);
            this.panel1.Location = new System.Drawing.Point(3, 3);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(339, 136);
            this.panel1.TabIndex = 0;
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.statusUInst);
            this.groupBox1.Controls.Add(this.statusPConf);
            this.groupBox1.Controls.Add(this.statusPInst);
            this.groupBox1.Location = new System.Drawing.Point(196, 28);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(130, 91);
            this.groupBox1.TabIndex = 31;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Installation Status";
            // 
            // statusUInst
            // 
            this.statusUInst.AutoSize = true;
            this.statusUInst.Enabled = false;
            this.statusUInst.Location = new System.Drawing.Point(6, 65);
            this.statusUInst.Name = "statusUInst";
            this.statusUInst.Size = new System.Drawing.Size(120, 17);
            this.statusUInst.TabIndex = 2;
            this.statusUInst.Text = "USB Mode Installed";
            this.statusUInst.UseVisualStyleBackColor = true;
            // 
            // statusPConf
            // 
            this.statusPConf.AutoSize = true;
            this.statusPConf.Enabled = false;
            this.statusPConf.Location = new System.Drawing.Point(6, 42);
            this.statusPConf.Name = "statusPConf";
            this.statusPConf.Size = new System.Drawing.Size(110, 17);
            this.statusPConf.TabIndex = 1;
            this.statusPConf.Text = "PPJoy Configured";
            this.statusPConf.UseVisualStyleBackColor = true;
            // 
            // statusPInst
            // 
            this.statusPInst.AutoSize = true;
            this.statusPInst.Enabled = false;
            this.statusPInst.Location = new System.Drawing.Point(6, 19);
            this.statusPInst.Name = "statusPInst";
            this.statusPInst.Size = new System.Drawing.Size(98, 17);
            this.statusPInst.TabIndex = 0;
            this.statusPInst.Text = "PPJoy Installed";
            this.statusPInst.UseVisualStyleBackColor = true;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(9, 106);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(10, 13);
            this.label1.TabIndex = 29;
            this.label1.Text = "-";
            // 
            // buttonsLabel
            // 
            this.buttonsLabel.AutoSize = true;
            this.buttonsLabel.Location = new System.Drawing.Point(9, 93);
            this.buttonsLabel.Name = "buttonsLabel";
            this.buttonsLabel.Size = new System.Drawing.Size(10, 13);
            this.buttonsLabel.TabIndex = 28;
            this.buttonsLabel.Text = "-";
            // 
            // labelZ
            // 
            this.labelZ.AutoSize = true;
            this.labelZ.Location = new System.Drawing.Point(9, 80);
            this.labelZ.Name = "labelZ";
            this.labelZ.Size = new System.Drawing.Size(10, 13);
            this.labelZ.TabIndex = 25;
            this.labelZ.Text = "-";
            // 
            // labelY
            // 
            this.labelY.AutoSize = true;
            this.labelY.Location = new System.Drawing.Point(9, 67);
            this.labelY.Name = "labelY";
            this.labelY.Size = new System.Drawing.Size(10, 13);
            this.labelY.TabIndex = 21;
            this.labelY.Text = "-";
            // 
            // labelX
            // 
            this.labelX.AutoSize = true;
            this.labelX.Location = new System.Drawing.Point(9, 54);
            this.labelX.Name = "labelX";
            this.labelX.Size = new System.Drawing.Size(10, 13);
            this.labelX.TabIndex = 20;
            this.labelX.Text = "-";
            // 
            // Stopit
            // 
            this.Stopit.Enabled = false;
            this.Stopit.Location = new System.Drawing.Point(133, 29);
            this.Stopit.Name = "Stopit";
            this.Stopit.Size = new System.Drawing.Size(57, 22);
            this.Stopit.TabIndex = 8;
            this.Stopit.Text = "Stop";
            this.Stopit.UseVisualStyleBackColor = true;
            this.Stopit.Click += new System.EventHandler(this.Stopit_Click);
            // 
            // USBMode
            // 
            this.USBMode.AutoSize = true;
            this.USBMode.Location = new System.Drawing.Point(215, 5);
            this.USBMode.Name = "USBMode";
            this.USBMode.Size = new System.Drawing.Size(78, 17);
            this.USBMode.TabIndex = 5;
            this.USBMode.Text = "USB Mode";
            this.USBMode.UseVisualStyleBackColor = true;
            this.USBMode.CheckedChanged += new System.EventHandler(this.USBMode_CheckedChanged);
            // 
            // settingsButton
            // 
            this.settingsButton.Location = new System.Drawing.Point(9, 29);
            this.settingsButton.Name = "settingsButton";
            this.settingsButton.Size = new System.Drawing.Size(57, 22);
            this.settingsButton.TabIndex = 6;
            this.settingsButton.Text = "Settings";
            this.settingsButton.UseVisualStyleBackColor = true;
            this.settingsButton.Click += new System.EventHandler(this.settingsButton_Click);
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(166, 6);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(10, 13);
            this.label8.TabIndex = 11;
            this.label8.Text = ".";
            // 
            // IP3
            // 
            this.IP3.Location = new System.Drawing.Point(133, 3);
            this.IP3.Name = "IP3";
            this.IP3.Size = new System.Drawing.Size(27, 20);
            this.IP3.TabIndex = 3;
            this.IP3.Text = "1";
            // 
            // IP4
            // 
            this.IP4.Location = new System.Drawing.Point(182, 3);
            this.IP4.Name = "IP4";
            this.IP4.Size = new System.Drawing.Size(27, 20);
            this.IP4.TabIndex = 4;
            this.IP4.Text = "0";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(117, 6);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(10, 13);
            this.label6.TabIndex = 7;
            this.label6.Text = ".";
            // 
            // StartProg
            // 
            this.StartProg.Location = new System.Drawing.Point(70, 29);
            this.StartProg.Name = "StartProg";
            this.StartProg.Size = new System.Drawing.Size(57, 22);
            this.StartProg.TabIndex = 7;
            this.StartProg.Text = "Start";
            this.StartProg.UseVisualStyleBackColor = true;
            this.StartProg.Click += new System.EventHandler(this.StartProg_Click);
            // 
            // IP2
            // 
            this.IP2.Location = new System.Drawing.Point(84, 3);
            this.IP2.Name = "IP2";
            this.IP2.Size = new System.Drawing.Size(27, 20);
            this.IP2.TabIndex = 2;
            this.IP2.Text = "168";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(68, 6);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(10, 13);
            this.label5.TabIndex = 5;
            this.label5.Text = ".";
            // 
            // IP1
            // 
            this.IP1.Location = new System.Drawing.Point(35, 3);
            this.IP1.Name = "IP1";
            this.IP1.Size = new System.Drawing.Size(27, 20);
            this.IP1.TabIndex = 1;
            this.IP1.Text = "192";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(9, 6);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(20, 13);
            this.label4.TabIndex = 3;
            this.label4.Text = "IP:";
            // 
            // StatBox
            // 
            this.StatBox.Dock = System.Windows.Forms.DockStyle.Fill;
            this.StatBox.Location = new System.Drawing.Point(3, 145);
            this.StatBox.Multiline = true;
            this.StatBox.Name = "StatBox";
            this.StatBox.Size = new System.Drawing.Size(339, 120);
            this.StatBox.TabIndex = 9;
            // 
            // menuStrip1
            // 
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.helpToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(345, 24);
            this.menuStrip1.TabIndex = 0;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.startToolStripMenuItem,
            this.stopToolStripMenuItem,
            this.toolStripSeparator1,
            this.settingsToolStripMenuItem,
            this.chooseDeviceToolStripMenuItem});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(35, 20);
            this.fileToolStripMenuItem.Text = "File";
            // 
            // startToolStripMenuItem
            // 
            this.startToolStripMenuItem.Name = "startToolStripMenuItem";
            this.startToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.startToolStripMenuItem.Text = "Start";
            this.startToolStripMenuItem.Click += new System.EventHandler(this.StartProg_Click);
            // 
            // stopToolStripMenuItem
            // 
            this.stopToolStripMenuItem.Name = "stopToolStripMenuItem";
            this.stopToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.stopToolStripMenuItem.Text = "Stop";
            this.stopToolStripMenuItem.Click += new System.EventHandler(this.Stopit_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(153, 6);
            // 
            // settingsToolStripMenuItem
            // 
            this.settingsToolStripMenuItem.Name = "settingsToolStripMenuItem";
            this.settingsToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.settingsToolStripMenuItem.Text = "Settings";
            this.settingsToolStripMenuItem.Click += new System.EventHandler(this.settingsButton_Click);
            // 
            // chooseDeviceToolStripMenuItem
            // 
            this.chooseDeviceToolStripMenuItem.Name = "chooseDeviceToolStripMenuItem";
            this.chooseDeviceToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.chooseDeviceToolStripMenuItem.Text = "Choose Device";
            this.chooseDeviceToolStripMenuItem.Click += new System.EventHandler(this.chooseDeviceToolStripMenuItem_Click);
            // 
            // helpToolStripMenuItem
            // 
            this.helpToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.aboutToolStripMenuItem,
            this.websiteToolStripMenuItem,
            this.manualToolStripMenuItem,
            this.toolStripSeparator2,
            this.checkForUpdatesToolStripMenuItem});
            this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
            this.helpToolStripMenuItem.Size = new System.Drawing.Size(40, 20);
            this.helpToolStripMenuItem.Text = "Help";
            // 
            // aboutToolStripMenuItem
            // 
            this.aboutToolStripMenuItem.Name = "aboutToolStripMenuItem";
            this.aboutToolStripMenuItem.Size = new System.Drawing.Size(174, 22);
            this.aboutToolStripMenuItem.Text = "About";
            this.aboutToolStripMenuItem.Click += new System.EventHandler(this.aboutToolStripMenuItem_Click);
            // 
            // websiteToolStripMenuItem
            // 
            this.websiteToolStripMenuItem.Name = "websiteToolStripMenuItem";
            this.websiteToolStripMenuItem.Size = new System.Drawing.Size(174, 22);
            this.websiteToolStripMenuItem.Text = "Website";
            this.websiteToolStripMenuItem.Click += new System.EventHandler(this.websiteToolStripMenuItem_Click);
            // 
            // manualToolStripMenuItem
            // 
            this.manualToolStripMenuItem.Name = "manualToolStripMenuItem";
            this.manualToolStripMenuItem.Size = new System.Drawing.Size(174, 22);
            this.manualToolStripMenuItem.Text = "Manual";
            this.manualToolStripMenuItem.Click += new System.EventHandler(this.manualToolStripMenuItem_Click);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(171, 6);
            // 
            // checkForUpdatesToolStripMenuItem
            // 
            this.checkForUpdatesToolStripMenuItem.Name = "checkForUpdatesToolStripMenuItem";
            this.checkForUpdatesToolStripMenuItem.Size = new System.Drawing.Size(174, 22);
            this.checkForUpdatesToolStripMenuItem.Text = "Check for Updates";
            this.checkForUpdatesToolStripMenuItem.Click += new System.EventHandler(this.checkForUpdatesToolStripMenuItem_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(345, 268);
            this.Controls.Add(this.tableLayoutPanel1);
            this.Controls.Add(this.menuStrip1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MainMenuStrip = this.menuStrip1;
            this.MaximizeBox = false;
            this.MaximumSize = new System.Drawing.Size(477, 500);
            this.MinimumSize = new System.Drawing.Size(353, 302);
            this.Name = "Form1";
            this.Text = "DroidPad";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form1_FormClosing);
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button StartProg;
        private System.Windows.Forms.TextBox StatBox;
        private System.Windows.Forms.TextBox IP1;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.TextBox IP2;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.TextBox IP3;
        private System.Windows.Forms.TextBox IP4;
        private System.Windows.Forms.Button settingsButton;
        private System.Windows.Forms.CheckBox USBMode;
        private System.Windows.Forms.Button Stopit;
        private System.Windows.Forms.Label labelY;
        private System.Windows.Forms.Label labelX;
        private System.Windows.Forms.Label labelZ;
        private System.Windows.Forms.Label buttonsLabel;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.CheckBox statusUInst;
        private System.Windows.Forms.CheckBox statusPConf;
        private System.Windows.Forms.CheckBox statusPInst;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem startToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem stopToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripMenuItem settingsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem aboutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem websiteToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem manualToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem chooseDeviceToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripMenuItem checkForUpdatesToolStripMenuItem;

    }
}

