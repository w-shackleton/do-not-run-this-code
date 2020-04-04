namespace DroidPad
{
    partial class Settings
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Settings));
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.PortNum = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.ContNum = new System.Windows.Forms.TextBox();
            this.UpdInt = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.ok = new System.Windows.Forms.Button();
            this.cancel = new System.Windows.Forms.Button();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.ConfigurePPJoy = new System.Windows.Forms.Button();
            this.PPJoymode = new System.Windows.Forms.Label();
            this.UninstallPPJoy = new System.Windows.Forms.Button();
            this.InstallPPJoy = new System.Windows.Forms.Button();
            this.usbmode = new System.Windows.Forms.Label();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.button1 = new System.Windows.Forms.Button();
            this.InstallADB = new System.Windows.Forms.Button();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.pp64drv = new System.Windows.Forms.Button();
            this.label4 = new System.Windows.Forms.Label();
            this.groupBox2.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.groupBox4.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.groupBox5.SuspendLayout();
            this.SuspendLayout();
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.PortNum);
            this.groupBox2.Controls.Add(this.label3);
            this.groupBox2.Location = new System.Drawing.Point(12, 87);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(151, 44);
            this.groupBox2.TabIndex = 6;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Android Settings";
            // 
            // PortNum
            // 
            this.PortNum.Location = new System.Drawing.Point(92, 13);
            this.PortNum.Name = "PortNum";
            this.PortNum.Size = new System.Drawing.Size(53, 20);
            this.PortNum.TabIndex = 2;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(6, 16);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(51, 13);
            this.label3.TabIndex = 1;
            this.label3.Text = "Port Num";
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.ContNum);
            this.groupBox1.Controls.Add(this.UpdInt);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(151, 69);
            this.groupBox1.TabIndex = 5;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "PPJoy Settings";
            // 
            // ContNum
            // 
            this.ContNum.Location = new System.Drawing.Point(92, 39);
            this.ContNum.Name = "ContNum";
            this.ContNum.Size = new System.Drawing.Size(53, 20);
            this.ContNum.TabIndex = 3;
            // 
            // UpdInt
            // 
            this.UpdInt.Location = new System.Drawing.Point(92, 13);
            this.UpdInt.Name = "UpdInt";
            this.UpdInt.Size = new System.Drawing.Size(53, 20);
            this.UpdInt.TabIndex = 2;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 16);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(80, 13);
            this.label2.TabIndex = 1;
            this.label2.Text = "Update Interval";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 42);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(76, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Controller Num";
            // 
            // ok
            // 
            this.ok.Location = new System.Drawing.Point(13, 141);
            this.ok.Name = "ok";
            this.ok.Size = new System.Drawing.Size(61, 22);
            this.ok.TabIndex = 7;
            this.ok.Text = "OK";
            this.ok.UseVisualStyleBackColor = true;
            this.ok.Click += new System.EventHandler(this.ok_Click);
            // 
            // cancel
            // 
            this.cancel.Location = new System.Drawing.Point(80, 141);
            this.cancel.Name = "cancel";
            this.cancel.Size = new System.Drawing.Size(77, 22);
            this.cancel.TabIndex = 8;
            this.cancel.Text = "Cancel";
            this.cancel.UseVisualStyleBackColor = true;
            this.cancel.Click += new System.EventHandler(this.cancel_Click);
            // 
            // groupBox4
            // 
            this.groupBox4.Controls.Add(this.ConfigurePPJoy);
            this.groupBox4.Controls.Add(this.PPJoymode);
            this.groupBox4.Controls.Add(this.UninstallPPJoy);
            this.groupBox4.Controls.Add(this.InstallPPJoy);
            this.groupBox4.Location = new System.Drawing.Point(169, 14);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(115, 149);
            this.groupBox4.TabIndex = 11;
            this.groupBox4.TabStop = false;
            this.groupBox4.Text = "PPJoy Joystick";
            // 
            // ConfigurePPJoy
            // 
            this.ConfigurePPJoy.Enabled = false;
            this.ConfigurePPJoy.Location = new System.Drawing.Point(6, 119);
            this.ConfigurePPJoy.Name = "ConfigurePPJoy";
            this.ConfigurePPJoy.Size = new System.Drawing.Size(103, 23);
            this.ConfigurePPJoy.TabIndex = 3;
            this.ConfigurePPJoy.Text = "Configure";
            this.ConfigurePPJoy.UseVisualStyleBackColor = true;
            this.ConfigurePPJoy.Click += new System.EventHandler(this.ConfigurePPJoy_Click);
            // 
            // PPJoymode
            // 
            this.PPJoymode.AutoSize = true;
            this.PPJoymode.Location = new System.Drawing.Point(6, 16);
            this.PPJoymode.Name = "PPJoymode";
            this.PPJoymode.Size = new System.Drawing.Size(112, 26);
            this.PPJoymode.TabIndex = 2;
            this.PPJoymode.Text = "PPJoy Joystick Driver:\r\nNot Installed";
            // 
            // UninstallPPJoy
            // 
            this.UninstallPPJoy.Enabled = false;
            this.UninstallPPJoy.Location = new System.Drawing.Point(6, 90);
            this.UninstallPPJoy.Name = "UninstallPPJoy";
            this.UninstallPPJoy.Size = new System.Drawing.Size(103, 23);
            this.UninstallPPJoy.TabIndex = 1;
            this.UninstallPPJoy.Text = "Uninstall";
            this.UninstallPPJoy.UseVisualStyleBackColor = true;
            this.UninstallPPJoy.Click += new System.EventHandler(this.UninstallPPJoy_Click);
            // 
            // InstallPPJoy
            // 
            this.InstallPPJoy.Location = new System.Drawing.Point(6, 61);
            this.InstallPPJoy.Name = "InstallPPJoy";
            this.InstallPPJoy.Size = new System.Drawing.Size(103, 23);
            this.InstallPPJoy.TabIndex = 0;
            this.InstallPPJoy.Text = "Install";
            this.InstallPPJoy.UseVisualStyleBackColor = true;
            this.InstallPPJoy.Click += new System.EventHandler(this.InstallPPJoy_Click);
            // 
            // usbmode
            // 
            this.usbmode.AutoSize = true;
            this.usbmode.Location = new System.Drawing.Point(6, 16);
            this.usbmode.Name = "usbmode";
            this.usbmode.Size = new System.Drawing.Size(66, 26);
            this.usbmode.TabIndex = 2;
            this.usbmode.Text = "USB Mode:\r\nNot Installed";
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.button1);
            this.groupBox3.Controls.Add(this.InstallADB);
            this.groupBox3.Controls.Add(this.usbmode);
            this.groupBox3.Location = new System.Drawing.Point(290, 14);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(115, 119);
            this.groupBox3.TabIndex = 9;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "USB Mode";
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(6, 90);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(103, 23);
            this.button1.TabIndex = 4;
            this.button1.Text = "Install Instructions";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // InstallADB
            // 
            this.InstallADB.Location = new System.Drawing.Point(6, 61);
            this.InstallADB.Name = "InstallADB";
            this.InstallADB.Size = new System.Drawing.Size(103, 23);
            this.InstallADB.TabIndex = 3;
            this.InstallADB.Text = "Install";
            this.InstallADB.UseVisualStyleBackColor = true;
            this.InstallADB.Click += new System.EventHandler(this.InstallADB_Click);
            // 
            // checkBox1
            // 
            this.checkBox1.AutoSize = true;
            this.checkBox1.Location = new System.Drawing.Point(290, 139);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(58, 17);
            this.checkBox1.TabIndex = 12;
            this.checkBox1.Text = "Debug";
            this.checkBox1.UseVisualStyleBackColor = true;
            this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
            // 
            // groupBox5
            // 
            this.groupBox5.Controls.Add(this.pp64drv);
            this.groupBox5.Controls.Add(this.label4);
            this.groupBox5.Location = new System.Drawing.Point(411, 14);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(115, 102);
            this.groupBox5.TabIndex = 13;
            this.groupBox5.TabStop = false;
            this.groupBox5.Text = "PPJoy 64-bit enable";
            // 
            // pp64drv
            // 
            this.pp64drv.Location = new System.Drawing.Point(6, 71);
            this.pp64drv.Name = "pp64drv";
            this.pp64drv.Size = new System.Drawing.Size(103, 23);
            this.pp64drv.TabIndex = 3;
            this.pp64drv.Text = "Enable";
            this.pp64drv.UseVisualStyleBackColor = true;
            this.pp64drv.Click += new System.EventHandler(this.pp64drv_Click);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(6, 16);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(96, 52);
            this.label4.TabIndex = 2;
            this.label4.Text = "Requires restart,\r\nenables installation\r\nof PPJoy on 64-bit\r\nPCs";
            // 
            // Settings
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(533, 172);
            this.Controls.Add(this.groupBox5);
            this.Controls.Add(this.checkBox1);
            this.Controls.Add(this.groupBox4);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.cancel);
            this.Controls.Add(this.ok);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "Settings";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.Text = "Settings";
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox4.ResumeLayout(false);
            this.groupBox4.PerformLayout();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.groupBox5.ResumeLayout(false);
            this.groupBox5.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.TextBox PortNum;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.TextBox ContNum;
        private System.Windows.Forms.TextBox UpdInt;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button ok;
        private System.Windows.Forms.Button cancel;
        private System.Windows.Forms.GroupBox groupBox4;
        private System.Windows.Forms.Label PPJoymode;
        private System.Windows.Forms.Button UninstallPPJoy;
        private System.Windows.Forms.Button InstallPPJoy;
        private System.Windows.Forms.Button ConfigurePPJoy;
        private System.Windows.Forms.Label usbmode;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.Button InstallADB;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.GroupBox groupBox5;
        private System.Windows.Forms.Button pp64drv;
        private System.Windows.Forms.Label label4;
    }
}