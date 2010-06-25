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

using System.Threading;
using System.Net.Sockets;
using System.Diagnostics;
using System.Windows.Forms;
using System;
using System.Text;
using System.Drawing;
using PPJoy_dotNET;

namespace DroidPad
{
    /// <summary>
    /// Main thread for DroidPad, which recieves data and sends data to various outputs
    /// </summary>
    public class DPThread
    {
        private Thread th;
        private bool running;

        private bool sending;
        private object sendingLock = new object();

        private bool stop;
        private object stopLock = new object();

        string ip;
        int port;
        bool joystickAllowed;
        bool debug;

        int ppNum, ppUpd;
        private PPJoyProvider ppProv;

        public enum DPMode
        {
            Mouse,
            Joystick,
            Presentation,
        }

        public delegate void StatMessageHandler(string message);
        /// <summary>
        /// Called when a status message is needed to be added to the status window
        /// </summary>
        public event StatMessageHandler StatMessageSend;
        private void SendStatMessage(string message)
        {
            if (StatMessageSend != null) StatMessageSend(message);
        }

        public delegate void ErrorMessageHandler(string message);
        /// <summary>
        /// Called when there is a fatal error
        /// </summary>
        public event ErrorMessageHandler ErrorMessageSend;
        private void SendErrorMessage(string message, bool kill)
        {
            if (ErrorMessageSend != null) ErrorMessageSend(message);
            if (kill)
            {
                SendStop();
            }
        }

        public delegate void StopHandler();
        /// <summary>
        /// Called when the thread is stopping
        /// </summary>
        public event StopHandler Stopped;
        private void SendStop()
        {
            if(ppProv != null) ppProv.Stop();
            if (Stopped != null) Stopped();
        }

