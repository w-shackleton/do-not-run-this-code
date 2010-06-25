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
    public partial class Install : Form
    {
        string c, curdir;
        Thread thr;
        delegate void Del1();
        public Install(string Text, string title, string cmd, string CurDir)
        {
            InitializeComponent();
            TextLabel.Text = Text;
            c = cmd;
            curdir = CurDir;
            this.Text = title;
            thr = new Thread(delegate()
            {
                InstallIt(c, curdir);
            }
            );
        }

        private void Install_Shown(object sender, EventArgs e)
        {
            thr.Start();
        }
        private void InstallIt(string c, string curdir)
        {
            Process p;
            string co, ca;
            try
            {
                co = c.Substring(0,c.IndexOf(' '));
                ca = c.Substring(c.IndexOf(' '));
            }
            catch
            {
                co = c;
                ca = "";
            }
            try
            {
                string TempDir = Environment.CurrentDirectory;
                Environment.CurrentDirectory = curdir;
                p = new Process();
                p.StartInfo.FileName = co;
                p.StartInfo.Arguments = ca;
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;

                p.Start();
                p.WaitForExit();
                Environment.CurrentDirectory = TempDir;
            }
            catch
            {
                MessageBox.Show("Could not find Program, did you delete it?");
            }
            this.Invoke(new Del1(this.Close));
        }
    }
}
