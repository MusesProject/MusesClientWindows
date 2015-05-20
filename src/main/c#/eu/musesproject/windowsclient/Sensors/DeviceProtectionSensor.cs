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
using System.ComponentModel;
using System.DirectoryServices.AccountManagement;
using System.Runtime.InteropServices;
using System.Text;
using System.Management;
using System.DirectoryServices;

namespace Sensors
{
    public class DeviceProtectionSensor : ISensor
    {
        public string Type { get; set; }
        public bool ispasswordprotected { get; set; }
        public bool istrustedantivirusinstalled { get; set; }
        public int screentimeoutinseconds { get; set; }

        public void Update()
        {
            istrustedantivirusinstalled = IsAntivirusInstalled();
            screentimeoutinseconds = GetScreenTimeout();
            ispasswordprotected = IsPasswordProtected();
        }

        private bool IsPasswordProtected()
        {
            var phToken = new IntPtr();

            var loggedIn = WinAPI.LogonUser(Environment.UserName,
                null,
                "",
                (int)WinAPI.LogonType.Interactive,
                (int)WinAPI.LogonProvider.Default,
                ref phToken);

            var error = Marshal.GetLastWin32Error();

            if (phToken != IntPtr.Zero)
                WinAPI.CloseHandle(phToken);

            // 1327 = empty password
            return !loggedIn && error != 1327;
        }

        private int GetScreenTimeout()
        {
            var activeGuidPtr = IntPtr.Zero;
            var timeout = IntPtr.Zero;

            try
            {
                var res = WinAPI.PowerGetActiveScheme(IntPtr.Zero, ref activeGuidPtr);

                if (res != 0)
                    throw new Win32Exception();

                //const uint index = 1;
                //var videoSettingGuid = Guid.Empty;
                //var bufferSize = Convert.ToUInt32(Marshal.SizeOf(typeof(Guid)));
                //uint size = 4;
                //var type = 0;

                //PowerEnumerate(IntPtr.Zero, activeGuidPtr, ref GUID_VIDEO_SUBGROUP, 18, index, ref videoSettingGuid,
                //    ref bufferSize);
                //PowerReadACValue(IntPtr.Zero, activeGuidPtr, IntPtr.Zero,
                //    ref videoSettingGuid, ref type, ref timeout, ref size);

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            return timeout.ToInt32();
        }

        private bool IsAntivirusInstalled()
        {
            var wmipathstr = @"\\" + Environment.MachineName + @"\root\SecurityCenter";
            try
            {
                var searcher = new ManagementObjectSearcher(wmipathstr, "SELECT * FROM AntivirusProduct");
                ManagementObjectCollection instances = searcher.Get();
                return instances.Count > 0;
            }

            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

            return false;
        }

        public static Guid GetCurrentPowerScheme()
        {
            IntPtr pCurrentSchemeGuid = IntPtr.Zero;

            WinAPI.PowerGetActiveScheme(IntPtr.Zero, ref pCurrentSchemeGuid);

            var currentSchemeGuid = (Guid)Marshal.PtrToStructure(pCurrentSchemeGuid, typeof(Guid));

            return currentSchemeGuid;
        }

    }
}
