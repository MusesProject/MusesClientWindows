using System.Web.Http;
using Sensors;

namespace Sensors.Service.Controllers
{
    public class CONTEXT_SENSOR_CONNECTIVITYController : ApiController
    {
        // GET api/values 
        public ConnectivitySensor Get()
        {
            var sensor = new ConnectivitySensor();
            sensor.Update();
            return sensor;
        }
    }
}