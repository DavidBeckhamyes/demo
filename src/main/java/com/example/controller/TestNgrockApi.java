package com.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Product;
import com.example.service.WxService;
import com.example.util.TulingUtil;

//@RestController
@Controller
@RequestMapping("/wx")
public class TestNgrockApi {

	@ResponseBody
	@RequestMapping(value = "/QR", method = RequestMethod.GET)
	public String getQrCode() {
		String ticket = WxService.getQrCodeTicket();
		return ticket;
	}

	@ResponseBody
	@RequestMapping(value = "/GetUserInfo")
	public void getUserInfo(HttpServletRequest request) {
		// 获取code
		String code = request.getParameter("code");
		// 换取accesstoken的地址
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		url = url.replace("APPID", "wxbbcc503ac70293c3").replace("SECRET", "91284cfecad1bc9270b38269d4682a82")
				.replace("CODE", code);
		String result = TulingUtil.get(url);
		System.out.println(result);
		String at = JSONObject.parseObject(result).getString("access_token");
		String openid = JSONObject.parseObject(result).getString("openid");
		// 拉取用户的基本信息
		url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		url = url.replace("ACCESS_TOKEN", at).replace("OPENID", openid);
		result = TulingUtil.get(url);
		System.out.println(result);
	}

	@RequestMapping("/welcome")
	public String welcome(Map<String, Object> map) {
		// 给Thymeleaf 准备数据
		map.put("welcome", "欢迎来到王者荣耀"); // 给request域中方welcome
		List<Product> prods = new ArrayList<>();
		prods.add(new Product("a", 100, 10));
		prods.add(new Product("b", 200, 20));
		prods.add(new Product("c", 300, 30));
		map.put("prods", prods);
		return "thymeleaf";
	}

	@ResponseBody
	@RequestMapping("/test")
	public String index(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		return "Greeting from SpringBoot";

	}

	@ResponseBody
	@RequestMapping("/receiveReq")
	public String test(HttpServletRequest request) {
		// 签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		// 校验请求
		if (WxService.check(timestamp, nonce, signature)) {
			return echostr;
		} else {
			System.out.println("接入失败");
			return null;
		}
	}

	// 调用核心业务类接收消息、处理消息跟推送消息
	@ResponseBody
	@RequestMapping(value = "/receiveReq", method = RequestMethod.POST)
	protected void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		// 处理消息和事件推送
		Map<String, String> requestMap = WxService.parseRequest(request.getInputStream());
		System.out.println(requestMap);
		// 准备回复的数据包
		String respXml = WxService.getResponse(requestMap);
		PrintWriter out = response.getWriter();
		out.print(respXml);
		out.flush();
		out.close();
	}
}
