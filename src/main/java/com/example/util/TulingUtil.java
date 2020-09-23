package com.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.fastjson.JSONObject;

public class TulingUtil {
	// 存储APIkey
	public static final String API_KEY = "d79f6d21816842af9aefdca7587da088";
	// 存储接口请求地址
	public static final String API_URL = "http://openapi.tuling123.com/openapi/api/v2";
	// 用户id
	public static final String USER_ID = "921120";

	/**
	 * 向指定的地址发送一个post请求,带着data数据
	 * 
	 * @param url
	 * @author 詹凌瀚
	 * @return
	 */
	public static String post(String url, String data) {
		URL urlObj = null;
		try {
			urlObj = new URL(url);
			URLConnection connection = urlObj.openConnection();
			// 要发送数据出去,必须要设置为可发送数据状态
			connection.setDoOutput(true);
			// 获取输出流
			OutputStream os = connection.getOutputStream();
			// 写出数据
			os.write(data.getBytes());
			os.close();
			// 获取输入流
			InputStream is = connection.getInputStream();
			byte[] b = new byte[1024];
			int len;
			StringBuilder sb = new StringBuilder();
			while ((len = is.read(b)) != -1) {
				sb.append(new String(b, 0, len));
			}
			return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 向指定的地址发送get请求
	 * 
	 * @param url
	 * @return
	 * @author 詹凌瀚
	 */
	public static String get(String url) {
		URL urlObj = null;
		try {
			urlObj = new URL(url);
			// 开连接
			URLConnection connection;
			try {
				connection = urlObj.openConnection();
				InputStream is = connection.getInputStream();
				byte[] b = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = is.read(b)) != -1) {
					sb.append(new String(b, 0, len));
				}
				return sb.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 封装入参
	 * 
	 * @param reqMes
	 * @return
	 */
	public static String getReqMes(String reqMes) {
		// 请求json，里面包含reqType，perception，userInfo
		JSONObject reqJson = new JSONObject();
		// 输入类型:0-文本(默认)、1-图片、2-音频
		int reqType = 0;
		reqJson.put("reqType", reqType);

		// 输入信息,里面包含inputText，inputImage，selfInfo
		JSONObject perception = new JSONObject();
		// 输入的文本信息
		JSONObject inputText = new JSONObject();
		inputText.put("text", reqMes);
		perception.put("inputText", inputText);
		// 输入的图片信息
		// JSONObject inputImage = new JSONObject();
		// inputImage.put("url","");
		// perception.put("inputImage",inputImage);
		// 个人信息，里面包含location
		// JSONObject selfInfo = new JSONObject();
		// 包含city，province，street
		// JSONObject location = new JSONObject();
		// location.put("city","");
		// location.put("province","");
		// location.put("street","");
		// selfInfo.put("location",location);
		// perception.put("selfInfo",selfInfo);
		// 用户信息
		JSONObject userInfo = new JSONObject();
		userInfo.put("apiKey", API_KEY);
		userInfo.put("userId", USER_ID);
		reqJson.put("perception", perception);
		reqJson.put("userInfo", userInfo);
		return reqJson.toString();
	}

	/**
	 * 获取图灵机器人自动回复消息
	 * 
	 * @param reqMes
	 * @return
	 */
	public static String tulinPost(String reqMes) {
//		String status = "";
		String responseStr = "";
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			URL realUrl = new URL(API_URL);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
			// 设置请求属性
			httpUrlConnection.setRequestProperty("Content-Type", "application/json");
			httpUrlConnection.setRequestProperty("x-adviewrtb-version", "2.1");
			// 发送POST请求必须设置如下两行
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpUrlConnection.getOutputStream());
			// 发送请求参数
			out.write(reqMes);
			// flush输出流的缓冲
			out.flush();
			httpUrlConnection.connect();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				responseStr += line;
			}
//			status = new Integer(httpUrlConnection.getResponseCode()).toString();
//         System.out.println("status=============="+status);
//         System.out.println("response=============="+responseStr);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return responseStr;
	}

}
