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
using Microsoft.Win32.SafeHandles;
using System.IO;
using PPJoy_dotNET;

/* !!!A message to all those who dare to read this code!!!
 * 
 * It's messy. I know. But it works, and IS maintainable (sort of), so meh.
 * 
 * Happy figuring out how this works!
 * 
 * TODO: Documentation!!
 */

namespace DroidPad
{
    public partial class SetupDroidPad : Form
    {
        public static int totalSteps = 10;
        private int cS = 0;
        private int ControllerNum = 0;
        private int currentStep
        {
            get
            {
                return cS;
            }
            set
            {
                cS = value;
                stepText.Text = "Step " + Convert.ToString(value) + " of " + Convert.ToString(totalSteps) + ".";
            }
        }
        private bool closeable = true;
        private bool finished = false;

        Thread cmdRunThread;

        public SetupDroidPad()
        {
            InitializeComponent();
            nextStep();
            ControllerNum = Properties.Settings.Default.PPNum;
        }

        private void SetupDroidPad_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (!finished)
            {
                if (closeable)
                {
                    if (MessageBox.Show("Are you sure you want to cancel the setup?\n(It can be run again from the main menu)", "Cancel Setup?", MessageBoxButtons.YesNo) == DialogResult.No)
                    {
                        e.Cancel = true;
                        Environment.Exit(0);
                    }
                }
                else
                {
                    e.Cancel = true;
                    Environment.Exit(0);
                }
            }
        }

