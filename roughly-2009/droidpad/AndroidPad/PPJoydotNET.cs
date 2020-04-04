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
using System.Text;
using System.Threading;
using Microsoft.Win32.SafeHandles;
using System.IO;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.ComponentModel;
using DroidPad;

namespace PPJoy_dotNET
{
    public class PPJoyProvider
    {
        #region Global Vars
        private Thread worker;

        private volatile int[] Analog;
        private volatile bool[] Digital;
        private volatile bool updated = false;
        private volatile bool stop = false;

        private object stoplock = new object();
        private object datalock = new object();
        static byte[] code = new byte[4] { 0x43, 0x41, 0x54, 0x53 }; //CATS in hex!?!
        private bool started = false;
        private bool debug = false;
        private bool workerDebugValue;
        private PPJoyDebug debugWindow;
        private delegate void UpdateDel(string s);
        #endregion

        #region Visible
        /// <summary>
        /// Initialise PPJoy
        /// </summary>
        /// <param name="Debug">If true, display a debug window.</param>
        public PPJoyProvider(bool Debug)
        {
            debug = Debug;
        }

        /// <summary>
        /// Start the main asynchronous loop to PPJoy
        /// </summary>
        /// <param name="JoyNum">The joystick number, usually 1</param>
        /// <param name="Update">The update interval, in milliseconds</param>
        public void Start(int JoyNum, int Update)
        {
            if (!started)
            {
                if (debug)
                {
                    debugWindow = new PPJoyDebug();
                    debugWindow.Show();
                }
                worker = new Thread(new ThreadStart(delegate()
                    {
                        Startit(JoyNum, Update, debug);
                    }
                ));
                uDebug("Started");
                worker.Start();
                started = true;

            }
        }
        /// <summary>
        /// Stop the main call loop.
        /// (This is synchronous since it has to complete fully.)
        /// MUST be called before the program finishes.
        /// </summary>
        public void Stop()
        {
            if (started)
            {
                lock (stoplock)
                {
                    stop = true;
                }
                try
                {
                    if(!debug)
                        worker.Join();
                }
                catch { }
                if(debug)
                    debugWindow.button1.Enabled = true;
                uDebug("Stopped");
                started = false;
            }
        }
        /// <summary>
        /// Updates the data sent to the joystick
        /// </summary>
        /// <param name="analog">Array of Analog stick values. Ranges from -16383 to 16383</param>
        /// <param name="digital">Array of Digital button values.</param>
        public void Update(int[] analog, bool[] digital)
        {
            for (int i = 0; i < analog.Length; i++)
            {
                analog[i] += 16384;
            }
            lock (datalock)
            {
                Analog = analog;
                Digital = digital;
                updated = true;
            }
        }
        /// <summary>
        /// Gets a value indicating whether it is started.
        /// </summary>
        public bool isRunning
        {
            get
            {
                return worker.IsAlive;
            }
        }
        #endregion


        #region Invisible - Worker thread
        private void Startit(int JoyNum, int Update, bool d)
        {
            workerDebugValue = d;
            string DevName = "\\\\.\\PPJoyIOCTL" + Convert.ToString(JoyNum);
            SafeFileHandle sfh = null;
            try
            {
                sfh = csHandle.CreateFile(DevName, FileAccess.Write, FileShare.Write, 0, FileMode.Open, 0, IntPtr.Zero);
                uDebug("PPJoy joystick connected to");
            }
            catch
            {
                uDebug("ERROR: Could not connect to PPJoy Joystick. (Err 1)");
                MessageBox.Show("ERROR: Could not connect to PPJoy Joystick.\nPlease check your settings\n(Error 1)");
                return;
            }
            if (sfh == null)
            {
                uDebug("ERROR: Could not connect to PPJoy Joystick. (Err 2)");
                MessageBox.Show("ERROR: Could not connect to PPJoy Joystick.\nPlease check your settings\n(Error 2)");
                return;
            }
            if (sfh.IsInvalid)
            {
                uDebug("ERROR: Could not connect to PPJoy Joystick. (Err 3)");
                MessageBox.Show("ERROR: Could not connect to PPJoy Joystick.\nPlease check your settings\n(Error 3)");
                return;
            }
            loop(Update, ref sfh);
            //uDebug("Finished");
        }

        private int[] aPre = new int[2]; //analog
        private bool[] dPre = new bool[2]; //digital

