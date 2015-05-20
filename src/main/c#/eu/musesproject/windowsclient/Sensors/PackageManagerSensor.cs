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
using System.Collections.Generic;
using Microsoft.Win32;

namespace Sensors
{
    public class PackageManagerSensor : ISensor
    {
        public string Type { get; set; }
        public string installedapps { get; set; }

        public void Update()
        {
            installedapps = String.Join(";", GetInstalledApps());
        }

        public List<string> GetInstalledApps()
        {
            const string uninstallKey = @"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall";
            var listInstalled = new List<string>();
            using (var rk = Registry.LocalMachine.OpenSubKey(uninstallKey))
            {
                foreach (var skName in rk.GetSubKeyNames())
                {
                    using (var sk = rk.OpenSubKey(skName))
                    {
                        try
                        {
                            var appName = sk.GetValue("DisplayName").ToString();
                            var appVersion = sk.GetValue("DisplayVersion").ToString();
                            listInstalled.Add(appName+","+appVersion);
                            //lstInstalled.Add(new Application
                            //{
                            //    Name = appName,
                            //    Version = appVersion,
                            //    AppState = Application.ApplicationStatus.Installed
                            //});
                        }
                        catch (Exception) { }
                    }
                }
            }
            return listInstalled;
        }
        public bool IsApplictionInstalled(string appName)
        {
            // search in: LocalMachine_64
            var key = Registry.LocalMachine.OpenSubKey(@"SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall");
            for (var index = 0; index < key.GetSubKeyNames().Length; index++)
            {
                var keyName = key.GetSubKeyNames()[index];
                var subkey = key.OpenSubKey(keyName);
                var displayName = subkey.GetValue("DisplayName") as string;
                if (appName.Equals(displayName, StringComparison.OrdinalIgnoreCase))
                {
                    return true;
                }
            }
            // NOT FOUND
            return false;
        }
    }
}
