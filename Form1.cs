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
using System.Net;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using PPJoy_dotNET;
using System.Diagnostics;
using Microsoft.Win32.SafeHandles;
using System.Runtime.InteropServices;
using System.Reflection;
using System.Globalization;

namespace DroidPad
{
    public partial class Form1 : Form
    {
        volatile bool stop;
        volatile bool sending = false;
        object stopLock = new object();
        object sendingLock = new object();
        public static double RADIAN_CONVERT = Math.PI / 180;


        private DPThread dpThread = null;
        
        public enum spcode
        {
            OK = 0,
            IPWF = 1,
            PortWF = 2,
            UpdIntWF = 3,
            ContNumWF = 4,
        }
        bool[] pressedButtons = new bool[] { false, false, false };
        private bool JoystickAllowed = false;
    
        public Form1()
        {
            InitializeComponent();
            label1.Text = "";
            buttonsLabel.Text = "";
            labelX.Text = "";
            labelY.Text = "";
            labelZ.Text = "";
            
            #region Check for Installations
            if (DPsf.checkPPJoy())
            {
                statusPInst.Checked = true;

                string DevName = "\\\\.\\PPJoyIOCTL" + Convert.ToString(Properties.Settings.Default.PPNum);
                SafeFileHandle sfh = csHandle.CreateFile(DevName, FileAccess.Write, FileShare.Write, 0, FileMode.Open, 0, IntPtr.Zero);
                if (!sfh.IsInvalid)
                {
                    statusPConf.Checked = true;
                    JoystickAllowed = true;
                }
            }

            if (DPsf.checkUSBInstall())
            {
                statusUInst.Checked = true;
            }
            #endregion
            IP1.Text = Convert.ToString(Properties.Settings.Default.IP1);
            IP2.Text = Convert.ToString(Properties.Settings.Default.IP2);
            IP3.Text = Convert.ToString(Properties.Settings.Default.IP3);
            IP4.Text = Convert.ToString(Properties.Settings.Default.IP4);
            #region Update Procedure
            if (File.Exists(Environment.CurrentDirectory + @"\DroidPad_update.exe"))
            {
                File.Delete(Environment.CurrentDirectory + @"\DroidPad_update.exe");
            }
            try
            {
                WebClient client = new WebClient();
                client.Headers.Add(HttpRequestHeader.Referer, "http://digitalsquid.co.uk/DROIDPADUPDATE");
                client.DownloadStringCompleted += new DownloadStringCompletedEventHandler(client_DownloadStringCompleted);
                client.DownloadStringAsync(new Uri("http://digitalsquid.co.uk/download/droidpad/versions.txt"));
            }
            catch
            {
                labelX.Text = "Err1: Error checking for updates";
            }
            #endregion
        }

        /// <summary>
        /// Check for update, and download it if necessary
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void client_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            string s, result = "";
            try
            {
                if (!(e.Result.IndexOf("!DP!") == -1 | e.Result.IndexOf("!PD!") == -1))
                {
                    result = e.Result;
                }
                else
                {
                    labelX.Text = "Err2: Error checking for updates";
                }
            }
            catch
            {
                labelX.Text = "Err3: Error checking for updates";
            }
            s = result.Substring(
                result.IndexOf("!DP!") + 5,
                result.IndexOf("!PD!") - 5
                );
            string current = null;
            try
            {
                current = s.Substring(0,s.IndexOf("\n"));
            }
            catch
            {
                labelX.Text = "Err4: Update file empty";
            }
            int[] newver = new int[4];
            try
            {
                newver[0] = Convert.ToInt32(s.Substring(0, s.IndexOf('.')));
                s = s.Substring(s.IndexOf('.') + 1);
                newver[1] = Convert.ToInt32(s.Substring(0, s.IndexOf('.')));
                s = s.Substring(s.IndexOf('.') + 1);
                newver[2] = Convert.ToInt32(s.Substring(0, s.IndexOf('.')));
                s = s.Substring(s.IndexOf('.') + 1);
                newver[3] = Convert.ToInt32(s.Substring(0, s.IndexOf(';')));
                s = s.Substring(s.IndexOf(';') + 1);
            }
            catch
            {
                labelX.Text = "Err5: Malformed update file";
                return;
            }
            bool newer = false;
            if (newver[0] > Assembly.GetExecutingAssembly().GetName().Version.Major) newer = true;
            else if (newver[0] < Assembly.GetExecutingAssembly().GetName().Version.Major) return;
            if (newver[1] > Assembly.GetExecutingAssembly().GetName().Version.Minor) newer = true;
            else if (newver[1] < Assembly.GetExecutingAssembly().GetName().Version.Minor) return;
            if (newver[2] > Assembly.GetExecutingAssembly().GetName().Version.Build) newer = true;
            else if (newver[2] < Assembly.GetExecutingAssembly().GetName().Version.Build) return;
            if (newver[3] > Assembly.GetExecutingAssembly().GetName().Version.Major) newer = true;
            else if (newver[3] < Assembly.GetExecutingAssembly().GetName().Version.Revision) return;

