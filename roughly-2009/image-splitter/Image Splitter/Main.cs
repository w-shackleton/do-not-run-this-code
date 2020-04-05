using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace Image_Splitter
{
    public partial class Main : Form
    {
        public Main()
        {
            InitializeComponent();
        }
        public delegate void StatDel(string StatMessage, int StatBar);
        private Thread thr;
        private void Go_Click(object sender, EventArgs e)
        {
            Go.Enabled = false;
            Status_T.Text = "Starting...";
            int SX = Convert.ToInt32(SizeX.Text);
            int SY = Convert.ToInt32(SizeY.Text);
            int CB = Convert.ToInt32(CropBottom.Text);
            int CR = Convert.ToInt32(CropRight.Text);
            Bitmap source = new Bitmap(ImgIn1.Text);
            int ASizeX = source.Width / SX + 1;
            int ASizeY = source.Height / SY + 1;
            MessageBox.Show(Convert.ToString(ASizeX));
            MessageBox.Show(Convert.ToString(ASizeY));
            string[,] fnms;
            fnms = new string[ASizeX, ASizeY];
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    fnms[i, j] = ImgOut.Text + "\\" + ImgName.Text + "-" + Convert.ToString(i) + "-" +  Convert.ToString(j) + ".jpg";
                }
            }
            thr = new Thread(delegate()
                {
                    StartCalc(fnms,source,CB,CR,SX,SY,ASizeX,ASizeY);
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
            if (InImgDialog.ShowDialog() == DialogResult.OK)
            {
                ImgIn1.Text = InImgDialog.FileName;
            }
        }
        private void GetFolder_Click(object sender, EventArgs e)
        {
            if (FolderGetDialog.ShowDialog() == DialogResult.OK)
            {
                ImgOut.Text = FolderGetDialog.SelectedPath;
            }
        }
        public void StartCalc(string[,] fnms, Bitmap source, int CB, int CR, int SizeX, int SizeY, int ASizeX, int ASizeY)
        {
            Bitmap[,] outputs = new Bitmap[ASizeX, ASizeY];
            Color col = new Color();
            int TSizeX = source.Width;
            int TSizeY = source.Height;
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    outputs[i, j] = new Bitmap(SizeX + CR, SizeY + CB);
                }
            }
            this.Invoke(new StatDel(this.UpStat), new object[] { "Loaded        Starting Split", 0 });
            for (int i = 0; i < ASizeX; i++)
            {
                for (int j = 0; j < ASizeY; j++)
                {
                    for (int k = 0; k < SizeX + CR; k++)
                    {
                        for (int l = 0; l < SizeY + CB; l++)
                        {
                            if (i * SizeX + k < TSizeX)
                            {
                                if (j * SizeY + l < TSizeY)
                                {
                                    col = source.GetPixel(i * SizeX + k, j * SizeY + l);
                                    outputs[i, j].SetPixel(k, l, col);
                                }
                                else
                                {
                                    outputs[i, j].SetPixel(k, l, Color.White);
                                }
                            }
                            else
                            {
                                outputs[i, j].SetPixel(k, l, Color.White);
                            }
                        }
                    }
                    outputs[i, j].Save(fnms[i, j], System.Drawing.Imaging.ImageFormat.Jpeg);
                    this.Invoke(new StatDel(this.UpStat), new object[] { "Splitted " + Convert.ToString((i * ASizeY) + j + 1) + " of " + Convert.ToString(ASizeX * ASizeY), Convert.ToInt32(Convert.ToDouble(((i * ASizeY) + j) * 1000 / (ASizeX * ASizeY))) });
                    //Thread.Sleep(500);
                }
            }
            this.Invoke(new StatDel(this.UpStat), new object[] { "Finished", 0});
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
