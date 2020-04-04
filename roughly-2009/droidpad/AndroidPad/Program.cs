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
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;

namespace DroidPad
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main(string[] args)
        {
            DPsf.englishLocale.NegativeSign = "-";
            DPsf.englishLocale.NumberDecimalSeparator = ".";
            DPsf.englishLocale.NumberGroupSizes = new int[] { 0 };
            DPsf.englishLocale.NumberGroupSeparator = ",";

            Environment.CurrentDirectory = Application.StartupPath;
            if (args.Length != 0)
            {
                if (args[0] == "setup")
                {
                    Application.EnableVisualStyles();
                    Application.SetCompatibleTextRenderingDefault(false);
                    Application.Run(new SetupDroidPad());
                }
                else if(args[0] == "autosetup")
                {
                    if (!File.Exists(Environment.CurrentDirectory + @"\setupCompleted.conf"))
                    {
                        Application.EnableVisualStyles();
                        Application.SetCompatibleTextRenderingDefault(false);
                        Application.Run(new SetupDroidPad());
                    }
                    else
                    {
                        if (DPsf.checkOldPPJoy())
                        {
                            string uninst = DPsf.getPPJoyUninst();
                            if(IntPtr.Size == 8)
                            {
                                if(MessageBox.Show("You are strongly advised to perform a fresh install of PPJoy\nwhen upgrading on a 64-bit computer.\nThe old version of PPJoy will now be uninstalled,\nand the setup wizard will be run.\n\nDo you want to do this?", "Upgrading PPJoy", MessageBoxButtons.YesNo) == DialogResult.Yes)
                                {
                                    if (!(uninst == "" | uninst == null))
                                     {
                                        //MessageBox.Show(uninst);
                                        new Install("Uninstalling old version...", "Uninstalling", uninst, Environment.CurrentDirectory).ShowDialog();
                                        
                                        try
                                        {
                                            Process.Start(Environment.CurrentDirectory + @"\DroidPad.exe", "setup").WaitForExit();
                                        }
                                        catch { }
                                        Environment.Exit(0);
                                     }
                                }
                            }
                            MessageBox.Show("Upgrading to new version of PPJoy joystick driver\n\nJust complete the previous version's uninstaller,\nthen complete the new version's installer.");
                            
                            if (!(uninst == "" | uninst == null))
                            {
                                //MessageBox.Show(uninst);
                                new Install("Uninstalling old version...", "Uninstalling", uninst, Environment.CurrentDirectory).ShowDialog();
                            }
                            MessageBox.Show("Running new PPJoy setup");
                            Process.Start(Environment.CurrentDirectory + @"\files\PPJoySetup.exe");
                        }
                    }
                }
                else if (args[0] == "uinstproc")
                {
                    if (DPsf.checkPPJoy())
                    {
                        string Upath = DPsf.getPPJoyUninst();
                        if (Upath != null)
                        {
                            //MessageBox.Show(Upath);
                            try
                            {
                                Process.Start(Upath).WaitForExit();
                            }
                            catch { }
                        }
                    }
                    
                    if (IntPtr.Size == 8)
                    {
                        if(File.Exists(Environment.CurrentDirectory + @"\testsigndrv.conf"))
                        {
                            DPsf.testSign(false);
                            File.Delete(Environment.CurrentDirectory + @"\testsigndrv.conf");
                            MessageBox.Show("You will now need to restart your PC if you installed PPJoy\n to remove the 'test mode' message\n\nThis will stop PPJoy from working, however.", "Restart required.");
                        }
                    }
                }
                else
                {
                    Application.EnableVisualStyles();
                    Application.SetCompatibleTextRenderingDefault(false);
                    Application.Run(new Form1());
                }
            }
            else
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new Form1());
            }
        }
    }
}
