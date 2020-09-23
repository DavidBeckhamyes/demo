package com.example.service;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.AccessToken;
import com.example.entity.Article;
import com.example.entity.BaseMessage;
import com.example.entity.ImageMessage;
import com.example.entity.MusicMessage;
import com.example.entity.NewsMessage;
import com.example.entity.TextMessage;
import com.example.entity.VideoMessage;
import com.example.entity.VoiceMessage;
import com.example.util.TulingUtil;
import com.thoughtworks.xstream.XStream;

public class WxService {
	private static final String Token = "weixin21312izhhann";
	private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	private static final String APPID = "wxbbcc503ac70293c3";
	private static final String APPSECRET = "91284cfecad1bc9270b38269d4682a82";
	//用于存储token
	private static AccessToken at;

	/**
	 * 获取token
	 * 
	 * @author 詹凌瀚
	 */
	private static void getToken() {
		String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		String access_token_str = TulingUtil.get(url);
		JSONObject jsonObject = JSONObject.parseObject(access_token_str);
		String access_token = jsonObject.getString("access_token");
		String expireIn = jsonObject.getString("expires_in");
		//创建token对象,并存起来
		at = new AccessToken(access_token, expireIn);
	}
	
	/**
	 * 向外暴露的获取token的方法
	 * @return
	 */
	public static String getAccessToken() {
		if (at == null || at.isExpired()) {
			getToken();
		}
		return at.getAccessToken();
	}

	/**
	 * 验证签名
	 * 
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @return
	 * @author zhan
	 */
	public static boolean check(String timestamp, String nonce, String signature) {
		// 1).将token、timestamp、nonce三个参数进行字典序排序
		String[] strs = new String[] { Token, timestamp, nonce };
		Arrays.sort(strs);
		// 2).将三个参数字符串拼接成一个字符串进行sha1加密
		String str = strs[0] + strs[1] + strs[2];
		String mysig = sha1(str);
		System.out.println(mysig);
		System.out.println(signature);
		// 3).开发者获得加密后的字符串可与signature对比,标识该请求来源于微信
		return mysig.equalsIgnoreCase(signature);
	}

	/**
	 * 进行sha1加密
	 * 
	 * @param str
	 * @return
	 * @author zhan
	 */
	private static String sha1(String src) {
		try {
			// 获取一个加密对象
			MessageDigest md = MessageDigest.getInstance("sha1");
			// 加密
			byte[] digest = md.digest(src.getBytes());
			char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			StringBuilder sb = new StringBuilder();
			// 处理加密结果
			for (byte b : digest) {
				sb.append(chars[(b >> 4) & 15]);
				sb.append(chars[b & 15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, String> parseRequest(InputStream is) {
		Map<String, String> map = new HashMap<>();
		SAXReader reader = new SAXReader();
		try {
			// 读取输入流，获取文档对象
			Document document = reader.read(is);
			// 根据文档对象获取根节点
			Element root = document.getRootElement();
			// 获取根节点的所有子节点
			List<Element> elements = root.elements();
			for (Element e : elements) {
				map.put(e.getName(), e.getStringValue());
			}
			return map;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 用于处理所有的事件和消息的回复
	 * 
	 * @param requestMap
	 * @return 返回的是XML数据包
	 * @author zhan
	 */
	public static String getResponse(Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		BaseMessage msg = null;
		String msgType = requestMap.get("MsgType");
		switch (msgType) {
		// 处理文本消息
		case "text":
			msg = dealTextMessage(requestMap);
			break;
		case "image":
			break;
		case "voice":
			break;
		case "video":
			break;
		case "shortvideo":
			break;
		case "location":
			break;
		case "link":
			break;
		default:
			break;
		}
		// 把消息对象处理为xml数据包
		if (msg != null) {
			return beanToXml(msg);
		} else {
			return null;
		}
	}

	/**
	 * 把消息对象处理为xml数据包
	 * 
	 * @param msg
	 * @return
	 * @author zhan
	 */
	private static String beanToXml(BaseMessage msg) {
		// TODO Auto-generated method stub
		XStream stream = new XStream();
		// 设置需要处理XStreamAlias("xml)注释的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(msg);
		return xml;
	}

	/**
	 * 调用图灵机器人聊天
	 * 
	 * @param msg
	 * @return
	 * @author zhan
	 */
	private static String chat(String msg) {
		// TODO Auto-generated method stub
		// 生成传给机器人的消息
		String req = TulingUtil.getReqMes(msg);
		// 返回机器人回复的消息
		String resp = TulingUtil.tulinPost(req);
		System.out.print(resp);
		// 解析Json
		JSONObject jsonObject = JSONObject.parseObject(resp);
		List<?> results = (List<?>) jsonObject.get("results");
		JSONObject resultObject = JSONObject.parseObject(results.get(0).toString());
		JSONObject valuesObject = JSONObject.parseObject(resultObject.get("values").toString());
		return valuesObject.getString("text").toString();
	}

	/**
	 * 处理文本消息
	 */
	private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
		// 用户发来的内容
		String msg = requestMap.get("Content");
		if (msg.equals("图文")) {
			List<Article> articles = new ArrayList<>();
			articles.add(new Article("这是图文消息的标题", "这是图文消息的详细介绍",
					"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1597236473&di=4d48d0e5fdb172c1eab035d3ecdeda32&src=http://dik.img.kttpdq.com/pic/19/12786/67278f953e503402_1024x768.jpg",
					"https://www.baidu.com"));
			NewsMessage nm = new NewsMessage(requestMap, articles);
			return nm;
		}
		// 调用方法返回聊天的内容
		String resp = chat(msg);
		TextMessage tm = new TextMessage(requestMap, resp);
		return tm;
	}
}
