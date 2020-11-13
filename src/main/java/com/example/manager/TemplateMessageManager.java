package com.example.manager;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.TemplateData;
import com.example.entity.WxTemplate;
import com.example.service.WxService;
import com.example.util.TulingUtil;

public class TemplateMessageManager {
	/**
	 * 设置行业
	 * 
	 * @author 詹凌瀚
	 *
	 */
	public void setProfession() {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=" + at;
		String data = "{\n" + "    \"industry_id1\":\"1\",\r\n" + "    \"industry_id2\":\"4\"\r\n" + "}";
		String result = TulingUtil.post(url, data);
		System.out.println(result);
	}

	/**
	 * 发送模板消息
	 * 
	 * @author zhan
	 */
	public static void sendTemplateMessage() {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + at;
		String resultData = "";
		WxTemplate t = new WxTemplate();
		t.setTouser("oq3FLv6uifHWSOLM5Z8Tk6xg6zAQ");
		t.setTemplate_id("PrZY21P7oM65H_T574eDONZ9KgJirUzaokMuK4kdxYQ");
		Map<String, TemplateData> m = new HashMap<String, TemplateData>();
		TemplateData first = new TemplateData();
		first.setColor("#173177");
		first.setValue("面试结果");
		m.put("first", first);
		TemplateData company = new TemplateData();
		company.setColor("#173177");
		company.setValue("中铁程科技有限公司");
		m.put("company", company);
		TemplateData time = new TemplateData();
		time.setColor("blue");
		time.setValue("2020年11月2日17:14:50");
		m.put("time", time);
		TemplateData result = new TemplateData();
		result.setColor("red");
		result.setValue("恭喜您已被我公司录取");
		m.put("result", result);
		TemplateData remark = new TemplateData();
		remark.setColor("#000000");
		remark.setValue("请及时与我公司人事经理联系!");
		m.put("remark", remark);
		t.setData(m);
		String requestJsonData = JSONObject.toJSONString(t);
		resultData = TulingUtil.post(url, requestJsonData);
		System.out.println(resultData);
	}

	/**
	 * 新增临时素材
	 */
	public void testUpload() {
		String file = "";
		String result = WxService.upload(file, "image");
		System.out.println(result);
	}
}
