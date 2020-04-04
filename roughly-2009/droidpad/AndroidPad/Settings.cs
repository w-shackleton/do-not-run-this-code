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
using System.IO;
using System.Diagnostics;

namespace DroidPad
{
    public partial class Settings : Form
    {
        public Settings()
        {
            /*Drivers to check for:
             * 
             * Android USB Driver
             * Parallel Port Joystick Bus device driver
             * Parallel Port Joystick device driver
             *
             */
            InitializeComponent();
            UpdInt.Text = Convert.ToString(Properties.Settings.Default.UpInt);
            PortNum.Text = Convert.ToString(Properties.Settings.Default.Port);
            ContNum.Text = Convert.ToString(Properties.Settings.Default.PPNum);
            checkBox1.Checked = Properties.Settings.Default.debug;

            if(DPsf.checkUSBInstall())
            {
                usbmode.Text = "USB Mode:\r\nInstalled!";
                InstallADB.Text = "(Re)install";
            }
            if (DPsf.checkPPJoy())
            {
                PPJoymode.Text = "PPJoy Joystick Driver:\r\nInstalled!";
                //InstallPPJoy.Text = "Website";
                UninstallPPJoy.Enabled = true;
                ConfigurePPJoy.Enabled = true;
            }

            if (File.Exists(Environment.CurrentDirectory + @"\testsigndrv.conf")) // test-mode enabled
            {
                pp64drv.Text = "Disable";
            }
            if(IntPtr.Size != 8) // 64-bit
            {
                Size = new Size(420, Size.Height);
                groupBox5.Visible = false;
            }
        }

        private void ok_Click(object sender, EventArgs e)
        {
            bool a = false;
            try
            {
                Properties.Settings.Default.UpInt = Convert.ToInt32(UpdInt.Text);
                Properties.Settings.Default.Port = Convert.ToInt32(PortNum.Text);
                Properties.Settings.Default.PPNum = Convert.ToInt32(ContNum.Text);
                Properties.Settings.Default.debug = checkBox1.Checked;
            }
            catch
            {
                MessageBox.Show("Values not entered properly.");
                a = true;
            }
            if (!a)
            {
                Properties.Settings.Default.Save();
                Close();
            }
        }

        private void cancel_Click(object sender, EventArgs e)
        {
            Close();
        }

