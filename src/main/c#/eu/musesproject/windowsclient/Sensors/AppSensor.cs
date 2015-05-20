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
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace Sensors
{
    public class AppSensor : ISensor
    {
        public string Type { get; set; }
        public string appname { get; set; }
        public string appversion { get; set; }
        public List<string> backgroundprocess { get; set; }

        public AppSensor()
        {
            backgroundprocess = new List<string>();
        }
        public void Update()
        {
            var hWnd = WinAPI.GetForegroundWindow();
            uint processId = 0;
            WinAPI.GetWindowThreadProcessId(hWnd, out processId);
            var activeProcess = Process.GetProcessById((int)processId);
            try
            {
                appname = activeProcess.ProcessName;
                appversion = activeProcess.MainModule.FileVersionInfo.FileVersion;
                //Console.WriteLine(AppName + ": " + AppVersion);
            }
            catch (Exception ex)
            {
                //Console.WriteLine("AppSenspor: "+ex.Message);
            }
        }
    }
}
