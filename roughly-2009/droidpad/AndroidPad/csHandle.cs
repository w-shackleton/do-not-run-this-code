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
using Microsoft.Win32.SafeHandles;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;


namespace PPJoy_dotNET
{
    public class csHandle
    {
        #region DLL Imports & Misc (CreateFile,CloseHandle,DevIoControl,SetupCopyOEMInf)
        [DllImport("Kernel32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern SafeFileHandle CreateFile(
            string fileName,
            [MarshalAs(UnmanagedType.U4)] FileAccess fileAccess,
            [MarshalAs(UnmanagedType.U4)] FileShare fileShare,
            int securityAttributes,
            [MarshalAs(UnmanagedType.U4)] FileMode creationDisposition,
            int flags,
            IntPtr template);
        [DllImport("kernel32.dll", SetLastError = true)]
        public static extern int CloseHandle(SafeFileHandle hObject);

        [DllImport("kernel32.dll", ExactSpelling = true, SetLastError = true, CharSet = CharSet.Auto)]
        public static extern bool DeviceIoControl(
            SafeFileHandle hDevice, int dwIoControlCode,
            IntPtr lpInBuffer, int nInBufferSize,
            IntPtr lpOutBuffer, int nOutBufferSize,
            out int lpBytesReturned, IntPtr lpOverlapped);
        public static uint CTL_CODE(uint DeviceType, uint Function, uint Method, uint Access)
        {
            return ((DeviceType) << 16) | ((Access) << 14) | ((Function) << 2) | (Method);
        }
        /*
        [DllImport("setupapi.dll")]
        public static extern bool SetupCopyOEMInf(

            string SourceInfFileName,
            string OEMSourceMediaLocation,
            OemSourceMediaType OEMSourceMediaType,
            OemCopyStyle CopyStyle,
            string DestinationInfFileName,
            int DestinationInfFileNameSize,
            ref int RequiredSize,
            string DestinationInfFileNameComponent

            );


        /// <summary>
        /// Driver media type
        /// </summary>
        public enum OemSourceMediaType
        {
            SPOST_NONE = 0,
            SPOST_PATH = 1,
            SPOST_URL = 2,
            SPOST_MAX = 3
        }

        /// <summary>
        /// Driver file copy style
        /// </summary>
        public enum OemCopyStyle
        {
            SP_COPY_DELETESOURCE = 0x0000001,   // delete source file on successful copy
            SP_COPY_REPLACEONLY = 0x0000002,   // copy only if target file already present
            SP_COPY_NEWER = 0x0000004,   // copy only if source newer than or same as target
            SP_COPY_NEWER_OR_SAME = SP_COPY_NEWER,
            SP_COPY_NOOVERWRITE = 0x0000008,   // copy only if target doesn't exist
            SP_COPY_NODECOMP = 0x0000010,   // don't decompress source file while copying
            SP_COPY_LANGUAGEAWARE = 0x0000020,   // don't overwrite file of different language
            SP_COPY_SOURCE_ABSOLUTE = 0x0000040,   // SourceFile is a full source path
            SP_COPY_SOURCEPATH_ABSOLUTE = 0x0000080,   // SourcePathRoot is the full path
            SP_COPY_IN_USE_NEEDS_REBOOT = 0x0000100,   // System needs reboot if file in use
            SP_COPY_FORCE_IN_USE = 0x0000200,   // Force target-in-use behavior
            SP_COPY_NOSKIP = 0x0000400,   // Skip is disallowed for this file or section
            SP_FLAG_CABINETCONTINUATION = 0x0000800,   // Used with need media notification
            SP_COPY_FORCE_NOOVERWRITE = 0x0001000,   // like NOOVERWRITE but no callback nofitication
            SP_COPY_FORCE_NEWER = 0x0002000,   // like NEWER but no callback nofitication
            SP_COPY_WARNIFSKIP = 0x0004000,   // system critical file: warn if user tries to skip
            SP_COPY_NOBROWSE = 0x0008000,   // Browsing is disallowed for this file or section
            SP_COPY_NEWER_ONLY = 0x0010000,   // copy only if source file newer than target
            SP_COPY_SOURCE_SIS_MASTER = 0x0020000,   // source is single-instance store master
            SP_COPY_OEMINF_CATALOG_ONLY = 0x0040000,   // (SetupCopyOEMInf only) don't copy INF--just catalog
            SP_COPY_REPLACE_BOOT_FILE = 0x0080000,   // file must be present upon reboot (i.e., it's needed by the loader), this flag implies a reboot
            SP_COPY_NOPRUNE = 0x0100000   // never prune this file
        }*/

        #endregion
    }
}