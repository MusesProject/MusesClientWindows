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

namespace Sensors
{
    public class EmailSensor:ISensor
    {
        public string Type { get; set; }
        public string subject { get; set; }
        public string receivers { get; set; }
        public string cc { get; set; }
        public string bcc { get; set; }
        public string attachments { get; set; }

        public void Update()
        {
            // fake test info
            subject = "test";
            receivers = "r_1@a.com; r_2@b.com";
            cc = "cc_1@a.com; cc_2@b.com";
            bcc = "bcc_1@a.com; bcc_2@b.com";
            attachments = "c:\\tmp\\test.txt; d:\\t.pdf";
        }
    }
}
