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
using System.Threading;
using System.Diagnostics;

namespace DroidPad
{
    public partial class StartUSB : Form
    {
        delegate void Del(int id);
        delegate void Del2(bool n);
        delegate void Del3();
        Thread thr;
        int Pid = -1;

        public StartUSB()
        {
            InitializeComponent();
            new Install("Starting USB Mode...", "Starting", @".\files\adb start-server", Environment.CurrentDirectory).ShowDialog();

            thr = new Thread(delegate()
            {
                Thread.Sleep(500);
                Process p = new Process();
                p.StartInfo.FileName = @".\files\adb";
                p.StartInfo.Arguments = "-s " + Properties.Settings.Default.AdbDevice + " status-window";
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardOutput = true;

                p.Start();

                this.Invoke(new Del(ProcessStarted), new object[] { p.Id });
                while (true)
                {
                    string str = p.StandardOutput.ReadLine();
                    if (str == "State: device")
                    {
                        this.Invoke(new Del2(DevicePlugged), new object[] { true });
                        break;
                    }
                    if (str == "State: unknown")
                    {
                        this.Invoke(new Del2(DevicePlugged), new object[] { false });
                    }
                }
                p.Kill();

                Thread.Sleep(1000); //To allow for catchup

                p = new Process();
                p.StartInfo.FileName = @".\files\adb";
                p.StartInfo.Arguments = "-s " + Properties.Settings.Default.AdbDevice + " forward tcp:" + Convert.ToString(Properties.Settings.Default.Port) + " tcp:" + Convert.ToString(Properties.Settings.Default.Port);
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardOutput = true;

                p.Start();
                p.WaitForExit();

                this.Invoke(new Del3(PortConfigured));
            });
            thr.Start();
            
        }

        private void ProcessStarted(int id)
        {
            Pid = id;
        }

        private void DevicePlugged(bool n)
        {
            if (n)
            {
                status1.Text = "Plugged in...";
                status2.Text = "Device " + Properties.Settings.Default.AdbDevice + " has been plugged in";
                button1.Enabled = false;
            }
            else
            {

                status1.Text = "Plug in";
                status2.Text = "Please Plug in the Device " + Properties.Settings.Default.AdbDevice + ".";
            }
        }

        private void PortConfigured()
        {

            status1.Text = "Ready";
            status2.Text = "Device " + Properties.Settings.Default.AdbDevice + " is ready!";
            button1.Enabled = false;
            OKButton.Enabled = true;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            thr.Abort();
            if (Pid != -1)
            {
                try
                {
                    Process.GetProcessById(Pid).Kill();
                }
                catch { }
            }
            Close();
        }

        private void OKButton_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}