        private void cancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }

        private void nextButton_Click(object sender, EventArgs e)
        {
            nextStep();
        }

        private bool helpVisible
        {
            get
            {
                return helpLabel.Visible;
            }
            set
            {
                helpLabel.Visible = value;
                helpImg1.Visible = value;
                helpImg2.Visible = value;
                helptxt1.Visible = value;
                helptxt2.Visible = value;
            }
        }

        private void nextStep()
        {
            switch (currentStep)
            {
                case 0:
                    subtitleText.Text = "Welcome";
                    mainText.Text = "Welcome to DroidPad setup.\nThis program will help you get DroidPad up and running.";
                    break;
                case 1:
                    subtitleText.Text = "Install Joystick";
                    mainText.Text = "The first step is to install\nthe joystick driver, PPJoy.\n\nClick Install to run the PPJoy setup.\n\nIf you want to skip installing PPJoy,\nclick Skip.";
                    skipButton.Visible = true;
                    nextButton.Font = new Font(nextButton.Font, FontStyle.Bold);
                    nextButton.Text = "Install";
                    
                    break;
                case 2:
                    if (DPsf.checkPPJoy())
                    {
                        if (MessageBox.Show("Are you sure you want to install PPJoy again?\nYou already have it installed", "Are you sure?", MessageBoxButtons.YesNo) == DialogResult.No)
                            return;
                    }

                    if (IntPtr.Size == 8) // 64
                    {
                        if(!File.Exists(Environment.CurrentDirectory + @"\testsigndrv.conf"))
                        {
                            if (MessageBox.Show("You have a 64-bit version of Windows installed,\ndo you want to enable installing of PPJoy's drivers?\n\nThis is required in most cases to use PPJoy.\n\nThis requires a computer restart, and was described at the second screen of the installer.", "Enable driver installation?", MessageBoxButtons.YesNo) == System.Windows.Forms.DialogResult.Yes)
                            {
                                DPsf.testSign(true);
                                MessageBox.Show("Restarting PC.\n\nOnce the PC has restarted, run this setup from the start menu.\n\nOnce the PC has restarted, it will be possible to install PPJoy", "Restarting");
                                File.Create(Environment.CurrentDirectory + @"\testsigndrv.conf");
                                DPsf.restart();
                                Environment.Exit(0);
                            }
                        }
                    }

                    subtitleText.Text = "Joystick installing...";
                    mainText.Text = "Installing PPJoy...\n\nJust go through the installer normally.";
                    skipButton.Visible = false;
                    nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                    nextButton.Enabled = false;
                    closeable = false;
                    cancelButton.Enabled = false;

                    cmdRunThread = new Thread(delegate()
                        {
                            cmdRun(
                                "files/PPJoySetup.exe",
                                "",
                                new FinishCmd(delegate()
                                    {
                                        BringToFront();
                                        closeable = true;
                                        cancelButton.Enabled = true;
                                        nextButton.Enabled = true;
                                        if (!DPsf.checkPPJoy())
                                        {
                                            mainText.Text = "Installing PPJoy...\n\nJust go through the installer normally.\n\nERROR: PPJoy install failed";
                                            nextButton.Text = "Back";
                                            currentStep = 1;
                                            return;
                                        }
                                        else
                                        {
                                            mainText.Text = "Install Complete!";
                                            nextButton.Text = "Next >";
                                        }
                                    }),
                                Environment.CurrentDirectory);
                        });
                    cmdRunThread.Start();
                    break;
                case 3:
                    subtitleText.Text = "Configure Joystick";
                    mainText.Text = "The next step is to configure\nthe joystick driver, PPJoy.\n\nSelect a joystick, or select 'new...'";
                    nextButton.Text = "Next >";
                    nextButton.Enabled = false;
                    closeable = true;
                    cancelButton.Enabled = true;
                    skipButton.Visible = true;
                    controllerDBox.Visible = true;
                    checkControllerButton.Visible = true;

                    RefreshControllers();
                    break;
                case 4:
                    string DevName = "\\\\.\\PPJoyIOCTL" + Convert.ToString(controllerDBox.SelectedIndex);
                    SafeFileHandle sfh = csHandle.CreateFile(DevName, FileAccess.Write, FileShare.Write, 0, FileMode.Open, 0, IntPtr.Zero);
                    if (sfh.IsInvalid)
                    {
                        MessageBox.Show("Controller Doesn't Exist!");
                        RefreshControllers();
                        return;
                    }
                    subtitleText.Text = "Joystick Configured!";
                    Properties.Settings.Default.PPNum = controllerDBox.SelectedIndex;
                    Properties.Settings.Default.Save();
                    mainText.Text = "Joystick Setup complete!\nJoystick " + Convert.ToString(controllerDBox.SelectedIndex) + " is configured.\n\nClick Next to go onto USB setup.";
                    controllerDBox.Visible = false;
                    checkControllerButton.Visible = false;
                    skipButton.Visible = false;
                    new SlideTimer(
                        Size.Width,
                        Size.Height,
                        MinimumSize.Width,
                        MinimumSize.Height,
                        new SlideTimer.updateSize(UpdateSize),
                        new SlideTimer.finishCommand(delegate()
                            {
                                helpVisible = false;
                            }),
                            3);
                    break;
                case 5:
                    checkControllerButton.Visible = false;
                    controllerDBox.Visible = false;

                    skipButton.Text = "Skip >>";
                    skipButton.Visible = true;
                    subtitleText.Text = "Install USB Drivers";
                    mainText.Text = "The next step is to install the\ndrivers for the USB Connection (optional)\nThis driver may not be compatible with all phones.\nIf you are having problems with\nthe phone not being recognised, then reinstall the driver.\n\nThis can take a long time, please be patient.";

                    nextButton.Text = "Install";

                    if (DPsf.checkUSBInstall())
                    {
                        nextButton.Text = "Reinstall";
                        nextButton.Font = new Font(nextButton.Font, FontStyle.Bold);
                        break;
                    }
                    skipButton.Enabled = true;
                    break;
                case 6:
                    skipButton.Visible = false;
                    closeable = false;
                    cancelButton.Enabled = false;
                    nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                    nextButton.Enabled = false;
                    nextButton.Text = "Next >";
                    mainText.Text = "Installing USB Drivers...\n\nPlease wait, it can take a long time...\nIt IS installing, so just be patient.";
                    DriverInstProg.Visible = true;

                    cmdRunThread = new Thread(delegate()
                        {
                            cmdRun(IntPtr.Size == 8 ? "dpinst-64.exe" : "dpinst-32.exe",
                                "/SW /PATH usb_driver",
                                new FinishCmd(delegate()
                                    {
                                        closeable = true;
                                        cancelButton.Enabled = true;
                                        nextButton.Enabled = true;
                                        DriverInstProg.Visible = false;
                                        bool drvInstalled = true;
                                        mainText.Text = "Installed!";
                                        /*bool drvInstalled = false;
                                        System.Management.SelectQuery q2 = new System.Management.SelectQuery("Win32_SystemDriver");
                                        System.Management.ManagementObjectSearcher searcher2 = new System.Management.ManagementObjectSearcher(q2);
                                        foreach (System.Management.ManagementObject ManageObject in searcher2.Get())
                                        {
                                            if (ManageObject["Description"].ToString() == "Android USB Driver")
                                            {
                                                mainText.Text = "Installed!";
                                                drvInstalled = true;
                                                break;
                                            }
                                        }*/
                                        if (!drvInstalled)
                                        {
                                            currentStep--;
                                            mainText.Text = "Failed to install, try again?";
                                            nextButton.Enabled = true;
                                            nextButton.Text = "Retry";
                                            skipButton.Visible = true;
                                            skipButton.Text = "Skip >>";
                                        }
                                    }),
                                Environment.CurrentDirectory + @"\files");
                        });
                    cmdRunThread.Start();

                    break;
                case 7:
                    nextButton.Enabled = false;
                    helpImg1.Image = Properties.Resources.EnableDebug1;
                    helpImg2.Image = Properties.Resources.EnableDebug2;
                    helpImg2.Size = new Size(114, helpImg2.Size.Height);
                    helpImg3.Image = Properties.Resources.EnableDebug3;
                    helpImg3.Visible = true;
                    helptxt1.Text = "1. On your phone, go into\n    settings, then click on\n    applications";
                    helptxt2.Text = "2. Click on development\n3. click 'USB Debugging' so\n    it is checked";

                    helpVisible = true;
                    mainText.Text = "Do NOT plug in the phone yet.\n\nFor USB mode to work, you must enable debug mode on your phone.";

                    new SlideTimer(Size.Width,
                                   Size.Height,
                                   MaximumSize.Width,
                                   MaximumSize.Height,
                                   new SlideTimer.updateSize(UpdateSize),
                                   new SlideTimer.finishCommand(delegate()
                                   {
                                       nextButton.Enabled = true;
                                   }),
                                   3);
                    break;
                case 8:
                    mainText.Text = "Now you have enabled 'USB debugging',\nplug in your phone.\n\nIF a 'Found new Hardware' dialog pops up, then let it\ninstall normally.\n\nClick Next once it has completed.";
                    skipButton.Visible = false;
                    nextButton.Enabled = false;

                    System.Windows.Forms.Timer timer = new System.Windows.Forms.Timer();
                    timer.Interval = 5000;
                    timer.Tick += new EventHandler(foundNewTimer_Tick);
                    timer.Start();
                    break;
                case 9:
                    new SlideTimer(Size.Width,
                                   Size.Height,
                                   MinimumSize.Width,
                                   MinimumSize.Height,
                                   new SlideTimer.updateSize(UpdateSize),
                                   new SlideTimer.finishCommand(delegate()
                                   {
                                       helpVisible = false;
                                   }),
                                   3);
                    mainText.Text = "Now select your phone.\nIt may not appear until the Found New Hardware Wizard has completed.";
                    nextButton.Enabled = false;
                    new SelectDevice(true).ShowDialog();
                    if (Properties.Settings.Default.AdbDevice == "")
                    {
                        mainText.Text = "You have not selected a phone.";
                        skipButton.Visible = true;
                        nextButton.Text = "Retry";
                        nextButton.Enabled = true;
                        currentStep--;
                    }
                    else
                    {
                        mainText.Text = "Phone " + Properties.Settings.Default.AdbDevice + " selected!";
                        nextButton.Enabled = true;
                        skipButton.Visible = false;
                    }
                    break;
                case 10:
                    Properties.Settings.Default.UpInt = 1;
                    Properties.Settings.Default.Port = 3141;
                    Properties.Settings.Default.Save();
                    mainText.Text = "Setup is complete!";
                    subtitleText.Text = "Done";
                    nextButton.Text = "Quit";
                    cancelButton.Enabled = false;
                    stepText.Visible = false;
                    skipButton.Visible = false;
                    File.Create(Environment.CurrentDirectory + @"\setupCompleted.conf");
                    break;
                case 11:
                    finished = true;
                    Close();
                    break;
            }
            currentStep++;
        }

        void foundNewTimer_Tick(object sender, EventArgs e)
        {
            ((System.Windows.Forms.Timer)sender).Stop();
            helptxt1.Text = "1. Accept normal settings\nfor the 'Found\nNew Hardware Wizard'.";
            helptxt2.Text = "2. Let it complete\ninstalling.";
            helpImg1.Image = Properties.Resources.FoundNew1;
            helpImg2.Image = Properties.Resources.FoundNew2;
            helpLabel.Text = "";
            helpImg2.Size = new Size(164, helpImg2.Size.Height);
            helpImg3.Visible = false;
            new SlideTimer(Size.Width,
                           Size.Height,
                           MinimumSize.Width,
                           MaximumSize.Height,
                           new SlideTimer.updateSize(UpdateSize),
                           new SlideTimer.finishCommand(delegate()
                           {
                               nextButton.Enabled = true;
                           }),
                           1);
        }
        private void UpdateSize(Size s)
        {
            Size = s;
        }
        private void skipButton_Click(object sender, EventArgs e)
        {
            switch (currentStep)
            {
                case 2:
                    if (DPsf.checkPPJoy())
                    {
                        if (MessageBox.Show("Do you want to:\n\nYES: Skip install only\nNO: Skip whole PPJoy setup", "What do you want to do?", MessageBoxButtons.YesNo) == DialogResult.Yes)
                        {
                            currentStep = 3;
                            nextStep();
                        }
                        else
                        {
                            currentStep = 5;
                            nextStep();
                        }
                    }
                    else
                    {
                        currentStep = 5;
                        nextStep();
                    }
                    nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                    break;
                case 4:
                    currentStep = 5;
                    nextStep();
                    return;
                case 6:
                    if (MessageBox.Show("Do you want to skip all USB setup,\nor just driver installing?\n\nYES: Skip all\nNO: Only skip install", "Skip USB Setup?", MessageBoxButtons.YesNo) == DialogResult.Yes)
                    {
                        currentStep = 10;
                        nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                        nextStep();
                    }
                    else
                    {
                        skipButton.Visible = false;
                        nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                        nextButton.Enabled = false;
                        nextButton.Text = "Next >";
                        currentStep = 7;
                        nextStep();
                    }
                    break;
                case 7:
                    currentStep = 10;
                    nextStep();
                    break;
                case 9:
                    currentStep = 10;
                    nextStep();
                    break;
                case 11:
                    currentStep = 12;
                    nextButton.Font = new Font(nextButton.Font, FontStyle.Regular);
                    nextStep();
                    break;
            }
        }

        private delegate void FinishCmd();

        private void cmdRun(string cmd, string arg, FinishCmd endCmd, string curdir)
        {
            Process p;
            try
            {
                string TempDir = Environment.CurrentDirectory;
                Environment.CurrentDirectory = curdir;
                p = new Process();
                p.StartInfo.FileName = cmd;
                p.StartInfo.Arguments = arg;
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
            this.Invoke(endCmd);
        }

        private void checkControllerButton_Click(object sender, EventArgs e)
        {
            RefreshControllers();
        }
        private void RefreshControllers()
        {
            controllerDBox.Items.Clear();
            controllerDBox.Items.Add("Select...");
            for (int i = 0; i < 16; i++)
            {
                string DevName = "\\\\.\\PPJoyIOCTL" + Convert.ToString(i + 1);
                SafeFileHandle sfh = csHandle.CreateFile(DevName, FileAccess.Write, FileShare.Write, 0, FileMode.Open, 0, IntPtr.Zero);
                if (!sfh.IsInvalid)
                {
                    controllerDBox.Items.Add("Controller " + Convert.ToString(i + 1));
                }
            }
            controllerDBox.Items.Add("New...");
            controllerDBox.Items.Add("Manual Setup...");
            controllerDBox.SelectedIndex = 0;
        }

        private void webHelp_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://digitalsquid.co.uk/droidpad/docs");
        }

        private void controllerDBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                if (controllerDBox.SelectedIndex == controllerDBox.Items.Count - 2)
                {
                    MessageBox.Show("The program will automatically set up a joystick.\nA few windows will pop up, but just leave it.\nIt will take about 5 seconds.");
                    cmdRunThread = new Thread(delegate()
                        {
                        cmdRun(
                            "files/AutoSetupPP.exe",
                            "",
                            new FinishCmd(delegate()
                                {
                                    helpVisible = true;
                                    helpImg1.Image = Properties.Resources.FoundNew1;
                                    helpImg2.Image = Properties.Resources.FoundNew2;
                                    helptxt1.Text = "When (if) the Found new\nhardware Wizard appears, just\naccept all default settings.";
                                    helptxt2.Text = "    Once it has finished,\n    click 'Refresh', then select\n    one of the controllers.";
                                    new SlideTimer(
                                        Size.Width,
                                        Size.Height,
                                        MinimumSize.Width,
                                        MaximumSize.Height,
                                        new SlideTimer.updateSize(UpdateSize),
                                        new SlideTimer.finishCommand(delegate() { }),
                                        3);
                                }),
                                Environment.CurrentDirectory
                                );

                        }
                    );
                    cmdRunThread.Start();
                    try { Process.Start("control", DPsf.getPPJoyCPL()); }
                    catch { }
                    nextButton.Enabled = false;
                }
                else if (controllerDBox.SelectedIndex == controllerDBox.Items.Count - 1)
                {
                    try { Process.Start("control", DPsf.getPPJoyCPL()); }
                    catch { }
                    helpVisible = true;
                    helpImg1.Image = Properties.Resources.PPConf1;
                    helpImg2.Image = Properties.Resources.PPConf2;
                    helptxt1.Text = @"1. Click Add (if there is
    no existing virtual
    joystick)";
                    helptxt2.Text = @"2. Select Virtual Joysticks
    From the top box
    Click Add";
                    new SlideTimer(
                        Size.Width,
                        Size.Height,
                        MinimumSize.Width,
                        MaximumSize.Height,
                        new SlideTimer.updateSize(UpdateSize),
                        new SlideTimer.finishCommand(delegate() { }),
                        3);
                    nextButton.Enabled = false;
                }
                else if (controllerDBox.SelectedIndex == 0)
                {
                    //NONE
                    nextButton.Enabled = false;
                }
                else
                {
                    //CONTROLLER SELECTED
                    nextButton.Enabled = true;
                }
            }
            catch { }
        }
    }
    public class SlideTimer
    {
        public delegate void updateSize(Size s);
        public delegate void finishCommand();
        System.Windows.Forms.Timer SlideTimerX, SlideTimerY;
        updateSize uSize;
        finishCommand fCmd;
        Size size;
        int[] SlideTimerXArray, SlideTimerYArray;
        int Endx, Endy;
        public SlideTimer(int startx, int starty, int endx, int endy, updateSize uS, finishCommand fC, double speed)
        {
            uSize = uS;
            fCmd = fC;
            Endx = endx;
            Endy = endy;
            size = new Size(startx, starty);
            Size SlideTimerInitSize = new Size(startx, starty);
            Size SlideTimerEndSize = new Size(endx, endy);

            SlideTimerXArray = new int[Convert.ToInt32(Math.Abs(endx - startx) / speed)];
            SlideTimerYArray = new int[Convert.ToInt32(Math.Abs(endy - starty) / speed)];
            for (int i = 0; i < SlideTimerXArray.Length; i++)
            {
                SlideTimerXArray[i] = (int)((0.5 - (0.5 * Math.Cos((double)i / (double)SlideTimerXArray.Length * Math.PI))) * (double)(endx - startx) + startx);
            }
            for (int i = 0; i < SlideTimerYArray.Length; i++)
            {
                SlideTimerYArray[i] = (int)((0.5 - (0.5 * Math.Cos((double)i / (double)SlideTimerYArray.Length * Math.PI))) * (double)(endy - starty) + starty);
            }

            SlideTimerX = new System.Windows.Forms.Timer();
            SlideTimerX.Tick += new EventHandler(SlideTimerX_Tick);
            SlideTimerX.Interval = 1;
            
            SlideTimerY = new System.Windows.Forms.Timer();
            SlideTimerY.Tick += new EventHandler(SlideTimerY_Tick);
            SlideTimerY.Interval = 1;

            bool noGo = false, noGo2 = false;
            if (SlideTimerXArray.Length != 0)
                SlideTimerX.Start();
            else
                noGo = true;
            if (SlideTimerYArray.Length != 0)
                SlideTimerY.Start();
            else
                noGo2 = true;
            if (noGo & noGo2)
                fCmd();
        }

        int xPos = 0, yPos = 0;
        void SlideTimerY_Tick(object sender, EventArgs e)
        {
            size.Height = SlideTimerYArray[yPos];
            uSize(size);
            yPos++;
            if (yPos == SlideTimerYArray.Length)
            {
                SlideTimerY.Stop();
                size.Height = Endy;
                uSize(size);
                if (!SlideTimerX.Enabled)
                    fCmd();
            }
        }
        private void SlideTimerX_Tick(object sender, EventArgs e)
        {
            size.Width = SlideTimerXArray[xPos];
            uSize(size);
            xPos++;
            if (xPos == SlideTimerXArray.Length)
            {
                SlideTimerX.Stop();
                size.Width = Endx;
                uSize(size);
                if (!SlideTimerY.Enabled)
                    fCmd();
            }
        }
    }
}
