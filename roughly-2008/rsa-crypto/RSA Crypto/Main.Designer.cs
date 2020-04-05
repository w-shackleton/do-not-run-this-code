namespace RSA_Crypto
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
            this.Genkeys_button = new System.Windows.Forms.Button();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.FileToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.newToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.editToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.cutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.copyToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.pasteToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.selectAllToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.keysToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.myKeyToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.generateKeysToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.importKeyToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.encodeToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.encodeMessageToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.decodeToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.decodeMessageToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.toolsToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.resetSettingsToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.manageKeysToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.editToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.keysToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.generateKeysToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.importKeyToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.myKeyToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.encodeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.encodeMessageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.decodeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.decodeMessageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.optionsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.manageKeysToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.resetSettingsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.Text_Box = new System.Windows.Forms.TextBox();
            this.KeyListBox = new System.Windows.Forms.ListBox();
            this.LoadKeyDialog = new System.Windows.Forms.OpenFileDialog();
            this.MyKeyDialog = new System.Windows.Forms.OpenFileDialog();
            this.Dec_button = new System.Windows.Forms.Button();
            this.Enc_button = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.Proc_Img = new System.Windows.Forms.PictureBox();
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.Open_File = new System.Windows.Forms.OpenFileDialog();
            this.Save_File = new System.Windows.Forms.SaveFileDialog();
            this.linkLabel1 = new System.Windows.Forms.LinkLabel();
            this.menuStrip1.SuspendLayout();
            this.panel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Proc_Img)).BeginInit();
            this.tableLayoutPanel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // Genkeys_button
            // 
            this.Genkeys_button.Location = new System.Drawing.Point(3, 3);
            this.Genkeys_button.Name = "Genkeys_button";
            this.Genkeys_button.Size = new System.Drawing.Size(112, 37);
            this.Genkeys_button.TabIndex = 0;
            this.Genkeys_button.Text = "Generate Keys";
            this.Genkeys_button.UseVisualStyleBackColor = true;
            this.Genkeys_button.Click += new System.EventHandler(this.Genkeys_button_Click);
            // 
            // menuStrip1
            // 
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.FileToolStripMenuItem1,
            this.editToolStripMenuItem1,
            this.keysToolStripMenuItem1,
            this.encodeToolStripMenuItem1,
            this.decodeToolStripMenuItem1,
            this.toolsToolStripMenuItem1});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(474, 24);
            this.menuStrip1.TabIndex = 1;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // FileToolStripMenuItem1
            // 
            this.FileToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.newToolStripMenuItem,
            this.openToolStripMenuItem,
            this.saveToolStripMenuItem});
            this.FileToolStripMenuItem1.Name = "FileToolStripMenuItem1";
            this.FileToolStripMenuItem1.Size = new System.Drawing.Size(35, 20);
            this.FileToolStripMenuItem1.Text = "&File";
            // 
            // newToolStripMenuItem
            // 
            this.newToolStripMenuItem.Name = "newToolStripMenuItem";
            this.newToolStripMenuItem.Size = new System.Drawing.Size(111, 22);
            this.newToolStripMenuItem.Text = "&New";
            this.newToolStripMenuItem.Click += new System.EventHandler(this.newToolStripMenuItem_Click);
            // 
            // openToolStripMenuItem
            // 
            this.openToolStripMenuItem.Name = "openToolStripMenuItem";
            this.openToolStripMenuItem.Size = new System.Drawing.Size(111, 22);
            this.openToolStripMenuItem.Text = "&Open";
            this.openToolStripMenuItem.Click += new System.EventHandler(this.openToolStripMenuItem_Click);
            // 
            // saveToolStripMenuItem
            // 
            this.saveToolStripMenuItem.Name = "saveToolStripMenuItem";
            this.saveToolStripMenuItem.Size = new System.Drawing.Size(111, 22);
            this.saveToolStripMenuItem.Text = "&Save";
            this.saveToolStripMenuItem.Click += new System.EventHandler(this.saveToolStripMenuItem_Click);
            // 
            // editToolStripMenuItem1
            // 
            this.editToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cutToolStripMenuItem,
            this.copyToolStripMenuItem,
            this.pasteToolStripMenuItem,
            this.toolStripSeparator1,
            this.selectAllToolStripMenuItem});
            this.editToolStripMenuItem1.Name = "editToolStripMenuItem1";
            this.editToolStripMenuItem1.Size = new System.Drawing.Size(37, 20);
            this.editToolStripMenuItem1.Text = "&Edit";
            // 
            // cutToolStripMenuItem
            // 
            this.cutToolStripMenuItem.Name = "cutToolStripMenuItem";
            this.cutToolStripMenuItem.Size = new System.Drawing.Size(128, 22);
            this.cutToolStripMenuItem.Text = "Cu&t";
            this.cutToolStripMenuItem.Click += new System.EventHandler(this.cutToolStripMenuItem_Click);
            // 
            // copyToolStripMenuItem
            // 
            this.copyToolStripMenuItem.Name = "copyToolStripMenuItem";
            this.copyToolStripMenuItem.Size = new System.Drawing.Size(128, 22);
            this.copyToolStripMenuItem.Text = "&Copy";
            this.copyToolStripMenuItem.Click += new System.EventHandler(this.copyToolStripMenuItem_Click);
            // 
            // pasteToolStripMenuItem
            // 
            this.pasteToolStripMenuItem.Name = "pasteToolStripMenuItem";
            this.pasteToolStripMenuItem.Size = new System.Drawing.Size(128, 22);
            this.pasteToolStripMenuItem.Text = "&Paste";
            this.pasteToolStripMenuItem.Click += new System.EventHandler(this.pasteToolStripMenuItem_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(125, 6);
            // 
            // selectAllToolStripMenuItem
            // 
            this.selectAllToolStripMenuItem.Name = "selectAllToolStripMenuItem";
            this.selectAllToolStripMenuItem.Size = new System.Drawing.Size(128, 22);
            this.selectAllToolStripMenuItem.Text = "Select &All";
            this.selectAllToolStripMenuItem.Click += new System.EventHandler(this.selectAllToolStripMenuItem_Click);
            // 
            // keysToolStripMenuItem1
            // 
            this.keysToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.myKeyToolStripMenuItem1,
            this.generateKeysToolStripMenuItem1,
            this.importKeyToolStripMenuItem1});
            this.keysToolStripMenuItem1.Name = "keysToolStripMenuItem1";
            this.keysToolStripMenuItem1.Size = new System.Drawing.Size(42, 20);
            this.keysToolStripMenuItem1.Text = "&Keys";
            // 
            // myKeyToolStripMenuItem1
            // 
            this.myKeyToolStripMenuItem1.Name = "myKeyToolStripMenuItem1";
            this.myKeyToolStripMenuItem1.Size = new System.Drawing.Size(156, 22);
            this.myKeyToolStripMenuItem1.Text = "&My Key";
            this.myKeyToolStripMenuItem1.Click += new System.EventHandler(this.myKeyToolStripMenuItem_Click);
            // 
            // generateKeysToolStripMenuItem1
            // 
            this.generateKeysToolStripMenuItem1.Name = "generateKeysToolStripMenuItem1";
            this.generateKeysToolStripMenuItem1.Size = new System.Drawing.Size(156, 22);
            this.generateKeysToolStripMenuItem1.Text = "&Generate Keys";
            this.generateKeysToolStripMenuItem1.Click += new System.EventHandler(this.generateKeysToolStripMenuItem_Click);
            // 
            // importKeyToolStripMenuItem1
            // 
            this.importKeyToolStripMenuItem1.Name = "importKeyToolStripMenuItem1";
            this.importKeyToolStripMenuItem1.Size = new System.Drawing.Size(156, 22);
            this.importKeyToolStripMenuItem1.Text = "&Import Key...";
            this.importKeyToolStripMenuItem1.Click += new System.EventHandler(this.importKeyToolStripMenuItem_Click);
            // 
            // encodeToolStripMenuItem1
            // 
            this.encodeToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.encodeMessageToolStripMenuItem1});
            this.encodeToolStripMenuItem1.Name = "encodeToolStripMenuItem1";
            this.encodeToolStripMenuItem1.Size = new System.Drawing.Size(54, 20);
            this.encodeToolStripMenuItem1.Text = "E&ncode";
            // 
            // encodeMessageToolStripMenuItem1
            // 
            this.encodeMessageToolStripMenuItem1.Name = "encodeMessageToolStripMenuItem1";
            this.encodeMessageToolStripMenuItem1.Size = new System.Drawing.Size(165, 22);
            this.encodeMessageToolStripMenuItem1.Text = "&Encode Message";
            this.encodeMessageToolStripMenuItem1.Click += new System.EventHandler(this.encodeMessageToolStripMenuItem_Click);
            // 
            // decodeToolStripMenuItem1
            // 
            this.decodeToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.decodeMessageToolStripMenuItem1});
            this.decodeToolStripMenuItem1.Name = "decodeToolStripMenuItem1";
            this.decodeToolStripMenuItem1.Size = new System.Drawing.Size(55, 20);
            this.decodeToolStripMenuItem1.Text = "&Decode";
            // 
            // decodeMessageToolStripMenuItem1
            // 
            this.decodeMessageToolStripMenuItem1.Name = "decodeMessageToolStripMenuItem1";
            this.decodeMessageToolStripMenuItem1.Size = new System.Drawing.Size(166, 22);
            this.decodeMessageToolStripMenuItem1.Text = "&Decode Message";
            this.decodeMessageToolStripMenuItem1.Click += new System.EventHandler(this.decodeMessageToolStripMenuItem_Click);
            // 
            // toolsToolStripMenuItem1
            // 
            this.toolsToolStripMenuItem1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.resetSettingsToolStripMenuItem1,
            this.manageKeysToolStripMenuItem1});
            this.toolsToolStripMenuItem1.Name = "toolsToolStripMenuItem1";
            this.toolsToolStripMenuItem1.Size = new System.Drawing.Size(44, 20);
            this.toolsToolStripMenuItem1.Text = "&Tools";
            // 
            // resetSettingsToolStripMenuItem1
            // 
            this.resetSettingsToolStripMenuItem1.Name = "resetSettingsToolStripMenuItem1";
            this.resetSettingsToolStripMenuItem1.Size = new System.Drawing.Size(155, 22);
            this.resetSettingsToolStripMenuItem1.Text = "&Reset Settings";
            this.resetSettingsToolStripMenuItem1.Click += new System.EventHandler(this.resetSettingsToolStripMenuItem_Click);
            // 
            // manageKeysToolStripMenuItem1
            // 
            this.manageKeysToolStripMenuItem1.Name = "manageKeysToolStripMenuItem1";
            this.manageKeysToolStripMenuItem1.Size = new System.Drawing.Size(155, 22);
            this.manageKeysToolStripMenuItem1.Text = "&Manage Keys";
            this.manageKeysToolStripMenuItem1.Click += new System.EventHandler(this.manageKeysToolStripMenuItem1_Click);
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(35, 20);
            this.fileToolStripMenuItem.Text = "File";
            // 
            // editToolStripMenuItem
            // 
            this.editToolStripMenuItem.Name = "editToolStripMenuItem";
            this.editToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
            this.editToolStripMenuItem.Text = "Edit";
            // 
            // keysToolStripMenuItem
            // 
            this.keysToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.generateKeysToolStripMenuItem,
            this.importKeyToolStripMenuItem,
            this.myKeyToolStripMenuItem});
            this.keysToolStripMenuItem.Name = "keysToolStripMenuItem";
            this.keysToolStripMenuItem.Size = new System.Drawing.Size(42, 20);
            this.keysToolStripMenuItem.Text = "Keys";
            // 
            // generateKeysToolStripMenuItem
            // 
            this.generateKeysToolStripMenuItem.Name = "generateKeysToolStripMenuItem";
            this.generateKeysToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.generateKeysToolStripMenuItem.Text = "Generate Keys";
            this.generateKeysToolStripMenuItem.Click += new System.EventHandler(this.generateKeysToolStripMenuItem_Click);
            // 
            // importKeyToolStripMenuItem
            // 
            this.importKeyToolStripMenuItem.Name = "importKeyToolStripMenuItem";
            this.importKeyToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.importKeyToolStripMenuItem.Text = "Import Key...";
            this.importKeyToolStripMenuItem.Click += new System.EventHandler(this.importKeyToolStripMenuItem_Click);
            // 
            // myKeyToolStripMenuItem
            // 
            this.myKeyToolStripMenuItem.Name = "myKeyToolStripMenuItem";
            this.myKeyToolStripMenuItem.Size = new System.Drawing.Size(156, 22);
            this.myKeyToolStripMenuItem.Text = "My Key";
            this.myKeyToolStripMenuItem.Click += new System.EventHandler(this.myKeyToolStripMenuItem_Click);
            // 
            // encodeToolStripMenuItem
            // 
            this.encodeToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.encodeMessageToolStripMenuItem});
            this.encodeToolStripMenuItem.Name = "encodeToolStripMenuItem";
            this.encodeToolStripMenuItem.Size = new System.Drawing.Size(54, 20);
            this.encodeToolStripMenuItem.Text = "Encode";
            // 
            // encodeMessageToolStripMenuItem
            // 
            this.encodeMessageToolStripMenuItem.Name = "encodeMessageToolStripMenuItem";
            this.encodeMessageToolStripMenuItem.Size = new System.Drawing.Size(165, 22);
            this.encodeMessageToolStripMenuItem.Text = "Encode Message";
            this.encodeMessageToolStripMenuItem.Click += new System.EventHandler(this.encodeMessageToolStripMenuItem_Click);
            // 
            // decodeToolStripMenuItem
            // 
            this.decodeToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.decodeMessageToolStripMenuItem});
            this.decodeToolStripMenuItem.Name = "decodeToolStripMenuItem";
            this.decodeToolStripMenuItem.Size = new System.Drawing.Size(55, 20);
            this.decodeToolStripMenuItem.Text = "Decode";
            // 
            // decodeMessageToolStripMenuItem
            // 
            this.decodeMessageToolStripMenuItem.Name = "decodeMessageToolStripMenuItem";
            this.decodeMessageToolStripMenuItem.Size = new System.Drawing.Size(166, 22);
            this.decodeMessageToolStripMenuItem.Text = "Decode Message";
            this.decodeMessageToolStripMenuItem.Click += new System.EventHandler(this.decodeMessageToolStripMenuItem_Click);
            // 
            // toolsToolStripMenuItem
            // 
            this.toolsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.optionsToolStripMenuItem,
            this.manageKeysToolStripMenuItem,
            this.resetSettingsToolStripMenuItem});
            this.toolsToolStripMenuItem.Name = "toolsToolStripMenuItem";
            this.toolsToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
            this.toolsToolStripMenuItem.Text = "Tools";
            // 
            // optionsToolStripMenuItem
            // 
            this.optionsToolStripMenuItem.Name = "optionsToolStripMenuItem";
            this.optionsToolStripMenuItem.Size = new System.Drawing.Size(155, 22);
            this.optionsToolStripMenuItem.Text = "Options";
            // 
            // manageKeysToolStripMenuItem
            // 
            this.manageKeysToolStripMenuItem.Name = "manageKeysToolStripMenuItem";
            this.manageKeysToolStripMenuItem.Size = new System.Drawing.Size(155, 22);
            this.manageKeysToolStripMenuItem.Text = "Manage Keys";
            // 
            // resetSettingsToolStripMenuItem
            // 
            this.resetSettingsToolStripMenuItem.Name = "resetSettingsToolStripMenuItem";
            this.resetSettingsToolStripMenuItem.Size = new System.Drawing.Size(155, 22);
            this.resetSettingsToolStripMenuItem.Text = "Reset Settings";
            this.resetSettingsToolStripMenuItem.Click += new System.EventHandler(this.resetSettingsToolStripMenuItem_Click);
            // 
            // Text_Box
            // 
            this.Text_Box.Dock = System.Windows.Forms.DockStyle.Fill;
            this.Text_Box.Location = new System.Drawing.Point(3, 3);
            this.Text_Box.Multiline = true;
            this.Text_Box.Name = "Text_Box";
            this.Text_Box.Size = new System.Drawing.Size(468, 165);
            this.Text_Box.TabIndex = 2;
            // 
            // KeyListBox
            // 
            this.KeyListBox.FormattingEnabled = true;
            this.KeyListBox.Location = new System.Drawing.Point(121, 3);
            this.KeyListBox.Name = "KeyListBox";
            this.KeyListBox.Size = new System.Drawing.Size(149, 121);
            this.KeyListBox.TabIndex = 3;
            // 
            // LoadKeyDialog
            // 
            this.LoadKeyDialog.FileName = "Key.pke";
            this.LoadKeyDialog.Filter = "Public Keys|*.pke";
            // 
            // MyKeyDialog
            // 
            this.MyKeyDialog.FileName = "Private Key.kez";
            this.MyKeyDialog.Filter = "Private Keys|*.kez";
            // 
            // Dec_button
            // 
            this.Dec_button.Location = new System.Drawing.Point(3, 89);
            this.Dec_button.Name = "Dec_button";
            this.Dec_button.Size = new System.Drawing.Size(112, 37);
            this.Dec_button.TabIndex = 4;
            this.Dec_button.Text = "Decode";
            this.Dec_button.UseVisualStyleBackColor = true;
            this.Dec_button.Click += new System.EventHandler(this.Dec_button_Click);
            // 
            // Enc_button
            // 
            this.Enc_button.Location = new System.Drawing.Point(3, 46);
            this.Enc_button.Name = "Enc_button";
            this.Enc_button.Size = new System.Drawing.Size(112, 37);
            this.Enc_button.TabIndex = 5;
            this.Enc_button.Text = "Encode";
            this.Enc_button.UseVisualStyleBackColor = true;
            this.Enc_button.Click += new System.EventHandler(this.Enc_button_Click);
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.linkLabel1);
            this.panel1.Controls.Add(this.Proc_Img);
            this.panel1.Controls.Add(this.Genkeys_button);
            this.panel1.Controls.Add(this.KeyListBox);
            this.panel1.Controls.Add(this.Dec_button);
            this.panel1.Controls.Add(this.Enc_button);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(3, 174);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(468, 131);
            this.panel1.TabIndex = 6;
            // 
            // Proc_Img
            // 
            this.Proc_Img.ErrorImage = global::RSA_Crypto.Properties.Resources.hour_glass;
            this.Proc_Img.Image = global::RSA_Crypto.Properties.Resources.hour_glass;
            this.Proc_Img.Location = new System.Drawing.Point(276, 3);
            this.Proc_Img.Name = "Proc_Img";
            this.Proc_Img.Size = new System.Drawing.Size(42, 59);
            this.Proc_Img.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.Proc_Img.TabIndex = 6;
            this.Proc_Img.TabStop = false;
            this.Proc_Img.Visible = false;
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 1;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.Controls.Add(this.panel1, 0, 1);
            this.tableLayoutPanel1.Controls.Add(this.Text_Box, 0, 0);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(0, 24);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 2;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 137F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(474, 308);
            this.tableLayoutPanel1.TabIndex = 7;
            // 
            // Open_File
            // 
            this.Open_File.FileName = "Code";
            this.Open_File.Filter = "Encoded Messages|*.Enc|Plaintext Messages|*.txt|All Files|*.*";
            // 
            // Save_File
            // 
            this.Save_File.DefaultExt = "enc";
            this.Save_File.FileName = "Code";
            this.Save_File.Filter = "Encoded Messages|*.Enc|Plaintext Messages|*.txt|All Files|*.*";
            // 
            // linkLabel1
            // 
            this.linkLabel1.AutoSize = true;
            this.linkLabel1.Location = new System.Drawing.Point(324, 3);
            this.linkLabel1.Name = "linkLabel1";
            this.linkLabel1.Size = new System.Drawing.Size(116, 13);
            this.linkLabel1.TabIndex = 7;
            this.linkLabel1.TabStop = true;
            this.linkLabel1.Text = "www.digitalsquid.co.uk";
            this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(474, 332);
            this.Controls.Add(this.tableLayoutPanel1);
            this.Controls.Add(this.menuStrip1);
            this.Cursor = System.Windows.Forms.Cursors.Default;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MainMenuStrip = this.menuStrip1;
            this.MinimumSize = new System.Drawing.Size(420, 345);
            this.Name = "Form1";
            this.Text = "RSA Crypto";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form1_FormClosing);
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Proc_Img)).EndInit();
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button Genkeys_button;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem keysToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem generateKeysToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem importKeyToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem myKeyToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem encodeToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem decodeToolStripMenuItem;
        private System.Windows.Forms.TextBox Text_Box;
        private System.Windows.Forms.ToolStripMenuItem toolsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem optionsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem encodeMessageToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem decodeMessageToolStripMenuItem;
        private System.Windows.Forms.ListBox KeyListBox;
        private System.Windows.Forms.OpenFileDialog LoadKeyDialog;
        private System.Windows.Forms.ToolStripMenuItem manageKeysToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem resetSettingsToolStripMenuItem;
        private System.Windows.Forms.OpenFileDialog MyKeyDialog;
        private System.Windows.Forms.ToolStripMenuItem editToolStripMenuItem;
        private System.Windows.Forms.Button Dec_button;
        private System.Windows.Forms.Button Enc_button;
        private System.Windows.Forms.ToolStripMenuItem FileToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem editToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem keysToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem myKeyToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem generateKeysToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem importKeyToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem encodeToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem encodeMessageToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem decodeToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem decodeMessageToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem toolsToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem resetSettingsToolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem manageKeysToolStripMenuItem1;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.ToolStripMenuItem newToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem cutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem copyToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem pasteToolStripMenuItem;
        private System.Windows.Forms.PictureBox Proc_Img;
        private System.Windows.Forms.OpenFileDialog Open_File;
        private System.Windows.Forms.SaveFileDialog Save_File;
        private System.Windows.Forms.ToolStripMenuItem selectAllToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.LinkLabel linkLabel1;
    }
}