        private int[] a = new int[8]; //analog mapped
        private bool[] d = new bool[16]; //digital mapped

        private void loop(int Update, ref SafeFileHandle sfh)
        {
            int ctl = (int)csHandle.CTL_CODE(0x22, 0, 0, 0); //34 is the code of FILE_DEVICE_UNKNOWN;
            byte[] sd; //send data
            while (true)
            {
                lock (stoplock)
                {
                    if (stop)
                    {
                        sfh.Close();
                        return;
                    }
                }
                lock (datalock)
                {
                    if (updated)
                    {
                        aPre = Analog;
                        dPre = Digital;
                        updated = false;
                        mapButtons();
                    }
                }
                uDebug("Data recieved");
                #region Data Construction
                int sdLength;
                sdLength = 5 + (a.Length * 4) + 1 + d.Length;
                sd = new byte[sdLength];
                try
                {
                    code.CopyTo(sd, 0);
                    sd[4] = Convert.ToByte(a.Length);
                    for (int i = 0; i < a.Length; i++)
                    {
                        BitConverter.GetBytes(a[i]).CopyTo(sd, 5 + (i * 4));
                    }
                    uDebug("Data: constructed analogue");
                    sd[5 + (a.Length * 4)] = Convert.ToByte(d.Length);
                    for (int i = 0; i < d.Length; i++)
                    {
                        if (d[i])
                        {
                            sd[6 + (a.Length * 4) + i] = 1;
                        }
                    }
                }
                catch
                {
                    uDebug("ERROR: Constructing data failed.");
                    MessageBox.Show("ERROR: PPJoy interface failed.\nSee website for details, or try restarting DroidPad\n(Error 4)");
                }
                #endregion
                uDebug("Data recieved & constructed into bytes");
                IntPtr sdPtr = IntPtr.Zero;
                try
                {
                    sdPtr = Marshal.AllocHGlobal(sdLength); //Make memory space
                    Marshal.Copy(sd, 0, sdPtr, sdLength); //Copy data
                }
                catch
                {
                    uDebug("ERROR: Out of memory / memory copying failed");
                    MessageBox.Show("ERROR: PPJoy interface failed.\nSee website for details, or try restarting DroidPad\n(Error 5)");
                    break;
                }

                byte rdLength = 0;
                IntPtr rdPtr = Marshal.AllocHGlobal(rdLength);
                int bytesReturned = 0;
                uDebug("Data copied into memory");

                try
                {
                    csHandle.DeviceIoControl(sfh, ctl, sdPtr, sdLength, rdPtr, rdLength, out bytesReturned, IntPtr.Zero);
                }
                catch
                {
                    uDebug("ERROR: Failed to send data to virtual joystick");
                    MessageBox.Show("ERROR: PPJoy interface failed.\nSee website for details, or try restarting DroidPad\n(Error 6)");
                    break;
                }

                Thread.Sleep(Update);
            }
            sfh.Close();
        }
        private void uDebug(string s)
        {
            if (workerDebugValue)
            {
                debugWindow.Invoke(new UpdateDel(debugWindow.updateText), new object[] { s });
            }
        }

        private void mapButtons()
        {
            int i;
            for (i = 0; i < a.Length; i++)
                a[i] = 16384; // Reset Values to 0
            for (i = 0; i < d.Length; i++)
                d[i] = false; // Reset Values to 0

            i = 0;

            foreach (int analoguePre in aPre)
            {
                ButtonMap.MapData data = new ButtonMap.MapData(i++, analoguePre);
                if (data.analogue)
                {
                    if (data.num >= 0)
                        a[data.num] = data.data;
                }
                else
                {
                    if (data.d1 >= 0)
                        d[data.d1] = data.dd1;
                    if (data.twoDigital)
                    {
                        if (data.d2 >= 0)
                            d[data.d2] = data.dd2;
                    }
                }
            }
            i = 0;
            foreach (bool digitalPre in dPre)
            {
                ButtonMap.MapData data = new ButtonMap.MapData(i++, digitalPre);
                if (data.analogue)
                {
                    if (data.num >= 0)
                        a[data.num] = data.data;
                }
                else
                {
                    if (data.d1 >= 0)
                        d[data.d1] = data.dd1;
                    if (data.twoDigital)
                    {
                        if (data.d2 >= 0)
                            d[data.d2] = data.dd2;
                    }
                }
            }
        }
        #endregion

    }
}
