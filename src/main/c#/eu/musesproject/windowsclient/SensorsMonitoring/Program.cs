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
using System.IO;
using System.Security.Permissions;
using System.Threading;
using Sensors;

namespace SensorsMonitoring
{
    class Program
    {
        static void Main(string[] args)
        {
            var sensor = new FilesAccessSensor { path = @"C:\Program Files" };
            sensor.Update();
            Console.WriteLine("c: "+sensor.cancreate);  
            Console.WriteLine("r: "+sensor.canread);  
            Console.WriteLine("e: "+sensor.canexecute);  
            Console.WriteLine("d: "+sensor.candelete);  
            Console.WriteLine("m: "+sensor.canmodify);  
            //Run();
            //Console.WriteLine("ScreenTimeout: " + sensor.ScreenTimeout);
            //Console.WriteLine("AV-Installed: " + sensor.IsAVInstalled);
            //Console.WriteLine("PassNeeded: " + sensor.PasswordProtected);
            //while (true)
            //{
            //    sensor.Update();
            //    Console.WriteLine("F App: " + sensor.AppName);
            //    Thread.Sleep(2000);
            //}
            //foreach (var s in sensor.UserFileSystemRightses)
            //{
            //    Console.WriteLine(s);                
            //}
            Console.ReadKey();
        }

        [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
        public static void Run()
        {
            string[] args = System.Environment.GetCommandLineArgs();

            // If a directory is not specified, exit program. 
            //if (args.Length != 2)
            //{
            //    // Display the proper way to call the program.
            //    Console.WriteLine("Usage: Watcher.exe (directory)");
            //    return;
            //}

            // Create a new FileSystemWatcher and set its properties.
            FileSystemWatcher watcher = new FileSystemWatcher();
            watcher.Path = "C:\\";
            /* Watch for changes in LastAccess and LastWrite times, and
               the renaming of files or directories. */
            watcher.NotifyFilter = NotifyFilters.LastAccess | NotifyFilters.LastWrite
               | NotifyFilters.FileName | NotifyFilters.DirectoryName;
            // Only watch text files.
            watcher.Filter = "*.*";

            // Add event handlers.
            watcher.Changed += new FileSystemEventHandler(OnChanged);
            watcher.Created += new FileSystemEventHandler(OnChanged);
            watcher.Deleted += new FileSystemEventHandler(OnChanged);
            watcher.Renamed += new RenamedEventHandler(OnRenamed);
            watcher.IncludeSubdirectories = true;
            // Begin watching.
            watcher.EnableRaisingEvents = true;

            // Wait for the user to quit the program.
            Console.WriteLine("Press \'q\' to quit the sample.");
            while (Console.Read() != 'q') ;
        }

        // Define the event handlers. 
        private static void OnChanged(object source, FileSystemEventArgs e)
        {
            // Specify what is done when a file is changed, created, or deleted.
            Console.WriteLine("File: " + e.FullPath + " " + e.ChangeType);
        }

        private static void OnRenamed(object source, RenamedEventArgs e)
        {
            // Specify what is done when a file is renamed.
            Console.WriteLine("File: {0} renamed to {1}", e.OldFullPath, e.FullPath);
        }

    }
}
