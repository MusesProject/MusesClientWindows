using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace Muses.WindowsClient.Service
{
    public partial class SensorsServiceRuner : ServiceBase
    {
        public SensorsServiceRuner()
        {
            InitializeComponent();
            if (!System.Diagnostics.EventLog.SourceExists("DoDyLogSourse"))
                System.Diagnostics.EventLog.CreateEventSource("DoDyLogSourse",
                                                                      "DoDyLog");

            eventLog1.Source = "DoDyLogSourse";
            // the event log source by which 

            //the application is registered on the computer

            eventLog1.Log = "DoDyLog"; 
        }

        private Process _sensorsService;
        protected override void OnStart(string[] args)
        {
            var path = AppDomain.CurrentDomain.BaseDirectory + "\\Sensors.Service.exe";
            _sensorsService = new Process();
            try
            {
                var start = new ProcessStartInfo
                {
                    FileName = path,
                    WindowStyle = ProcessWindowStyle.Hidden
                };
                _sensorsService = Process.Start(start);
                eventLog1.WriteEntry("Muses sensor service started");
            }
            catch (Exception)
            {
                
                throw(new Exception(path));
            }
        }

        protected override void OnStop()
        {
            foreach (var proc in Process.GetProcessesByName(_sensorsService.ProcessName))
            {
                proc.Kill();
                proc.WaitForExit();
            }
            eventLog1.WriteEntry("Muses sensor service stoped"); 
        }
    }
}
