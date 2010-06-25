namespace DroidPad
{
    partial class DownloadUpdate
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
            this.dlProgress = new System.Windows.Forms.ProgressBar();
            this.label1 = new System.Windows.Forms.Label();
            this.dlSize = new System.Windows.Forms.Label();
            this.dlPercent = new System.Windows.Forms.Label();
            this.dlName = new System.Windows.Forms.Label();
            this.cancelButton = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // dlProgress
            // 
            this.dlProgress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.dlProgress.Location = new System.Drawing.Point(12, 32);
            this.dlProgress.Name = "dlProgress";
            this.dlProgress.Size = new System.Drawing.Size(261, 29);
            this.dlProgress.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(12, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(167, 20);
            this.label1.TabIndex = 1;
            this.label1.Text = "Downloading update...";
            // 
            // dlSize
            // 
            this.dlSize.AutoSize = true;
            this.dlSize.Location = new System.Drawing.Point(12, 64);
            this.dlSize.Name = "dlSize";
            this.dlSize.Size = new System.Drawing.Size(30, 13);
            this.dlSize.TabIndex = 2;
            this.dlSize.Text = "0 / 0";
            // 
            // dlPercent
            // 
            this.dlPercent.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dlPercent.AutoSize = true;
            this.dlPercent.Location = new System.Drawing.Point(252, 64);
            this.dlPercent.Name = "dlPercent";
            this.dlPercent.Size = new System.Drawing.Size(21, 13);
            this.dlPercent.TabIndex = 3;
            this.dlPercent.Text = "0%";
            // 
            // dlName
            // 
            this.dlName.AutoSize = true;
            this.dlName.Location = new System.Drawing.Point(13, 77);
            this.dlName.Name = "dlName";
            this.dlName.Size = new System.Drawing.Size(69, 13);
            this.dlName.TabIndex = 4;
            this.dlName.Text = "Downloading";
            // 
            // cancelButton
            // 
            this.cancelButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cancelButton.Location = new System.Drawing.Point(212, 98);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(61, 23);
            this.cancelButton.TabIndex = 5;
            this.cancelButton.Text = "Cancel";
            this.cancelButton.UseVisualStyleBackColor = true;
            this.cancelButton.Click += new System.EventHandler(this.cancelButton_Click);
            // 
            // DownloadUpdate
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(285, 133);
            this.ControlBox = false;
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.dlName);
            this.Controls.Add(this.dlPercent);
            this.Controls.Add(this.dlSize);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.dlProgress);
            this.MinimumSize = new System.Drawing.Size(293, 167);
            this.Name = "DownloadUpdate";
            this.Text = "Download Update";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ProgressBar dlProgress;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label dlSize;
        private System.Windows.Forms.Label dlPercent;
        private System.Windows.Forms.Label dlName;
        private System.Windows.Forms.Button cancelButton;
    }
}