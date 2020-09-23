package com.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.WxService;

@RestController
@RequestMapping("/wx")
public class TestNgrockApi {

	@RequestMapping("/test")
	public String index() {
		return "Greeting from SpringBoot";

	}

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
