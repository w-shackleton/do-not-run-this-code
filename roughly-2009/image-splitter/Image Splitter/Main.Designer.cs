namespace Image_Splitter
{
    partial class Main
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Main));
            this.CropBottom = new System.Windows.Forms.TextBox();
            this.CropRight = new System.Windows.Forms.TextBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.label23 = new System.Windows.Forms.Label();
            this.label22 = new System.Windows.Forms.Label();
            this.GetFolder = new System.Windows.Forms.Button();
            this.ImgOut = new System.Windows.Forms.TextBox();
            this.ImgIn1 = new System.Windows.Forms.TextBox();
            this.label19 = new System.Windows.Forms.Label();
            this.Status_B = new System.Windows.Forms.ProgressBar();
            this.SizeY = new System.Windows.Forms.TextBox();
            this.FolderGetDialog = new System.Windows.Forms.FolderBrowserDialog();
            this.Status_T = new System.Windows.Forms.Label();
            this.Go = new System.Windows.Forms.Button();
            this.SizeX = new System.Windows.Forms.TextBox();
            this.label27 = new System.Windows.Forms.Label();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.label26 = new System.Windows.Forms.Label();
            this.Weblink = new System.Windows.Forms.LinkLabel();
            this.FolderGet = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.InImgDialog = new System.Windows.Forms.OpenFileDialog();
            this.label2 = new System.Windows.Forms.Label();
            this.ImgName = new System.Windows.Forms.TextBox();
            this.groupBox2.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.SuspendLayout();
            // 
            // CropBottom
            // 
            this.CropBottom.Location = new System.Drawing.Point(8, 32);
            this.CropBottom.Name = "CropBottom";
            this.CropBottom.Size = new System.Drawing.Size(58, 20);
            this.CropBottom.TabIndex = 7;
            this.CropBottom.Text = "0";
            // 
            // CropRight
            // 
            this.CropRight.Location = new System.Drawing.Point(72, 32);
            this.CropRight.Name = "CropRight";
            this.CropRight.Size = new System.Drawing.Size(58, 20);
            this.CropRight.TabIndex = 5;
            this.CropRight.Text = "0";
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.CropBottom);
            this.groupBox2.Controls.Add(this.CropRight);
            this.groupBox2.Controls.Add(this.label23);
            this.groupBox2.Controls.Add(this.label22);
            this.groupBox2.Location = new System.Drawing.Point(4, 58);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(142, 67);
            this.groupBox2.TabIndex = 40;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Overlap";
            // 
            // label23
            // 
            this.label23.AutoSize = true;
            this.label23.Location = new System.Drawing.Point(26, 16);
            this.label23.Name = "label23";
            this.label23.Size = new System.Drawing.Size(40, 13);
            this.label23.TabIndex = 3;
            this.label23.Text = "Bottom";
            // 
            // label22
            // 
            this.label22.AutoSize = true;
            this.label22.Location = new System.Drawing.Point(69, 16);
            this.label22.Name = "label22";
            this.label22.Size = new System.Drawing.Size(32, 13);
            this.label22.TabIndex = 2;
            this.label22.Text = "Right";
            // 
            // GetFolder
            // 
            this.GetFolder.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.GetFolder.Location = new System.Drawing.Point(517, 32);
            this.GetFolder.Name = "GetFolder";
            this.GetFolder.Size = new System.Drawing.Size(32, 20);
            this.GetFolder.TabIndex = 39;
            this.GetFolder.Text = "...";
            this.GetFolder.UseVisualStyleBackColor = true;
            this.GetFolder.Click += new System.EventHandler(this.GetFolder_Click);
            // 
            // ImgOut
            // 
            this.ImgOut.Location = new System.Drawing.Point(113, 32);
            this.ImgOut.Name = "ImgOut";
            this.ImgOut.Size = new System.Drawing.Size(227, 20);
            this.ImgOut.TabIndex = 38;
            // 
            // ImgIn1
            // 
            this.ImgIn1.Location = new System.Drawing.Point(81, 6);
            this.ImgIn1.Name = "ImgIn1";
            this.ImgIn1.Size = new System.Drawing.Size(430, 20);
            this.ImgIn1.TabIndex = 0;
            // 
            // label19
            // 
            this.label19.AutoSize = true;
            this.label19.Location = new System.Drawing.Point(4, 35);
            this.label19.Name = "label19";
            this.label19.Size = new System.Drawing.Size(103, 13);
            this.label19.TabIndex = 37;
            this.label19.Text = "Output Image Folder";
            // 
            // Status_B
            // 
            this.Status_B.Location = new System.Drawing.Point(200, 155);
            this.Status_B.Maximum = 1000;
            this.Status_B.Name = "Status_B";
            this.Status_B.Size = new System.Drawing.Size(336, 30);
            this.Status_B.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
            this.Status_B.TabIndex = 43;
            // 
            // SizeY
            // 
            this.SizeY.Location = new System.Drawing.Point(73, 32);
            this.SizeY.Name = "SizeY";
            this.SizeY.Size = new System.Drawing.Size(58, 20);
            this.SizeY.TabIndex = 6;
            this.SizeY.Text = "100";
            // 
            // FolderGetDialog
            // 
            this.FolderGetDialog.Description = "Find the Folder...";
            this.FolderGetDialog.ShowNewFolderButton = false;
            // 
            // Status_T
            // 
            this.Status_T.AutoSize = true;
            this.Status_T.Location = new System.Drawing.Point(373, 136);
            this.Status_T.Name = "Status_T";
            this.Status_T.Size = new System.Drawing.Size(0, 13);
            this.Status_T.TabIndex = 44;
            // 
            // Go
            // 
            this.Go.Location = new System.Drawing.Point(376, 77);
            this.Go.Name = "Go";
            this.Go.Size = new System.Drawing.Size(81, 27);
            this.Go.TabIndex = 42;
            this.Go.Text = "Split";
            this.Go.UseVisualStyleBackColor = true;
            this.Go.Click += new System.EventHandler(this.Go_Click);
            // 
            // SizeX
            // 
            this.SizeX.Location = new System.Drawing.Point(9, 32);
            this.SizeX.Name = "SizeX";
            this.SizeX.Size = new System.Drawing.Size(58, 20);
            this.SizeX.TabIndex = 4;
            this.SizeX.Text = "100";
            // 
            // label27
            // 
            this.label27.AutoSize = true;
            this.label27.Location = new System.Drawing.Point(6, 16);
            this.label27.Name = "label27";
            this.label27.Size = new System.Drawing.Size(14, 13);
            this.label27.TabIndex = 0;
            this.label27.Text = "X";
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.SizeY);
            this.groupBox3.Controls.Add(this.SizeX);
            this.groupBox3.Controls.Add(this.label26);
            this.groupBox3.Controls.Add(this.label27);
            this.groupBox3.Location = new System.Drawing.Point(200, 61);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(167, 88);
            this.groupBox3.TabIndex = 41;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Output Image Sizes";
            // 
            // label26
            // 
            this.label26.AutoSize = true;
            this.label26.Location = new System.Drawing.Point(70, 16);
            this.label26.Name = "label26";
            this.label26.Size = new System.Drawing.Size(14, 13);
            this.label26.TabIndex = 1;
            this.label26.Text = "Y";
            // 
            // Weblink
            // 
            this.Weblink.AutoSize = true;
            this.Weblink.Location = new System.Drawing.Point(373, 61);
            this.Weblink.Name = "Weblink";
            this.Weblink.Size = new System.Drawing.Size(116, 13);
            this.Weblink.TabIndex = 45;
            this.Weblink.TabStop = true;
            this.Weblink.Text = "www.digitalsquid.co.uk";
            this.Weblink.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.Weblink_LinkClicked);
            // 
            // FolderGet
            // 
            this.FolderGet.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.FolderGet.Location = new System.Drawing.Point(517, 5);
            this.FolderGet.Name = "FolderGet";
            this.FolderGet.Size = new System.Drawing.Size(32, 20);
            this.FolderGet.TabIndex = 35;
            this.FolderGet.Text = "...";
            this.FolderGet.UseVisualStyleBackColor = true;
            this.FolderGet.Click += new System.EventHandler(this.FolderGet_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(63, 13);
            this.label1.TabIndex = 46;
            this.label1.Text = "Input Image";
            // 
            // InImgDialog
            // 
            this.InImgDialog.FileName = "Image.jpg";
            this.InImgDialog.Filter = "JPEG Files|*.jpg|PNG Files|*.png|BMP Files|*.bmp|All Files|*.*";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(346, 35);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(33, 13);
            this.label2.TabIndex = 47;
            this.label2.Text = "name";
            // 
            // ImgName
            // 
            this.ImgName.Location = new System.Drawing.Point(385, 32);
            this.ImgName.Name = "ImgName";
            this.ImgName.Size = new System.Drawing.Size(126, 20);
            this.ImgName.TabIndex = 48;
            // 
            // Main
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(567, 197);
            this.Controls.Add(this.ImgName);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.ImgIn1);
            this.Controls.Add(this.FolderGet);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.GetFolder);
            this.Controls.Add(this.ImgOut);
            this.Controls.Add(this.label19);
            this.Controls.Add(this.Status_B);
            this.Controls.Add(this.Status_T);
            this.Controls.Add(this.Go);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.Weblink);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Main";
            this.Text = "Image Splitter";
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox CropBottom;
        private System.Windows.Forms.TextBox CropRight;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.Label label23;
        private System.Windows.Forms.Label label22;
        private System.Windows.Forms.Button GetFolder;
        private System.Windows.Forms.TextBox ImgOut;
        private System.Windows.Forms.TextBox ImgIn1;
        private System.Windows.Forms.Label label19;
        private System.Windows.Forms.ProgressBar Status_B;
        private System.Windows.Forms.TextBox SizeY;
        private System.Windows.Forms.FolderBrowserDialog FolderGetDialog;
        private System.Windows.Forms.Label Status_T;
        private System.Windows.Forms.Button Go;
        private System.Windows.Forms.TextBox SizeX;
        private System.Windows.Forms.Label label27;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.Label label26;
        private System.Windows.Forms.LinkLabel Weblink;
        private System.Windows.Forms.Button FolderGet;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.OpenFileDialog InImgDialog;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox ImgName;
    }
}

