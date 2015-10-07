package eu.musesproject.windowsclient.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

/*
 * #%L
 * MUSES Client
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
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

public class MusesUtils {

	public static String serverCertificate = "";
	private static Locale currentLocale;
	private static ResourceBundle messages;
	
	public static String getCertificateFromSDCard()  {
		serverCertificate = ""; // FIXME
//		String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//		String certificateName = "localhost.crt";
//		try {
//			InputStream in = new BufferedInputStream(new FileInputStream(baseDir + File.separator + certificateName));
//			serverCertificate = inputStreamToString(in);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return serverCertificate;
			
	}
	
	public static String getCertificate(){
		return serverCertificate;
	}

	
	public static  String getMusesConf() {
		String settings = "sweoffice.mooo.com";
		try {
		    BufferedReader br = new BufferedReader(new FileReader("muses.conf"));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        settings = sb.toString();
		        settings = settings.replaceAll("\n", "");
		        settings = settings.replaceAll("\r", "");
		    } finally {
		        br.close();
		    }
			
		} catch (Exception e) {
			return settings;
		}
		return settings;
	}

	public static ResourceBundle getResourceBundle() {
		if (messages == null){
			currentLocale = new Locale(LoginMain.language, LoginMain.country);
            File file = new File("resources/MessagesBundle.properties");  
            ClassLoader loader=null;
            try {
            	URL[] urls = {file.toURI().toURL()};  
                loader = new URLClassLoader(urls); 
                messages = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault(), loader);
                return messages;
            } catch (MalformedURLException ex) {
            	ex.getLocalizedMessage();
            }
		} 
		return messages;
	}
	
	
    
}