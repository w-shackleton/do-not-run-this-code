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

namespace DroidPad
{
    public partial class About : Form
    {
        public About()
        {
            InitializeComponent();
            VersionLabel.Text = "Version: " + Convert.ToString(System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.Major) + "." + Convert.ToString(System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.Minor) + "." + Convert.ToString(System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.Build) + "." + Convert.ToString(System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.Revision);
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://creativecommons.org/licenses/by-nc-nd/2.0/uk/");
        }

        private void button1_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("http://www.geocities.com/deonvdw/Docs/PPJoyMain.htm");
        }

        private void PWebsiteButton_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("http://ppjoy.blogspot.com/");
        }

        private void WebsiteButton_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("http://digitalsquid.co.uk/droidpad");
        }

        private void licenseButton_Click(object sender, EventArgs e)
        {
            new License().ShowDialog();
        }
    }
}
