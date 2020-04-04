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
using System.Windows.Forms;

// This is currently unused. It smooths out the mouse movement, but doesn't really work.

namespace DroidPad
{
    class SmoothMouse
    {
        private Thread thr;

        public SmoothMouse()
        {

        }

        public void Start()
        {
            stop = false;
            currTime = DateTime.Now;
            thr = new Thread(delegate()
            {
                thrCmd();
            }
            );
            thr.Start();
        }

        private bool stop;
        private Object stopLock = new Object();
        public void Stop()
        {
            lock (stopLock)
            {
                stop = true;
            }
            try
            {
                thr.Join(2500);
                thr.Abort();
            }
            catch { }
        }

        int currX, currY;
        DateTime prevTime, currTime;
        TimeSpan timeS;
        object dataLock = new Object();

        public void SetPos(int x, int y)
        {
            lock (dataLock)
            {
                prevTime = currTime;
                currTime = DateTime.Now;
                timeS = currTime - prevTime;
                currX = x;
                currY = y;
            }
        }

        DateTime startTime;
        TimeSpan stepInt = new TimeSpan(1);

        private void thrCmd()
        {
            // Code yet to be sorted
            while (true)
            {
                lock (stopLock) if(stop == true) return;

                //startTime = DateTime.Now;

                lock(dataLock)
                {
                    //if (stepInt.Milliseconds == 0)
                    //    stepInt = new TimeSpan(0, 0, 0, 0, 1);
                    //int stepFraction = timeS.Milliseconds / stepInt.Milliseconds;

                    try { mouseMoveRel(currX / 2, currY / 2); }
                    catch { }
                }

                Thread.Sleep(20); // Random time interval - cpu catchup
                //stepInt = DateTime.Now - startTime;
            }
        }

        private void mouseMoveRel(int x, int y)
        {
            Cursor.Position = new System.Drawing.Point(x + Cursor.Position.X, y + Cursor.Position.Y);
        }
    }
}