        /*private void installPP()
        {
            if(!File.Exists(@"files/PPJoy/Setup.exe"))
            {
                MessageBox.Show("ERROR: File not found (Reinstall?)");
                return;
            }
            Install install = new Install("Installing PPJoy", "Installing...", "files/PPJoy/Setup.exe", Environment.CurrentDirectory);
            install.ShowDialog();
            RegistryKey regK = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Parallel Port Joystick");
            if (regK != null)
            {
                PPJoymode.Text = "PPJoy Joystick Driver:\r\nInstalled!";
                InstallPPJoy.Text = "(Re)install";
                UninstallPPJoy.Enabled = true;
                ConfigurePPJoy.Enabled = true;
            }
            else
            {
                PPJoymode.Text = "PPJoy Joystick Driver:\r\nNot Installed";
                InstallPPJoy.Enabled = true;
                UninstallPPJoy.Enabled = false;
                ConfigurePPJoy.Enabled = false;
            }
        }*/
        private void uninstallPP()
        {
            if (!DPsf.checkPPJoy())
            {
                MessageBox.Show("Error Uninstalling, please uninstall manually.", "Uninstall Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            string Upath = DPsf.getPPJoyUninst();
            if (Upath != null)
            {
                try
                {
                    Process.Start(Upath);
                }
                catch { }
            }
            else
            {
                MessageBox.Show("Error finding Uninstaller, please uninstall manually.", "Uninstall Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            if (DPsf.checkPPJoy())
            {
                PPJoymode.Text = "PPJoy Joystick Driver:\r\nInstalled!";
                InstallPPJoy.Text = "(Re)install";
                UninstallPPJoy.Enabled = true;
                ConfigurePPJoy.Enabled = true;
            }
            else
            {
                PPJoymode.Text = "PPJoy Joystick Driver:\r\nNot Installed";
                InstallPPJoy.Enabled = true;
                UninstallPPJoy.Enabled = false;
                ConfigurePPJoy.Enabled = false;
            }
        }

        private void InstallPPJoy_Click(object sender, EventArgs e)
        {
            Install install = new Install("Installing PPJoy", "Installing...", "files/PPJoySetup.exe", Environment.CurrentDirectory);
            install.ShowDialog();
        }

        private void UninstallPPJoy_Click(object sender, EventArgs e)
        {
            uninstallPP();
        }

        private void ConfigurePPJoy_Click(object sender, EventArgs e)
        {
            MessageBox.Show(
@"Make sure that in the following window there is at least 1 Virtual Joystick.
If there is not, click add, then select 'parallel port:' ->'virtual Joystick'.

Make a note of the number in 'Controller Number'. Click Add, then done.
Allow the 'Found new Hardware' wizard to complete.

In the settings window, type this number in 'controller num'.

For More info, see the manual",
                "Configure Joysticks"
            );
            Process.Start("control.exe", DPsf.getPPJoyCPL());
        }

        private void InstallADB_Click(object sender, EventArgs e)
        {
            /*string p64 = @"files\amd64\";
            string p86 = @"files\x86\";
            if (IntPtr.Size == 8)
            {
                // 64 Bit
                if(File.Exists(p64 + "android_usb.inf") &
                   File.Exists(p64 + "androidusb.sys") &
                   File.Exists(p64 + "androidusba64.cat") &
                   File.Exists(p64 + "WdfCoInstaller01005.dll"))
                {

                }
                else
                {
                    MessageBox.Show("ERROR: Install files not found (try reinstalling?)");
                }
            }
            else
            {
                //32 Bit
                if(File.Exists(p86 + "android_usb.inf") &
                   File.Exists(p86 + "androidusb.sys") &
                   File.Exists(p86 + "androidusb86.cat") &
                   File.Exists(p86 + "WdfCoInstaller01005.dll"))
                {
                    int a = 0;
                    PPJoy_dotNET.csHandle.SetupCopyOEMInf(
                        Environment.CurrentDirectory + @"\files\x86",
                        Environment.CurrentDirectory + @"\files\x86",
                        PPJoy_dotNET.csHandle.OemSourceMediaType.SPOST_PATH,
                        PPJoy_dotNET.csHandle.OemCopyStyle.SP_COPY_NEWER,
                        null,
                        0,
                        ref a,
                        null);
                }
                else
                {
                    MessageBox.Show("ERROR: Install files not found (try reinstalling?)");
                }
            }*/

            if (IntPtr.Size == 8)
                new Install("Installing USB Drivers (64-bit)", "Installing", @"dpinst-64.exe /PATH usb_driver", Environment.CurrentDirectory + @"\files").Show();
            else
                new Install("Installing USB Drivers", "Installing", @"dpinst-32.exe /PATH usb_driver", Environment.CurrentDirectory + @"\files").Show();

            if (DPsf.checkUSBInstall())
            {
                usbmode.Text = "USB Mode:\r\nInstalled!";
                InstallADB.Text = "(Re)install";
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Process.Start("http://digitalsquid.co.uk/droidpad");
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if(((CheckBox)sender).Checked)
            {
                MessageBox.Show("Debug mode eats up your system resources,\nso put the update interval to a SLOWER SPEED\non both your phone AND the computer");
            }
        }

        private void pp64drv_Click(object sender, EventArgs e)
        {
            if (File.Exists(Environment.CurrentDirectory + @"\testsigndrv.conf"))
            {
                DPsf.testSign(false);
                File.Delete(Environment.CurrentDirectory + @"\testsigndrv.conf");
            }
            else
            {
                DPsf.testSign(true);
                File.Create(Environment.CurrentDirectory + @"\testsigndrv.conf");
            }
            MessageBox.Show("Now restart your PC to complete the process.");
        }
    }
}
