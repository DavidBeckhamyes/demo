package com.example.util;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Button;
import com.example.entity.ClickButton;
import com.example.entity.PhotoOrAlbumButton;
import com.example.entity.SubButton;
import com.example.entity.ViewButton;
import com.example.service.WxService;

public class CreateMenu {
	public static void main(String[] args) {
		// 菜单对象
		Button btn = new Button();
		// 第一个一级菜单
		btn.getButton().add(new ClickButton("一级点击", "1"));
		// 第二个一级菜单
		btn.getButton().add(new ViewButton("一级跳转", "https://www.baidu.com"));
		// 创建第三个一级菜单
		SubButton sb = new SubButton("有子菜单");
		// 为第三个一级菜单增加子菜单
		sb.getSub_button().add(new PhotoOrAlbumButton("传图", "31"));
		sb.getSub_button().add(new ClickButton("点击", "32"));
		sb.getSub_button().add(new ViewButton("网易新闻", "https://news.163.com"));
		btn.getButton().add(sb);
		// 转为json
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(btn);
		// 准备url
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
		url = url.replace("ACCESS_TOKEN", WxService.getAccessToken());
		//发送请求
		String result = TulingUtil.post(url, jsonObject.toString());
		System.out.println(result);
	}
}