        public delegate void InitStatHandler(DPMode mode);
        /// <summary>
        /// Returns the mode to the main window.
        /// </summary>
        public event InitStatHandler InitStatSend;
        private void SendInitStat(DPMode mode)
        {
            if (InitStatSend != null) InitStatSend(mode);
        }
        /// <summary>
        /// Main execution thread of DroidPAd
        /// </summary>
        /// <param name="ip">IP address / "localhost"</param>
        /// <param name="port">Port number on phone</param>
        /// <param name="joystickAllowed">Whether joystick is allowed</param>
        /// <param name="debug">Debug mode?</param>
        /// <param name="ppNum">PPJoy controller number</param>
        /// <param name="ppUpd">PJoy update interval</param>
        public DPThread(string ip, int port, bool joystickAllowed, bool debug, int ppNum, int ppUpd)
        {
            this.ip = ip;
            this.port = port;
            this.joystickAllowed = joystickAllowed;
            this.debug = debug;
            this.ppNum = ppNum;
            this.ppUpd = ppUpd;
        }
        /// <summary>
        /// Starts the main thread
        /// </summary>
        public void Start()
        {
            th = new Thread(new ThreadStart(delegate()
            {
                t_Start(ip, port, joystickAllowed,debug, ppNum, ppUpd);
            }
            ));
            th.Start();
        }
        /// <summary>
        /// Stops the main thread
        /// </summary>
        public void Stop()
        {
            bool s;
            lock (sendingLock)
            {
                s = sending;
            }
            if (s)
            {
                lock (stopLock)
                {
                    stop = true;
                }
                try
                {
                    th.Join(3500);
                }
                catch { }
                try
                {
                    th.Abort();
                }
                catch { }
            }
            lock (stopLock)
            {
                stop = false;
            }
            lock (sendingLock)
            {
                sending = false;
            }
        }
        /// <summary>
        /// Start main thread
        /// </summary>
        /// <param name="ip">IP address / "localhost"</param>
        /// <param name="port">Port number on phone</param>
        /// <param name="joystickAllowed">Whether joystick is allowed</param>
        /// <param name="debug">Debug mode?</param>
        /// <param name="ppNum">PPJoy controller number</param>
        /// <param name="ppUpd">PJoy update interval</param>
        private void t_Start(string ip, int port, bool joystickAllowed, bool debug, int ppNum, int ppUpd)
        {
            {
                TcpClient tc;
                NetworkStream ns;
                string str = "";
                InfoDecoder infoDecoder = new InfoDecoder();

                if(debug) SendStatMessage("Setting process to high priority");

                Process.GetCurrentProcess().PriorityClass = ProcessPriorityClass.High;

                SendStatMessage("Started, connecting to " + ip + ":" + Convert.ToString(port));

                try { tc = new TcpClient(ip, port); }
                catch { SendErrorMessage("Could not connect.", true); return; }

                SendStatMessage("Connected");

                try { ns = tc.GetStream(); }
                catch { SendErrorMessage("Error while connecting.", true); return; }

                if(debug) SendStatMessage("Stream made");


                while (true)
                {
                    str = readString(ns);
                    if (str != "")
                        break;
                    if(debug) SendStatMessage("Empty packet sent... (why?)");
                }
                string mode = null;
                if(debug) SendStatMessage(str);
                try
                {
                    int ms = str.IndexOf("<MODE>") + 6;
                    int mf = str.IndexOf("</MODE>");
                    mode = str.Substring(ms, mf - ms);
                }
                catch
                {
                    if(debug) SendStatMessage("ERROR: Mode not sent correctly, using default mode.");
                    mode = "1";
                }
                DPMode dpMode = DPMode.Joystick;
                if(mode.StartsWith("mouse", true, null)) dpMode = DPMode.Mouse;
                else if(mode.StartsWith("slide", true, null)) dpMode = DPMode.Presentation;

                SendInitStat(dpMode);

                if ((dpMode == DPMode.Joystick) && !joystickAllowed)
                {
                    byte[] b = new ASCIIEncoding().GetBytes("<STOP>");
                    ns.Write(b, 0, b.Length);
                    ns.Close();
                    tc.Close();
                    MessageBox.Show("To use Joystick mode, please install & configure PPJoy. (See manual)", "Install PPJoy");
                    SendStop();
                    return;
                }
                lock (sendingLock)
                {
                    sending = true;
                }
                string str2;
                if(debug) SendStatMessage("Main loop starting");
                MouseFunc mousef = new MouseFunc();

                InfoDecoder.DPItem[] dpItems;

                bool[] pressedButtons = new bool[]{false, false, false};
                bool[] tmpButtons;
                int tmpButtonsCount;
                int[] tmpAxis; // All created here to save on memory allocation in each loop. (used later down)
                int tmpAxisCount;

                InfoDecoder.DPItem[] oldDpItems = new InfoDecoder.DPItem[0];

                int prevX = Cursor.Position.X, prevY = Cursor.Position.Y;
                //float dX = 0, dY = 0;

                if ((dpMode == DPMode.Joystick) && joystickAllowed)
                {
                    ppProv = new PPJoyProvider(debug);
                    ppProv.Start(ppNum, ppUpd);
                }

                SmoothMouse sM = null;
                if (dpMode == DPMode.Mouse)
                {
                    //sM = new SmoothMouse();
                    //sM.Start();
                }

                while (true)
                {
                    str2 = readString(ns);
                    if (str2 == null)
                    {
                        // Some sort of reason for closing, so disappearing...
                        ns.Close();
                        tc.Close();
                        if(sM != null) sM.Stop();
                        SendStop();
                        return;
                    }
                    if(debug)SendStatMessage("Read string: " + str2);
                    dpItems = infoDecoder.Decode(str2);
                    if(debug) SendStatMessage("Values processed correctly");
                    if (dpMode == DPMode.Mouse)
                    {
                        //dX = DPsf.TrimMinMax((Cursor.Position.X - prevX) / 2, 1, 10);
                        //dY = DPsf.TrimMinMax((Cursor.Position.Y - prevY) / 2, 1, 10);
                        //prevX = Cursor.Position.X;
                        //prevY = Cursor.Position.Y;

                        //sM.SetPos((int)Math.Abs(dX) * dpItems[0].axisX * 10 / 16384,
                        //    (int)Math.Abs(dY) * dpItems[0].axisY * 10 / 16384);
                        //sM.SetPos(dpItems[0].axisX * 10 / 16384,
                        //    dpItems[0].axisY * 10 / 16384);
                        Cursor.Position = new Point(
                            dpItems[0].axisX * 10 / 16384 + Cursor.Position.X,
                            dpItems[0].axisY * 10 / 16384 + Cursor.Position.Y);
                        if(debug) SendStatMessage("Mouse update sent");
                        #region update Clicks
                        try
                        {
                            if (dpItems[1].pressed) // Left
                            {
                                if (pressedButtons[0] == false)
                                {
                                    mousef.sim_click_down_l();
                                    pressedButtons[0] = true;
                                }
                            }
                            else
                            {
                                if (pressedButtons[0] == true)
                                {
                                    mousef.sim_click_up_l();
                                    pressedButtons[0] = false;
                                }
                            }
                        }
                        catch
                        {
                            if(debug) SendStatMessage("Error clicking left button");
                        }
                        try
                        {
                            if (dpItems[2].pressed) // Middle
                            {
                                if (pressedButtons[1] == false)
                                {
                                    mousef.sim_click_down_m();
                                    pressedButtons[1] = true;
                                }
                            }
                            else
                            {
                                if (pressedButtons[1] == true)
                                {
                                    mousef.sim_click_up_m();
                                    pressedButtons[1] = false;
                                }
                            }
                        }
                        catch
                        {
                            if(debug) SendStatMessage("Error clicking middle button");
                        }
                        try
                        {
                            if (dpItems[3].pressed) // Right
                            {
                                if (pressedButtons[2] == false)
                                {
                                    mousef.sim_click_down_r();
                                    pressedButtons[2] = true;
                                }
                            }
                            else
                            {
                                if (pressedButtons[2] == true)
                                {
                                    mousef.sim_click_up_r();
                                    pressedButtons[2] = false;
                                }
                            }
                        }
                        catch
                        {
                            if(debug) SendStatMessage("Error clicking right button");
                        }
                        if(debug) SendStatMessage("Clicks processed");
                        #endregion
                    }
                    else if(dpMode == DPMode.Joystick)
                    {
                        // Count number of 'Axises' & buttons needed
                        tmpAxisCount = 0;
                        tmpButtonsCount = 0;
                        for(int i = 0; i < dpItems.Length; i++)
                        {
                            if(dpItems[i].type == InfoDecoder.DPItem.Type.Axis)
                                tmpAxisCount += 2;
                            else if(dpItems[i].type == InfoDecoder.DPItem.Type.Slider)
                                tmpAxisCount++;
                            else // Button
                                tmpButtonsCount++;
                        }
                        tmpAxis = new int[tmpAxisCount];
                        tmpButtons = new bool[tmpButtonsCount];
                        //Arrange values into data
                        tmpAxisCount = 0;
                        tmpButtonsCount = 0;
                        for(int i = 0; i < dpItems.Length; i++)
                        {
                            if(dpItems[i].type == InfoDecoder.DPItem.Type.Axis)
                            {
                                tmpAxis[tmpAxisCount] = dpItems[i].axisX;
                                tmpAxisCount++;
                                tmpAxis[tmpAxisCount] = dpItems[i].axisY;
                                tmpAxisCount++;
                            }
                            else if(dpItems[i].type == InfoDecoder.DPItem.Type.Slider)
                            {
                                tmpAxis[tmpAxisCount] = dpItems[i].axisX;
                                tmpAxisCount++;
                            }
                            else // Button
                            {
                                tmpButtons[tmpButtonsCount] = dpItems[i].pressed;
                                tmpButtonsCount++;
                            }
                        }

                        ppProv.Update(tmpAxis, tmpButtons);

                        if(debug) SendStatMessage("Values sent to PPJoy handler");
                    }
                    else if (dpMode == DPMode.Presentation)
                    {
                        try
                        {
                            if (dpItems[1].pressed & !oldDpItems[1].pressed)
                                SendKeys.SendWait("{DOWN}");
                            if (dpItems[2].pressed & !oldDpItems[2].pressed)
                                SendKeys.SendWait("{UP}");
                            if (dpItems[3].pressed & !oldDpItems[3].pressed)
                                SendKeys.SendWait("{F5}");
                            if (dpItems[4].pressed & !oldDpItems[4].pressed)
                                SendKeys.SendWait("{ESC}");
                                    
                            if (dpItems[5].pressed ^ oldDpItems[5].pressed) // XOR Because toggle button
                                SendKeys.SendWait("W");
                            if (dpItems[6].pressed ^ oldDpItems[6].pressed) // XOR Because toggle button
                                SendKeys.SendWait("B");
                            
                            if (dpItems[7].pressed & !oldDpItems[7].pressed)
                                SendKeys.SendWait("{HOME}");
                            if (dpItems[8].pressed & !oldDpItems[8].pressed)
                                SendKeys.SendWait("{END}");
                        }
                        catch { }
                        oldDpItems = dpItems;
                    }
                    bool s;
                    lock (stopLock)
                    {
                        s = stop;
                    }
                    if (s)
                    {
                        if(debug) SendStatMessage("Stopping...");
                        byte[] b = new ASCIIEncoding().GetBytes("<STOP>");
                        ns.Write(b, 0, b.Length);
                        ns.Flush();
                        ns.Close();
                        tc.Close();
                        if (sM != null) sM.Stop();
                        SendStop();
                        return;
                    }
                }
            }
        }

        private string readString(NetworkStream ns)
        {
            byte[] m = new byte[2048];
            int bytesread;
            try
            {
                bytesread = ns.Read(m, 0, 2048);
            }
            catch
            {
                SendErrorMessage("Socket error.", true);
                return null;
            }
            if (bytesread == 0)
            {
                SendStatMessage("Disconnected.");
                return null;
            }
            return new ASCIIEncoding().GetString(m, 0, bytesread);
        }

        public static class StatFunc
        {
            public static double ClipBorders(double a, int top, int bottom)
            {
                if (a < bottom)
                {
                    a = bottom;
                }
                if (a > top)
                {
                    a = top;
                }
                return a;
            }
        }
    }
}