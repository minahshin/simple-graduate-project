package com.sookmyung.fulfillment.controller;

import java.util.ArrayList;
import java.util.HashMap;

import com.sookmyung.fulfillment.jsonformat.*;

public class SetFulfillmentMsg {
	public ResponseForm setMsg() {
		ResponseForm res = new ResponseForm();
		
//		HashMap<String, Object> type0 = new HashMap<String, Object>();
//		type0.put("type", 0);
//		type0.put("speech", "�̰��� messages�� speech�Դϴ�.");
		
		res.setSpeech("�̰��� speech �Դϴ�");
		res.setDisplayText("�̰��� displaytext�Դϴ�");
//		res.getMessages().add(type0);
		
		return res;
	}
	
}
