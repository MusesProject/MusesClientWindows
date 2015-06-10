package eu.musesproject.windowsclient.view;

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
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(
//					Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator +"muses.conf"));
//			settings = reader.readLine();
//			reader.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return settings;
	}
    
}