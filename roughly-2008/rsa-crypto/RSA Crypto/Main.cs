using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Security.Cryptography;
using System.Threading;
using System.IO;

namespace RSA_Crypto
{
    public partial class Form1 : Form
    {
        #region vars
        public delegate void EnableAndSaveIt(string p, string pnp);
        public delegate void EnableIt();
        public delegate void SaveString(string a);
        public string AppLoc;
        public Thread encthr;
        public Thread keyThread;
        public ManageKeys mk = new ManageKeys();
        #endregion
        public Form1()
        {
            InitializeComponent();
            AppLoc = Application.ExecutablePath;
            AppLoc = AppLoc.Substring(0, AppLoc.LastIndexOf(Convert.ToChar("\\")));
            //MessageBox.Show(AppLoc);
            Directory.CreateDirectory(AppLoc + "\\Keys");
            UpdateKeys();
        }
        public void GenKeys()
        {
            RSACryptoServiceProvider RSAcsp = new RSACryptoServiceProvider(2048);
            string pnp = "<BitStrength>2048</BitStrength>" + RSAcsp.ToXmlString(true);
            string p = "<BitStrength>2048</BitStrength>" + RSAcsp.ToXmlString(false);
            this.Invoke(new EnableAndSaveIt(this.EnableAndSave), new object[] { p, pnp });
        }
        public void Enable()
        {
            Proc_Img.Visible = false;
            Enabled = true;
        }
        public void EnableAndSave(string p, string pnp)
        {
            SaveFileDialog p_save = new SaveFileDialog();
            SaveFileDialog pnp_save = new SaveFileDialog();
            p_save.FileName = "Public Key.pke";
            p_save.Filter = "Public Keys|*.pke";
            pnp_save.FileName = "Private Key.kez";
            pnp_save.Filter = "Private Keys|*.kez";
            StreamWriter sw;
            StreamWriter sw2;
            if (p_save.ShowDialog() == DialogResult.OK)
            {
                if (pnp_save.ShowDialog() == DialogResult.OK)
                {
                    sw = new StreamWriter(p_save.FileName);
                    sw2 = new StreamWriter(pnp_save.FileName);
                    sw.Write(p);
                    sw2.Write(pnp);
                    sw.Close();
                    sw2.Close();
                    if (MessageBox.Show("Do you wish to set this as your private Key?", "?", MessageBoxButtons.YesNo) == DialogResult.Yes)
                    {

                        File.Copy(pnp_save.FileName, AppLoc + "\\keys\\myKey.kez", true);
                        Properties.Settings.Default.MyKey = "myKey.kez";
                    }
                }
            }
            else
            {
                MessageBox.Show("Not Saved Keys");
            }

            Proc_Img.Visible = false;
            Enabled = true;
        }
        private void Genkeys_button_Click(object sender, EventArgs e)
        {
            keyThread = new Thread(delegate()
                {
                    GenKeys();
                }
            );
            Enabled = false;
            Proc_Img.Visible = true;
            keyThread.Start();
        }
        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {

            Properties.Settings.Default.Save();
            Environment.Exit(0);
        }
        public void Encrypt(string text, string Key)
        {
            string FullKey = Key.Substring(31);
            //MessageBox.Show(FullKey);
            RSACryptoServiceProvider rsa = new RSACryptoServiceProvider(2048);
            rsa.FromXmlString(FullKey);
            byte[] byteData = Encoding.UTF32.GetBytes(text);
            int maxLength = 214;
            int dataLength = byteData.Length;
            int iterations = dataLength / maxLength;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= iterations; i++)
            {
                byte[] tempBytes = new byte[(dataLength - maxLength * i > maxLength) ? maxLength : dataLength - maxLength * i];
                Buffer.BlockCopy(byteData, maxLength * i, tempBytes, 0, tempBytes.Length);
				
                byte[] EncbyteData = rsa.Encrypt(tempBytes, false);
                sb.Append(Convert.ToBase64String(EncbyteData));
            }
            this.Invoke(new SaveString(this.savestring), new object[] { sb.ToString() });
            this.Invoke(new EnableIt(this.Enable));
        }
        private void generateKeysToolStripMenuItem_Click(object sender, EventArgs e)
        {
            keyThread = new Thread(delegate()
            {
                GenKeys();
            }
            );
            Enabled = false;
            Proc_Img.Visible = true;
            keyThread.Start();
        }
        public void savestring(string a)
        {
            Text_Box.Text = a;

        }
        private void encodeMessageToolStripMenuItem_Click(object sender, EventArgs e)
        {
            StartEncode();
        }
        public void StartEncode()
        {
            if (KeyListBox.SelectedIndex == -1)
            {
                MessageBox.Show("No Key Selected");
                return;
            }
            int k = KeyListBox.SelectedIndex;
            int c1 = Properties.Settings.Default.CollectedKeys[k].IndexOf(",");
            int c2 = Properties.Settings.Default.CollectedKeys[k].IndexOf(",", c1 + 1);
            string fp = AppLoc + "\\keys\\" + Properties.Settings.Default.CollectedKeys[k].Substring(c1 + 1, c2 - c1 - 1);
            //MessageBox.Show(fp);
            StreamReader sr = new StreamReader(fp);
            string Key = sr.ReadToEnd();
            sr.Close();

            encthr = new Thread(delegate()
            {
                Encrypt(Text_Box.Text, Key);
            }
            );
            Enabled = false;
            Proc_Img.Visible = true;
            encthr.Start();
        }
        public void StartDecode()
        {
            if (Properties.Settings.Default.MyKey == "")
            {
                MessageBox.Show("No Key.");
                return;
            }
            string fp = AppLoc + "\\keys\\" + Properties.Settings.Default.MyKey;
            //MessageBox.Show(fp);
            StreamReader sr = new StreamReader(fp);
            string Key = sr.ReadToEnd();
            sr.Close();

            encthr = new Thread(delegate()
            {
                Decrypt(Text_Box.Text, Key);
            }
            );
            Enabled = false;
            Proc_Img.Visible = true;
            encthr.Start();
        }
        private void importKeyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Name nmfrm = new Name();
            nmfrm.ShowDialog();
            if (LoadKeyDialog.ShowDialog() == DialogResult.OK)
            {
                System.IO.File.Copy(LoadKeyDialog.FileName, AppLoc + "\\Keys\\" + Convert.ToString(Properties.Settings.Default.NumOfKeys) + ".pke", true);
                string str = Convert.ToString(Properties.Settings.Default.NumOfKeys) + "," + Convert.ToString(Properties.Settings.Default.NumOfKeys) + ".pke," + Properties.Settings.Default.Temp;
                Properties.Settings.Default.CollectedKeys.Add(str);
                Properties.Settings.Default.NumOfKeys++;
            }
            UpdateKeys();
        }
        public void UpdateKeys()
        {
            KeyListBox.Items.Clear();
            for (int i = 0; i < Properties.Settings.Default.CollectedKeys.Count; i++)
            {
                try
                {
                    KeyListBox.Items.Add(Properties.Settings.Default.CollectedKeys[i].Substring(Properties.Settings.Default.CollectedKeys[i].LastIndexOf(Convert.ToChar(",")) + 1));
                }
                catch { }
            }
        }
        private void resetSettingsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Are you sure you want to reset all settings and saved Keys??", "!", MessageBoxButtons.OKCancel) == DialogResult.OK)
            {
                Properties.Settings.Default.Reset();
                UpdateKeys();
            }
        }
        private void myKeyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (MyKeyDialog.ShowDialog() == DialogResult.OK)
            {
                File.Copy(MyKeyDialog.FileName, AppLoc + "\\keys\\myKey.kez",true);
                Properties.Settings.Default.MyKey = "myKey.kez";
            }
        }
        private void decodeMessageToolStripMenuItem_Click(object sender, EventArgs e)
        {
            StartDecode();
        }
        public void Decrypt(string text, string Key)
        {
            string FullKey = Key.Substring(31);
            //MessageBox.Show(FullKey);
            //MessageBox.Show(FullKey);
            RSACryptoServiceProvider rsa = new RSACryptoServiceProvider(2048);
            rsa.FromXmlString(FullKey);
            int base64BlockSize = (256 % 3 != 0) ? ((256 / 3) * 4) + 4 : (256 / 3) * 4;
            int iterations = text.Length / base64BlockSize;
            //ArrayList al = new ArrayList();
            string ptext = "";
            int l = 0;
            byte[] fullbytes = new byte[0];
            for (int i = 0; i < iterations; i++)
            {
                byte[] encBytes = Convert.FromBase64String(text.Substring(base64BlockSize * i, base64BlockSize));
                byte[] bytes = rsa.Decrypt(encBytes, false);
                //ptext += Encoding.UTF32.GetString(bytes);
                //al.Add(bytes);
                Array.Resize(ref fullbytes, fullbytes.Length + bytes.Length);
                for (int k = 0; k < bytes.Length; k++)
                {
                    fullbytes[l] = bytes[k];
                    l++;
                }
            }
            
            ptext = Encoding.UTF32.GetString(fullbytes);
            //string ptext = Encoding.UTF32.GetString(al.ToArray(Type.GetType("System.Byte")) as byte[]);
            
            
            this.Invoke(new SaveString(this.savestring), new object[] { ptext });

            this.Invoke(new EnableIt(this.Enable));
        }
        private void Enc_button_Click(object sender, EventArgs e)
        {
            StartEncode();
        }
        private void Dec_button_Click(object sender, EventArgs e)
        {
            StartDecode();
        }
        private void manageKeysToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            mk.sc = Properties.Settings.Default.CollectedKeys;
            mk.ShowDialog();
        }
        private void newToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Text_Box.Text = "";
        }
        private void cutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Clipboard.SetDataObject(Text_Box.SelectedText);
            Text_Box.SelectedText = "";
        }
        private void copyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Clipboard.SetDataObject(Text_Box.SelectedText);
        }
        private void pasteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            IDataObject ido = Clipboard.GetDataObject();
            Text_Box.SelectedText = (String)ido.GetData(DataFormats.Text);
        }
        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (Open_File.ShowDialog() == DialogResult.OK)
            {
                StreamReader sr = new StreamReader(Open_File.FileName);
                Text_Box.Text = sr.ReadToEnd();
                sr.Close();
            }
        }
        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (Save_File.ShowDialog() == DialogResult.OK)
            {
                StreamWriter sw = new StreamWriter(Save_File.FileName);
                sw.Write(Text_Box.Text);
                sw.Close();
            }
        }
        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Text_Box.SelectionStart = 0;
            Text_Box.SelectionLength = Text_Box.TextLength;
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://www.digitalsquid.co.uk");
        }
    }
}
