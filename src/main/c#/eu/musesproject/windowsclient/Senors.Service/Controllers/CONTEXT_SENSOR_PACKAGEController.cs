using System.Web.Http;
using Sensors;

namespace Sensors.Service.Controllers
{
    public class CONTEXT_SENSOR_PACKAGEController : ApiController
    {
        // GET api/values 
        public PackageManagerSensor Get()
        {
            var sensor = new PackageManagerSensor();
            sensor.Update();
            return sensor;
        }
    }
}