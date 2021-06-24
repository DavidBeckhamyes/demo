package com.example.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
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
	public static void sendTemplateMessage(String openid) {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + at;
		String resultData = "";
		WxTemplate t = new WxTemplate();
		t.setTouser(openid);
		t.setTemplate_id("PrZY21P7oM65H_T574eDONZ9KgJirUzaokMuK4kdxYQ");
		t.setUrl(
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbbcc503ac70293c3&redirect_uri=http://zlhwechat.vipgz1.idcfengye.com/wx/GetUserInfo&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
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
		time.setValue("2021年6月16日16:16:16");
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
	 * 封装模板消息以及跳转方法（掃碼點餐）
	 *
	 * @author zhan
	 */
	public static void sendOrderMealTemplateMessage(String openid, String tableNo) {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + at;
		String resultData = "";
		WxTemplate t = new WxTemplate();
		t.setTouser(openid);
		t.setTemplate_id("3CAwvhZIrwLZH4pQVC2M8jQIoHOwCk9DMNiGfeymtk0");
		String jumpUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbbcc503ac70293c3&redirect_uri=http://zlhwechat.vipgz1.idcfengye.com/wx/createMealOrder?table_no=tableNo&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		jumpUrl = jumpUrl.replace("tableNo", tableNo);
		t.setUrl(jumpUrl);
		Map<String, TemplateData> m = new HashMap<String, TemplateData>();
		TemplateData first = new TemplateData();
		first.setColor("#173177");
		first.setValue("终于等到你，点击详情去点餐~");
		m.put("first", first);

		TemplateData company = new TemplateData();
		company.setColor("#173177");
		company.setValue("宏状元大柳树门店");
		m.put("keyword1", company);

		TemplateData tableInfo = new TemplateData();
		tableInfo.setColor("red");
		tableInfo.setValue(tableNo);
		m.put("keyword2", tableInfo);

		TemplateData time = new TemplateData();
		time.setColor("blue");

		Long timeStamp = System.currentTimeMillis(); // 获取当前时间戳
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒");
		String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));

		time.setValue(sd);
		m.put("keyword3", time);

		TemplateData remark = new TemplateData();
		remark.setColor("#000000");
		remark.setValue("点击立即下单或结账!");
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
