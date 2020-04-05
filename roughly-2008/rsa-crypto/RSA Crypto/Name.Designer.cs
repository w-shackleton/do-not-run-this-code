namespace RSA_Crypto
{
    partial class Name
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
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.NameForm = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // textBox1
            // 
            this.textBox1.Dock = System.Windows.Forms.DockStyle.Left;
            this.textBox1.Location = new System.Drawing.Point(0, 0);
            this.textBox1.Name = "textBox1";
            this.textBox1.Size = new System.Drawing.Size(152, 20);
            this.textBox1.TabIndex = 0;
            // 
            // NameForm
            // 
            this.NameForm.Dock = System.Windows.Forms.DockStyle.Right;
            this.NameForm.Location = new System.Drawing.Point(158, 0);
            this.NameForm.Name = "NameForm";
            this.NameForm.Size = new System.Drawing.Size(34, 20);
            this.NameForm.TabIndex = 1;
            this.NameForm.Text = "OK";
            this.NameForm.UseVisualStyleBackColor = true;
            this.NameForm.Click += new System.EventHandler(this.button1_Click);
            // 
            // Name
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(192, 20);
            this.ControlBox = false;
            this.Controls.Add(this.NameForm);
            this.Controls.Add(this.textBox1);
            this.MaximumSize = new System.Drawing.Size(200, 54);
            this.MinimumSize = new System.Drawing.Size(200, 54);
            this.Name = "Name";
            this.Text = "Enter A Name for this Key";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox textBox1;
        private System.Windows.Forms.Button NameForm;
    }
}