package com.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
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

	// 微信公众号
	private static final String APPID = "wxbbcc503ac70293c3";
	private static final String APPSECRET = "91284cfecad1bc9270b38269d4682a82";

	// 百度AI
	// 设置APPID/AK/SK
	public static final String APP_ID = "22777431";
	public static final String API_KEY = "6qjdg0gX1W1GLh6eexvHYVUN";
	public static final String SECRET_KEY = "LQHF5nAK39H6EsovNVEnaYZ5GiBKG0i5";
	// 用于存储token
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
		// 创建token对象,并存起来
		at = new AccessToken(access_token, expireIn);
	}

	/**
	 * 向外暴露的获取token的方法
	 * 
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
			msg = dealImage(requestMap);
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
		case "event":
			msg = dealEvent(requestMap);
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
	 * 图像识别
	 * 
	 * @param requestMap
	 * @author zhan
	 * @return
	 */
	private static BaseMessage dealImage(Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 调用接口
		String path = requestMap.get("PicUrl");
		org.json.JSONObject res = client.basicGeneralUrl(path, new HashMap<String, String>());
		String json = res.toString();
		// 转为jsonObject
		JSONObject jsonObject = JSONObject.parseObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("words_result");
		Iterator<Object> it = jsonArray.iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			JSONObject next = (JSONObject) it.next();
			sb.append(next.getString("words"));
		}
		return new TextMessage(requestMap, sb.toString());
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

	/**
	 * 处理事件推送
	 * 
	 * @param requestMap
	 * @return
	 * @author 詹凌瀚
	 */
	private static BaseMessage dealEvent(Map<String, String> requestMap) {
		String event = requestMap.get("Event");
		switch (event) {
		case "CLICK":
			return dealClick(requestMap);
		case "VIEW":
			return dealView(requestMap);
		default:
			break;
		}
		return null;
	}

	/**
	 * 处理view类型的按钮的菜单
	 * 
	 * @param requestMap
	 * @return
	 * @author 詹凌瀚
	 */
	private static BaseMessage dealView(Map<String, String> requestMap) {
		return null;
	}

	/**
	 * 处理click类型的按钮的菜单
	 * 
	 * @param requestMap
	 * @return
	 * @author 詹凌瀚
	 */
	private static BaseMessage dealClick(Map<String, String> requestMap) {
		String key = requestMap.get("EventKey");
		switch (key) {
		case "1":
			// 处理点击了第一个一级菜单
			return new TextMessage(requestMap, "你点了一下第一个一级菜单");
		case "32":
			// 处理点击了第三个一级菜单的第二个子菜单
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * 上传临时素材
	 * 
	 * @param path
	 *            上传的文件的路径
	 * @param type
	 *            上传的文件类型
	 * @return
	 * @author zhan
	 */

	public static String upload(String path, String type) {
		File file = new File(path);
		// 地址
		String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
		try {
			URL urlObj = new URL(url);
			try {
				// 强转为安全链接
				HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
				// 设置连接的信息
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				// 设置请求头信息
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", "utf8");
				// 数据的边界
				String boundary = "-----" + System.currentTimeMillis();
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				// 获取输出流
				OutputStream out = conn.getOutputStream();
				// 创建文件的输入流
				InputStream is = new FileInputStream(file);
				// 第一部分:头部信息
				// 准备头部信息
				StringBuilder sb = new StringBuilder();
				sb.append("--");
				sb.append(boundary);
				sb.append("\r\n");
				sb.append("Content-Disposition:form-data;name=\"media\";filename=\"" + file.getName() + "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");
				out.write(sb.toString().getBytes());
				out.flush();
				out.close();
				// 第二部分:文件内容
				byte[] b = new byte[1024];
				int len;
				while ((len = is.read(b)) != -1) {
					out.write(b, 0, len);
				}
				// 第三部分:尾部信息
				String foot = "\r\n" + boundary + "--\r\n";
				out.write(foot.getBytes());
				out.flush();
				out.close();
				// 读取数据
				InputStream is2 = conn.getInputStream();
				StringBuilder resp = new StringBuilder();
				while ((len = is2.read(b)) != -1) {
					resp.append(new String(b, 0, len));
				}
				return resp.toString();
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
	 * 获取带参数二维码的ticket
	 * 
	 * @return
	 * @author zhan
	 */
	public static String getQrCodeTicket() {
		String at = getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + at;
		//生成临时字符二维码
		String data = "{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"zlhzs\"}}}";
		String result = TulingUtil.post(url, data);
		return null;

	}
}
