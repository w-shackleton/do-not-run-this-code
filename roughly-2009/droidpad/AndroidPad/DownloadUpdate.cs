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

namespace DroidPad
{
    public partial class DownloadUpdate : Form
    {
        WebClient client;
        public DownloadUpdate(string dlFile, string version)
        {
            InitializeComponent();
            dlName.Text = "Downloading DroidPad " + version;
            client = new WebClient();
            dlProgress.Style = ProgressBarStyle.Marquee;
            client.Headers.Add(HttpRequestHeader.Referer, "http://digitalsquid.co.uk/DROIDPADUPDATE");
            client.DownloadFileCompleted += new AsyncCompletedEventHandler(client_DownloadFileCompleted);
            client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(client_DownloadProgressChanged);
            client.DownloadFileAsync(new Uri(dlFile), Environment.CurrentDirectory + @"\DroidPad_Update.exe");
        }

        void client_DownloadProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            dlProgress.Style = ProgressBarStyle.Blocks;
            dlProgress.Value = e.ProgressPercentage;
            dlPercent.Text = Convert.ToString(e.ProgressPercentage) + "%";
            dlSize.Text = Convert.ToString(e.BytesReceived) + "B / " + Convert.ToString(e.TotalBytesToReceive) + "B";
        }

        void client_DownloadFileCompleted(object sender, AsyncCompletedEventArgs e)
        {
            if (e.Cancelled == false)
            {
                System.Diagnostics.Process.Start(Environment.CurrentDirectory + @"\DroidPad_Update.exe", "/DIR=\"" + Environment.CurrentDirectory + "\"");
                Application.Exit();
            }
        }

        private void cancelButton_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Do you want to cancel?", "Cancel?", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                client.CancelAsync();
                Close();
            }
        }
    }
}
