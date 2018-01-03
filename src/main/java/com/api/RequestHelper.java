package com.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;



public class RequestHelper {
	
	
	
	public enum HttpMethodType { POST, GET, DELETE }
	
	public static String requestHttp(String reqestUrl, HttpMethodType method, Map<String, Object> params) {
		
		if (method == null) {
			method = HttpMethodType.GET;
        }
		
		String strParams = null;
		
		//param값을 구한다.
		if(params != null) {
			Iterator<String> keys = params.keySet().iterator();
			
			StringJoiner paramsJoiner = new StringJoiner("&");
			
			while(keys.hasNext()){
				String key = keys.next();
				Object value = params.get(key);
				if(!key.equals("serviceUrl") && value != null && !"".equals(value)){
					try {
						if(value instanceof String){
							paramsJoiner.add(URLEncoder.encode( key, "UTF-8" ) +"="+URLEncoder.encode( (String)value, "UTF-8" ));
						}else if(value instanceof Integer){
							paramsJoiner.add(URLEncoder.encode( key, "UTF-8" ) +"="+URLEncoder.encode( String.valueOf(value), "UTF-8" ));
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			
			strParams = paramsJoiner.toString();
			reqestUrl += strParams;
		}
		
		HttpURLConnection conn;
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        InputStreamReader isr = null;
        
        try {
			final URL url = new URL(reqestUrl);
			conn = (HttpURLConnection) url.openConnection();	//Http 프로토콜이니까 HttpURLConnnection 반
			conn.setRequestMethod(method.toString());
			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            
            //conn.setRequestProperty("Cache-Control", "no-cache");
			//conn.setRequestProperty("Accept", "*/*");
			//conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            
            if (strParams != null && strParams.length() > 0 && method == HttpMethodType.POST) {
            	//post는 스트림 방식이라 출력 스트림 사용 true
                conn.setDoOutput(true);
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(strParams);
                writer.flush();
            }
            
            final int responseCode = conn.getResponseCode();
            
            System.out.println("send request");
            System.out.println("request URL : " + reqestUrl);
            System.out.println("http method : " + method);
            System.out.println("Response Code : " + responseCode);
            
            if (responseCode == 200)
                isr = new InputStreamReader(conn.getInputStream());
            else
                isr = new InputStreamReader(conn.getErrorStream());

            reader = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            
            System.out.println("응답 : "+buffer.toString());
            
            return buffer.toString();
            
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) try { writer.close(); } catch (Exception e) { }
            if (reader != null) try { reader.close(); } catch (Exception e) { }
            if (isr != null) try { isr.close(); } catch (Exception e) { }
		}
        
		return null;
	}

}
