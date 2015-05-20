using System.Web.Http;
using Sensors;

namespace Sensors.Service.Controllers
{
    public class CONTEXT_SENSOR_DEVICE_PROTECTIONController : ApiController
    {
        // GET api/values 
        public DeviceProtectionSensor Get()
        {
            var sensor = new DeviceProtectionSensor();
            sensor.Update();
            return sensor;
        }
    }
}