            if (!newer) return;
            if (MessageBox.Show("A newer version of DroidPad is available.\nDo you want to download it?", "Update Available", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                dpThread.Stop();
                new DownloadUpdate(s.Substring(s.IndexOf(';') + 1, s.IndexOf('\n') - s.IndexOf(';') - 1),s.Substring(0, s.IndexOf(';'))).ShowDialog();
            }
        }
        /// <summary>
        /// Save vars, stop dpThread, disable USB mode
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            #region Save IPs
            try { Properties.Settings.Default.IP1 = Convert.ToInt32(IP1.Text); }
            catch { Properties.Settings.Default.IP1 = 0; }
            try { Properties.Settings.Default.IP2 = Convert.ToInt32(IP2.Text); }
            catch { Properties.Settings.Default.IP2 = 0; }
            try { Properties.Settings.Default.IP3 = Convert.ToInt32(IP3.Text); }
            catch { Properties.Settings.Default.IP3 = 0; }
            try { Properties.Settings.Default.IP4 = Convert.ToInt32(IP4.Text); }
            catch { Properties.Settings.Default.IP4 = 0; }
            Properties.Settings.Default.Save();
            #endregion

            if(dpThread != null) dpThread.Stop();

            #region Disable USB
            if (USBMode.Checked)
            {
                new Install("Stopping USB Mode...", "Stopping", @".\files\adb kill-server", Environment.CurrentDirectory).ShowDialog();
                IP1.Enabled = true;
                IP2.Enabled = true;
                IP3.Enabled = true;
                IP4.Enabled = true;
                USBMode.Checked = false;
            }
            #endregion

            Environment.Exit(0);
        }

        /// <summary>
        /// Start button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void StartProg_Click(object sender, EventArgs e)
        {
            switch (startprog())
            {
                case spcode.IPWF:
                    MessageBox.Show("IP in wrong format");
                    break;
            }
        }

        /// <summary>
        /// Actually start thread & return status
        /// </summary>
        /// <returns>Status returned, mostly errors.</returns>
        public spcode startprog()
        {
            string ip;
            if (!USBMode.Checked)
            {
                try
                {
                    ip = Convert.ToString(Convert.ToInt32(IP1.Text)) + "." + Convert.ToString(Convert.ToInt32(IP2.Text)) + "." + Convert.ToString(Convert.ToInt32(IP3.Text)) + "." + Convert.ToString(Convert.ToInt32(IP4.Text));
                }
                catch
                {
                    return spcode.IPWF;
                }
            }
            else
            {
                ip = "localhost";
            }
            dpThread = new DPThread(
                ip,
                Properties.Settings.Default.Port,
                JoystickAllowed,
                Properties.Settings.Default.debug,
                Properties.Settings.Default.PPNum,
                Properties.Settings.Default.UpInt
                );

            //Register events here
            dpThread.StatMessageSend += new DPThread.StatMessageHandler(dpThread_StatMessageSend);
            dpThread.ErrorMessageSend += new DPThread.ErrorMessageHandler(dpThread_ErrorMessageSend);
            dpThread.InitStatSend += new DPThread.InitStatHandler(dpThread_InitStatSend);
            dpThread.Stopped += new DPThread.StopHandler(dpThread_Stopped);

            dpThread.Start();
            StartProg.Enabled = false;
            Stopit.Enabled = true;
            settingsButton.Enabled = false;
            return spcode.OK;
        }

        #region Callbacks

        private delegate void DdpThread_ErrorMessageSend(string message);
        /// <summary>
        /// Callback from thread, error message
        /// </summary>
        /// <param name="message">Error message</param>
        void dpThread_ErrorMessageSend(string message)
        {
            Invoke(new DdpThread_ErrorMessageSend(AndroidError), new object[] { message });
        }

