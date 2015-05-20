using System.Web.Http;
using Sensors;

namespace Sensors.Service.Controllers
{
    public class CONTEXT_SENSOR_APPController : ApiController
    {
        // GET api/values 

        public AppSensor Get()
        {
            var sensor = new AppSensor();
            sensor.Update();
            return sensor;
        }
    }
}