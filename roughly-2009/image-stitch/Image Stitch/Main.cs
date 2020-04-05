using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.IO;

namespace Image_Stitch
{
    public partial class Main : Form
    {
        public string[,] fnms;
        public int ASizeX;
        public int ASizeY;
        public delegate void StatDel(string StatMessage, int StatBar);
        private Thread thr;
        public Main()
        {
            InitializeComponent();
        }
        private void Go_Click(object sender, EventArgs e)
        {
            Go.Enabled = false;
            Status_T.Text = "Starting...";
            ASizeX = Convert.ToInt32(ImgInX2.Text) - Convert.ToInt32(ImgInX1.Text) + 1;
            ASizeY = Convert.ToInt32(ImgInY2.Text) - Convert.ToInt32(ImgInY1.Text) + 1;
            int SX = Convert.ToInt32(SizeX.Text);
            int SY = Convert.ToInt32(SizeY.Text);
            int CT = Convert.ToInt32(CropTop.Text);
            int CL = Convert.ToInt32(CropLeft.Text);
            int CB = Convert.ToInt32(CropBottom.Text);
            int CR = Convert.ToInt32(CropRight.Text);
            fnms = new string[ASizeX, ASizeY];
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    fnms[i, j] = ImgIn1.Text + "\\" +  ImgIn2.Text + Convert.ToString(i + Convert.ToInt32(ImgInX1.Text)) + ImgIn3.Text + Convert.ToString(j + Convert.ToInt32(ImgInY1.Text)) + ImgIn4.Text;
                    //MessageBox.Show(fnms[i, j]);
                }
            }
            thr = new Thread(delegate()
                {
                    StartCalc(fnms,ImgOut.Text,CT,CL,CB,CR,SX,SY,ASizeX,ASizeY);
                });
            thr.Start();
        }
        public void UpStat(string StatMessage, int StatBar)
        {
            Status_B.Value = StatBar;
            Status_T.Text = StatMessage;
        }
        private void FolderGet_Click(object sender, EventArgs e)
        {
            if (FolderGetDialog.ShowDialog() == DialogResult.OK)
            {
                ImgIn1.Text = FolderGetDialog.SelectedPath;
            }
        }
        private void GetFolder_Click(object sender, EventArgs e)
        {
            if (OutImgDialog.ShowDialog() == DialogResult.OK)
            {
                ImgOut.Text = OutImgDialog.FileName;
            }
        }
        public void StartCalc(string[,] fnms, string OutImgnm, int CT, int CL, int CB, int CR, int SizeX, int SizeY, int ASizeX, int ASizeY)
        {
            int FSX = ASizeX * (SizeX - CL - CR);
            int FSY = ASizeY * (SizeY - CT - CB);
            Color col = new Color();
            Bitmap bitmap = new Bitmap(FSX, FSY);
            Bitmap[,] imgs = new Bitmap[ASizeX, ASizeY];
            this.Invoke(new StatDel(this.UpStat), new object[] {"Loading Images...", 0});
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    imgs[i, j] = new Bitmap(fnms[i, j]);
                }
            }
            this.Invoke(new StatDel(this.UpStat), new object[] { "Loaded Images        Starting Stitch", 0 });
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    for(int k = CL; k < SizeX - CR; k++)
                    {
                        for (int l = CT; l < SizeY - CB; l++)
                        {
                            col = imgs[i, j].GetPixel(k, l);
                            bitmap.SetPixel(i * SizeX + k, j * SizeY + l, col);
                        }
                    }
                    this.Invoke(new StatDel(this.UpStat), new object[] { "Stitched " + Convert.ToString((i * ASizeY) + j + 1) + " of " + Convert.ToString(ASizeX * ASizeY), Convert.ToInt32(Convert.ToDouble(((i * ASizeY) + j) * 1000 / (ASizeX * ASizeY))) });
                    //Thread.Sleep(500);
                }
            }
            this.Invoke(new StatDel(this.UpStat), new object[] { "Stitched image.    Saving...", 1000 });
            bitmap.Save(OutImgnm, System.Drawing.Imaging.ImageFormat.Png);
            this.Invoke(new StatDel(this.UpStat), new object[] { "Saved Image.", 1000 });
        }
        private void Main_FormClosed(object sender, FormClosedEventArgs e)
        {
            Environment.Exit(0);
        }

        private void Weblink_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://www.digitalsquid.co.uk");
        }
    }
}
