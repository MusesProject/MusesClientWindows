package eu.musesproject.windowsclient.contextmonitoring.sensors;
/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
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

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alirezaalizadeh
 * This class handles RESTful communication between sensor .net running service
 * and java client app
 */
public class RESTController {
    public static Map<String, String> requestSensorInfo(String sensorType) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:9000/api/"+sensorType);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String responseData = "";
        Gson gson=new Gson();
        //ToDo:check for readLine function
        responseData = rd.readLine();

        return gson.fromJson(responseData, new HashMap<String,String>().getClass());
    }
    public static Map<String, String> requestSensorInfo(String sensorType, String[] params) throws IOException {
        HttpClient client = new DefaultHttpClient();

        // join params
        String paramsStr = "/";
        for (String param : params) {
            paramsStr += param + "/";
        }

        HttpGet request = new HttpGet("http://localhost:9000/api/" + sensorType + paramsStr);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String responseData = "";
        Gson gson=new Gson();
        //ToDo:check for readLine function
        responseData = rd.readLine();

        return gson.fromJson(responseData, new HashMap<String,String>().getClass());
    }
}
