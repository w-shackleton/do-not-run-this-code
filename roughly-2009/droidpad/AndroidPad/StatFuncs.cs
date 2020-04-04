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

using Microsoft.Win32;
using System.Diagnostics;
using System.Windows.Forms;
using System.Globalization;

namespace DroidPad
{
    public static class DPsf
    {
        public static NumberFormatInfo englishLocale = new NumberFormatInfo();

        /// <summary>
        /// Checks whether a program is installed (32 & 64-bit)
        /// </summary>
        /// <param name="itemName">Program's regristy name</param>
        /// <returns></returns>
        public static bool checkInstall(string itemName)
        {
            // Checks 32-bit and 64-bit.

            RegistryKey regK = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + itemName);//PPJoy Joystick Driver");
            if (regK != null)
                return true;
            regK = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + itemName);//PPJoy Joystick Driver");
            if (regK != null)
                return true;
            return false;
        }
        /// <summary>
        /// Checks for PPJoy
        /// </summary>
        /// <returns></returns>
        public static bool checkPPJoy()
        {
            return checkInstall("PPJoy Joystick Driver");
        }
        /// <summary>
        /// Checks for the old version of PPJoy
        /// </summary>
        /// <returns></returns>
        public static bool checkOldPPJoy()
        {
            return checkInstall("Parallel Port Joystick");
        }
        public static string getPPJoyUninst()
        {
            RegistryKey regK = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\PPJoy Joystick Driver");//PPJoy Joystick Driver");
            if (regK != null)
                return (string)regK.GetValue("UninstallString");
            // 64-bit
            regK = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\PPJoy Joystick Driver");//PPJoy Joystick Driver");
            if (regK != null)
                return (string)regK.GetValue("UninstallString");
            return null;
        }
        /// <summary>
        /// Get the location of PPJoy's control panel app.
        /// </summary>
        /// <returns></returns>
        public static string getPPJoyCPL()
        {
            string dir = getPPJoyUninst();
            dir = dir.Substring(0, dir.LastIndexOf(@"\"));
            //MessageBox.Show(dir);
            return "\"" + dir + "\\PPortJoy.cpl\"";
        }
        /// <summary>
        /// Checks whether the Nexus One / G1 (htc) drivers are installed.
        /// </summary>
        /// <returns></returns>
        public static bool checkUSBInstall()
        {
            System.Management.ManagementObjectSearcher searcher = new System.Management.ManagementObjectSearcher(new System.Management.SelectQuery("Win32_SystemDriver"));
            foreach (System.Management.ManagementObject ManageObject in searcher.Get())
            {
                if (ManageObject["Description"].ToString() == "Android USB Driver")
                {
                    return true;
                }
                //textBox1.Text += "\r\n" + ManageObject["Name"].ToString() + "\t\t" + ManageObject["Description"].ToString();
            }
            return false;
        }
        /// <summary>
        /// Enables or disables "test signing" in win64 (Vista & 7)
        /// </summary>
        /// <param name="onOff">If true, will enable test signing. Otherwise, will disable it.</param>
        public static void testSign(bool onOff)
        {
            try
            {
                Process p = new Process();
                p.StartInfo.FileName = "bcdedit.exe";
                p.StartInfo.Arguments = "-set TESTSIGNING " + (onOff ? "ON" : "OFF");
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.Start();
                p.WaitForExit();
            }
            catch { }
        }
        /// <summary>
        /// Restarts the computer
        /// </summary>
        public static void restart()
        {
            try
            {
                Process p = new Process();
                p.StartInfo.FileName = "shutdown.exe";
                p.StartInfo.Arguments = "-r";
                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.UseShellExecute = false;
                p.Start();
            }
            catch { }
        }

        public static float TrimMinMax(float num, float min, float max)
        {
            if (num < min)
                return min;
            if (num > max)
                return min;
            return num;
        }
    }
}