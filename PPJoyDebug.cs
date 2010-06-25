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

namespace PPJoy_dotNET
{
    public partial class PPJoyDebug : Form
    {
        public PPJoyDebug()
        {
            InitializeComponent();
        }
        public void updateText(string s)
        {
            DebugBox.Text = s + "\r\n" + DebugBox.Text;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Close();
        }
        public void finished()
        {
            button1.Enabled = true;
        }

        private void PPJoyDebug_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
        }
    }
}
