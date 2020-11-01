package com.example.manager;

import com.example.service.WxService;
import com.example.util.TulingUtil;

/**
 * 设置行业
 * 
 * @author 詹凌瀚
 *
 */
public class TemplateMessageManager {
	public static void main(String[] args) {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=" +at;
		String data = "{\n" + "    \"industry_id1\":\"1\",\r\n" + "    \"industry_id2\":\"4\"\r\n" + "}";
		String result = TulingUtil.post(url, data);
		System.out.println(result);
	}
}
