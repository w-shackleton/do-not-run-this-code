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
using System.Windows.Forms;
using System.Globalization;

namespace DroidPad
{
    public class InfoDecoder
    {

        public InfoDecoder()
        {

        }

        /// <summary>
        /// A struct describing a button / slider / axis in droidpad
        /// </summary>
        public struct DPItem
        {
            public enum Type
            {
                Button,
                Axis,
                Slider
            }
            /// <summary>
            /// Type of data
            /// </summary>
            public Type type;
            /// <summary>
            /// Whether button is pressed
            /// </summary>
            public bool pressed;
            /// <summary>
            /// In the case of a slider, the value is stored in axisX.
            /// In the case of an axis, this is the X-axis
            /// </summary>
            public int axisX;
            /// <summary>
            /// Y-axis of type Axis
            /// </summary>
            public int axisY;
        }

        private string[] iters, tmpdata;
        double tmpX, tmpY, tmpZ;

        private DPItem[] DPout;
        /// <summary>
        /// Decode a string returned from DroidPad on the phone. (Handle exception just in case)
        /// </summary>
        /// <param name="input">Input string from DroidPad</param>
        /// <returns>An array of decoded items</returns>
        public DPItem[] Decode(string input)
        {
            int i_beg = input.IndexOf('[') + 1;
            int i_end = input.IndexOf(']', i_beg); // This is to avoid flush-overflowing (multiple instances of command piling up and becoming out of sync
            input = input.Substring(i_beg, i_end - i_beg); // Trim to one output only.
            iters = input.Split(new char[] { ';' });

            DPout = new DPItem[iters.Length];

            #region decode initial
            tmpdata = iters[0].Split(new char[] { ',' });

            tmpX = Convert.ToDouble(tmpdata[0].Substring(1), DPsf.englishLocale);
            tmpY = Convert.ToDouble(tmpdata[1], DPsf.englishLocale);
            tmpZ = Convert.ToDouble(tmpdata[2].Substring(0, tmpdata[2].Length - 1), DPsf.englishLocale);

            DPout[0] = new DPItem();
            DPout[0].type = DPItem.Type.Axis;
            DPout[0].axisX = trimMinMax(
                Convert.ToInt32(Math.Atan2(tmpX, Math.Sqrt((tmpY * tmpY) + (tmpZ * tmpZ))) / Math.PI * 49152, DPsf.englishLocale),
                -16384,
                16384);
            DPout[0].axisY = trimMinMax(
                Convert.ToInt32(Math.Atan2(tmpY, tmpZ) / Math.PI * 49152, DPsf.englishLocale),
                -16384,
                16384);
            #endregion

            #region Decode others

            for (int i = 1; i < DPout.Length; i++)
            {
                DPout[i] = new DPItem();
                if (iters[i].Length == 1) // Is a button
                {
                    DPout[i].type = DPItem.Type.Button;
                    if (iters[i] == "1")
                        DPout[i].pressed = true;
                    else
                        DPout[i].pressed = false;
                }
                else if (iters[i].StartsWith("{A")) // Axis
                {
                    tmpdata = iters[i].Split(new char[] { ',' });
                    DPout[i].type = DPItem.Type.Axis;
                    DPout[i].axisX = (int)Convert.ToDouble(tmpdata[0].Substring(2), DPsf.englishLocale);
                    DPout[i].axisY = (int)Convert.ToDouble(tmpdata[1].Substring(0, tmpdata[1].Length - 1), DPsf.englishLocale);
                }
                else if (iters[i].StartsWith("{S"))
                {
                    DPout[i].type = DPItem.Type.Slider;
                    DPout[i].axisX = (int)Convert.ToDouble(iters[i].Substring(2, iters[i].Length - 2 - 1), DPsf.englishLocale);
                }
            }

            #endregion

            return DPout;
        }

        private static int trimMinMax(int num, int min, int max)
        {
            if (num < min)
                return min;
            if (num > max)
                return max;
            return num;
        }
    }
}
