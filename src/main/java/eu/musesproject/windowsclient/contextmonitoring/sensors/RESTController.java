package eu.musesproject.windowsclient.contextmonitoring.sensors;

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
 * Created by Alireza on 5/4/2015.
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
        //check for
        responseData = rd.readLine();

        return gson.fromJson(responseData, new HashMap<String,String>().getClass());
    }
    public static Map<String, String> requestSensorInfo(String sensorType, String[] params) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:9000/api/"+sensorType+"/"+String.join("/", params));
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String responseData = "";
        Gson gson=new Gson();
        //check for
        responseData = rd.readLine();

        return gson.fromJson(responseData, new HashMap<String,String>().getClass());
    }
}
