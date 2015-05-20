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
using System.IO;
using System.Security.AccessControl;
using System.Linq;
using System.Security.Principal;


namespace Sensors
{
    public class FilesAccessSensor : ISensor
    {
        public string Type { get; set; }
        public string path { get; set; }
        public List<FileSystemRights> userfilesystemrightses { get; set; }
        public bool cancreate { get; set; }
        public bool canread { get; set; }
        public bool canmodify { get; set; }
        public bool candelete { get; set; }
        public bool canexecute { get; set; }
        //Defenition for FileSystemRights
        //https://msdn.microsoft.com/en-us/library/system.security.accesscontrol.filesystemrights(v=vs.110).aspx
        public FilesAccessSensor()
        {
            userfilesystemrightses = new List<FileSystemRights>();
            path = @"C:\";

        }
        public void Update()
        {
            try
            {
                var curentIdentity = WindowsIdentity.GetCurrent();
                var accessControls = File.GetAccessControl(path);
                var accessRules = accessControls.GetAccessRules(true, true, typeof(NTAccount));

                foreach (var rights in from FileSystemAccessRule accessRule in accessRules
                                       let user = accessRule.IdentityReference.Value
                                       from rights in
                                           (from @group in curentIdentity.Groups.Translate(typeof(NTAccount))
                                            where @group.Value == user
                                            select accessRule.FileSystemRights)
                                       select rights)
                {
                    userfilesystemrightses.Add(rights);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("File Access: "+e.Message);
            }
            foreach (var userfilesystemrightse in userfilesystemrightses)
            {
                if (userfilesystemrightse.ToString().IndexOf("Modify", StringComparison.Ordinal) != -1)
                {
                    cancreate = true;
                    canmodify = true;
                    candelete = true;
                    canread = true;
                    canexecute = true;
                }
                if (userfilesystemrightse.ToString().IndexOf("Read", StringComparison.Ordinal) != -1)
                {
                    canread = true;
                }
                if (userfilesystemrightse.ToString().IndexOf("Delete", StringComparison.Ordinal) != -1)
                {
                    candelete = true;
                }
                if (userfilesystemrightse.ToString().IndexOf("Execute", StringComparison.Ordinal) != -1)
                {
                    canexecute = true;
                }
                if (userfilesystemrightse.ToString().IndexOf("Append", StringComparison.Ordinal) != -1)
                {
                    cancreate = true;
                }
            }
        }
    }
}
