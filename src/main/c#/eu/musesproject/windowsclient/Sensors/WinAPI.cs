/*
 * #%L
 * museswindowsapp
 * %%
 * Copyright (C) 2013 - 2015 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

using System;
using System.Runtime.InteropServices;

namespace Sensors
{
    internal static class WinAPI
    {
        public const int SPI_GETSCREENSAVETIMEOUT = 0x000E;
        public const int DESKTOP_SWITCHDESKTOP = 256;

        [DllImport("user32.dll")]
        public static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll", SetLastError = true)]
        public static extern uint GetWindowThreadProcessId(
            IntPtr hWnd,
            out uint lpdwProcessId);

        [DllImport("user32.dll", SetLastError = true)]
        public static extern bool SystemParametersInfo(
            int uiAction,
            int uiParam,
            ref int pvParam,
            int fWinIni);

        [DllImport("user32", EntryPoint = "OpenDesktopA",
                     CharSet = CharSet.Ansi,
                     SetLastError = true,
                     ExactSpelling = true)]
        public static extern Int32 OpenDesktop(string lpszDesktop,
                                                Int32 dwFlags,
                                                bool fInherit,
                                                Int32 dwDesiredAccess);

        [DllImport("user32", CharSet = CharSet.Ansi,
                             SetLastError = true,
                             ExactSpelling = true)]
        public static extern Int32 CloseDesktop(Int32 hDesktop);

        [DllImport("user32", CharSet = CharSet.Ansi,
                             SetLastError = true,
                             ExactSpelling = true)]
        public static extern Int32 SwitchDesktop(Int32 hDesktop);

        [DllImport("PowrProf.dll")]
        public static extern uint PowerGetActiveScheme(
            IntPtr UserRootPowerKey,
            ref IntPtr ActivePolicyGuid);

        [DllImport("powrprof.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        public static extern bool GetCurrentPowerPolicies(
            ref object pGlobalPowerPolicy,
             ref object pPowerPolicy);

        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool LogonUser(
            string lpszUsername,
            string lpszDomain,
            string lpszPassword,
            int dwLogonType,
            int dwLogonProvider,
            ref IntPtr phToken);

        [DllImport("kernel32.dll")]
        public static extern bool CloseHandle(IntPtr token);


        public enum LogonType
        {
            Interactive = 2,
            Network = 3,
            Batch = 4,
            Service = 5,
            Unlock = 7,
            NetworkClearText = 8,
            NewCredentials = 9
        }

        public enum LogonProvider
        {
            Default = 0, WinNT35 = 1,
            WinNT40 = 2,
            WinNT50 = 3

        }
    }
}