        private delegate void DdpThread_StatMessageSend(string message);
        /// <summary>
        /// Callback from thread, status mesage
        /// </summary>
        /// <param name="message">status message</param>
        void dpThread_StatMessageSend(string message)
        {
            Invoke(new DdpThread_StatMessageSend(AndroidMessage), new object[] { message });
        }
        
        private delegate void DdpThread_Stopped();
        /// <summary>
        /// Callback from thread, saying thread was stopped
        /// </summary>
        void dpThread_Stopped()
        {
            Invoke(new DdpThread_Stopped(AndroidStopped));
        }

        private delegate void DdpThread_InitStatSend(DPThread.DPMode mode);
        /// <summary>
        /// Callback from thread with initial status (mode etc)
        /// </summary>
        /// <param name="mode"></param>
        void dpThread_InitStatSend(DPThread.DPMode mode)
        {
            Invoke(new DdpThread_InitStatSend(AndroidInitStatSend), new object[] { mode });
        }
        
        private void AndroidMessage(string message)
        {
            StatBox.Text = message + "\r\n" + StatBox.Text;
        }
        private void AndroidError(string message)
        {
            MessageBox.Show(message);
        }
        private void AndroidStopped()
        {
            StartProg.Enabled = true;
            Stopit.Enabled = false;
            settingsButton.Enabled = true;
            dpThread = null;
        }
        private void AndroidInitStatSend(DPThread.DPMode mode)
        {

        }

        #endregion

        /// <summary>
        /// Settings button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void settingsButton_Click(object sender, EventArgs e)
        {
            new Settings().ShowDialog();

            #region Check for Installations
            if (DPsf.checkPPJoy())
            {
                statusPInst.Checked = true;

                string DevName = "\\\\.\\PPJoyIOCTL" + Convert.ToString(Properties.Settings.Default.PPNum);
                SafeFileHandle sfh = csHandle.CreateFile(DevName, FileAccess.Write, FileShare.Write, 0, FileMode.Open, 0, IntPtr.Zero);
                if (!sfh.IsInvalid)
                {
                    statusPConf.Checked = true;
                    JoystickAllowed = true;
                }
                else
                {
                    statusPConf.Checked = false;
                    JoystickAllowed = false;
                }
            }
            else
            {
                statusPConf.Checked = false;
                statusPInst.Checked = false;
            }

            if (DPsf.checkUSBInstall())
            {
                statusUInst.Checked = true;
                return;
            }
            statusUInst.Checked = false;
            #endregion
        }

        /// <summary>
        /// Enable and disable USB mode
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void USBMode_CheckedChanged(object sender, EventArgs e)
        {
            if (USBMode.Checked)
            {
                IP1.Enabled = false;
                IP2.Enabled = false;
                IP3.Enabled = false;
                IP4.Enabled = false;
                if (Properties.Settings.Default.AdbDevice == "")
                {
                    new SelectDevice(false).ShowDialog();

                    if (Properties.Settings.Default.AdbDevice != "")
                    {
                        new StartUSB().ShowDialog();
                    }
                    return;
                }
                else
                {
                    new StartUSB().ShowDialog();
                }
            }
            else
            {
                new Install("Stopping USB Mode...", "Stopping", @".\files\adb kill-server", Environment.CurrentDirectory).ShowDialog();
                IP1.Enabled = true;
                IP2.Enabled = true;
                IP3.Enabled = true;
                IP4.Enabled = true;
            }
        }
        private void Stopit_Click(object sender, EventArgs e)
        {
            dpThread.Stop();
        }

        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            About a = new About();
            a.ShowDialog();
        }

        private void websiteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("http://digitalsquid.co.uk/droidpad/");
        }

        private void chooseDeviceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new SelectDevice(!USBMode.Checked).ShowDialog();
        }

        private void manualToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("http://digitalsquid.co.uk/droidpad/docs/");
        }

        private void checkForUpdatesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                WebClient client = new WebClient();
                client.Headers.Add(HttpRequestHeader.Referer, "http://digitalsquid.co.uk/DROIDPADUPDATE");
                client.DownloadStringCompleted += new DownloadStringCompletedEventHandler(client_DownloadStringCompleted);
                client.DownloadStringAsync(new Uri("http://digitalsquid.co.uk/download/droidpad/versions.txt"));
            }
            catch
            {
                labelX.Text = "Err1: Error checking for updates";
            }
        }
    }
}
