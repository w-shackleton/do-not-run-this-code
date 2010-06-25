/* This file is part of DroidPad.
 * 
 * DroidPad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DroidPad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DroidPad.  If not, see <http://www.gnu.org/licenses/>.
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;

namespace DroidPad
{
    public partial class SelectDevice : Form
    {
        private delegate void Del();
        private delegate void Del2(string output);
        private bool closeable = true;
        private bool stopAfter;
        private int currentSelected = -1;

        public SelectDevice(bool StopAfter)
        {
            InitializeComponent();
            stopAfter = StopAfter;
            windowBusy = true;
            PhoneList.Enabled = true;
            new Thread(delegate()
            {
                Process p = new Process();
                p.StartInfo.FileName = @".\files\adb.exe";
                p.StartInfo.Arguments = "start-server";
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                try
                {
                    p.Start();
                    p.WaitForExit();
                }
                catch
                {
                    MessageBox.Show("ERROR 1: file adb.exe missing in files directory.\n\nTry reinstalling the program.");
                    Properties.Settings.Default.AdbDevice = "";
                    Properties.Settings.Default.Save();
                    this.Invoke(new Del(this.Close));
                }
                this.Invoke(new Del(finLoad));
            }).Start();
        }

        private void finLoad()
        {
            StatLabel.Text = "Finding Devices";
            new Thread(delegate()
            {
                Process p = new Process();
                p.StartInfo.FileName = @".\files\adb.exe";
                p.StartInfo.Arguments = "devices";
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardOutput = true;
                try
                {
                    p.Start();
                    p.WaitForExit();
                    this.Invoke(new Del2(finFind), new object[] { p.StandardOutput.ReadToEnd() });
                }
                catch
                {
                    MessageBox.Show("ERROR 2: file adb.exe missing in files directory.\n\nTry reinstalling the program.");
                    Properties.Settings.Default.AdbDevice = "";
                    Properties.Settings.Default.Save();
                    this.Invoke(new Del(this.Close));
                }
            }).Start();
        }

        private void finFind(string output)
        {
            windowBusy = false;
            OKButton.Enabled = false;
            StatLabel.Text = "";
            string op = "";
            try
            {
                int h = @"List of devices attached ".Length;
                int i = output.LastIndexOf(@"

");
                op = output.Substring(h, i - h + 2);
            }
            catch
            {
                MessageBox.Show("ERROR 3: file adb.exe returned incorrect data.");
            }
            try
            {
                if (op == "" || op == "\r\n")
                {
                    PhoneList.Items.Clear();
                    PhoneList.Items.Add("No Phones connected");
                    PhoneList.Enabled = false;
                }
                else
                {
                    if (op.Substring(0, 2) == "\r\n")
                        op = op.Substring(2);
                    int i = 1;
                    PhoneList.Items.Clear();
                    PhoneList.Enabled = true;
                    while (true)
                    {
                        try
                        {
                            int index = op.IndexOf("\r\n");
                            if (index == -1)
                                break;
                            string s = op.Substring(0, op.IndexOf("\t"));
                            PhoneList.Items.Add(new ListViewItem(new string[] { "Device " + Convert.ToString(i), s }));
                            op = op.Substring(index + 2);
                            i++;
                        }
                        catch
                        {
                            PhoneList.Items.Add("Unknown Device");
                        }
                    }
                }
            }
            catch
            {
                MessageBox.Show("ERROR 4: adb.exe returned incorrect data / data couldn't be processed.");
            }
        }

        private void SelectDevice_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = !closeable;
            if (closeable & stopAfter)
            {
                new Install("Stopping...", "Stopping", @".\files\adb kill-server", Environment.CurrentDirectory).ShowDialog();
            }
        }

        private bool windowBusy
        {
            get
            {
                return !closeable;
            }
            set
            {
                if (value)
                {
                    this.Cursor = Cursors.WaitCursor;
                }
                else
                {
                    this.Cursor = Cursors.Default;
                }
                closeable = !value;
                OKButton.Enabled = !value;
                RefreshButton.Enabled = !value;
                PhoneList.Enabled = !value;
            }
        }

        private void RefreshButton_Click(object sender, EventArgs e)
        {
            windowBusy = true;
            StatLabel.Text = "Finding Devices";
            new Thread(delegate()
            {
                Process p = new Process();
                p.StartInfo.FileName = @".\files\adb";
                p.StartInfo.Arguments = "devices";
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardOutput = true;
                try
                {
                    p.Start();
                    p.WaitForExit();
                    this.Invoke(new Del2(finFind), new object[] { p.StandardOutput.ReadToEnd() });
                }
                catch
                {
                    MessageBox.Show("ERROR 2: file adb.exe missing in files directory.\n\nTry reinstalling the program.");
                    Properties.Settings.Default.AdbDevice = "";
                    Properties.Settings.Default.Save();
                    this.Invoke(new Del(this.Close));
                }
            }).Start();
        }

        private void PhoneList_SelectedIndexChanged(object sender, EventArgs e)
        {
            currentSelected = -1;
            OKButton.Enabled = false;
            for (int i = 0; i < PhoneList.Items.Count; i++)
            {
                if (PhoneList.Items[i].Selected == true)
                {
                    currentSelected = i;
                    OKButton.Enabled = true;
                }
            }
        }

        private void OKButton_Click(object sender, EventArgs e)
        {
            if (currentSelected != -1)
            {
                Properties.Settings.Default.AdbDevice = PhoneList.Items[currentSelected].SubItems[1].Text;
                Properties.Settings.Default.Save();

                if (closeable & stopAfter)
                {
                    new Install("Stopping...", "Stopping", @".\files\adb kill-server", Environment.CurrentDirectory).ShowDialog();
                }
                Close();
            }
        }
    }
}
