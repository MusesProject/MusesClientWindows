﻿/*
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