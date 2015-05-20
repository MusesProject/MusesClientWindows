using System;
using System.Web.Http;
using Sensors;

namespace Sensors.Service.Controllers
{
    public class CONTEXT_SENSOR_FILE_ACCESSController : ApiController
    {
        // GET api/values 
        public FilesAccessSensor Get()
        {
            var sensor = new FilesAccessSensor();
            sensor.Update();
            return sensor;
        }
        public FilesAccessSensor Get(string param)
        {
            var sensor = new FilesAccessSensor();
            if (param.Split(',').Length == 1)
            {
                param += ",";
            }
            sensor.path = String.Join("\\", param.Split(','));
            sensor.Update();
            return sensor;
        }
        
    }
}