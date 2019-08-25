package com.sookmyung.fulfillment.control;

import com.sookmyung.fulfillment.SetFulfillmentMsg;
import com.sookmyung.fulfillment.db.category.CategoryDAOService;
import com.sookmyung.fulfillment.db.restaurant.Restaurant;
import com.sookmyung.fulfillment.db.restaurant.RestaurantDAOService;

// import javax.servlet.http.HttpSession;

import com.sookmyung.fulfillment.jsonformat.RequestForm;
import com.sookmyung.fulfillment.jsonformat.ResponseForm;

public class IntentHandler {
	SetFulfillmentMsg setting = new SetFulfillmentMsg();
	RestaurantDAOService resDAO = null;
	CategoryDAOService cateDAO = null;
	
	ResponseForm res = new ResponseForm();
	
	public IntentHandler(RestaurantDAOService resDAO, CategoryDAOService cateDAO){
		this.resDAO = resDAO;
		this.cateDAO = cateDAO;
	}
	
	public ResponseForm chooseIntent(RequestForm req) throws Exception {
		
		String intent = (String) req.getResult().getMetadata().get("intentName");		
		
		if(intent.contains("Get_sensors")) 
			res = setRestaurantResult(req);
		
		else if(intent.equals("ask_for_satisfying - no")) 
			res = setSpeechAskforSatisfying(req);
		

		return res;
	}
	
	public ResponseForm setRestaurantResult(RequestForm req) throws Exception {
		Restaurant rest = new Restaurant();
		
		ConditionHandler condhandler = new ConditionHandler(cateDAO);
		String time = condhandler.setTimeString();
		
		double lat = Double.parseDouble((String) req.getResult().getParameters().get("latitude"));
		double lng = Double.parseDouble((String) req.getResult().getParameters().get("longitude"));
		String weather = (String) req.getResult().getParameters().get("weather");
		String big_category = null;
		String small_category = null;
		String speech = null;
		String displayText = null;
		
		int index = 0;

		while(index < req.getResult().getContexts().size()) {
			if(req.getResult().getContexts().get(index).getName().equals("tell_category-followup")) {
				big_category = (String) req.getResult().getContexts().get(index).getParameters().get("category");
				small_category = (String) req.getResult().getContexts().get(index).getParameters().get("category.original");
				break;
			}
			index++;
			
		}
		
		if(big_category == null)
			big_category = condhandler.setCategoryString(weather);
		
		if(small_category != null) {
			rest = resDAO.getRestaurantByLocationMenu(lng, lat, small_category, 0.15);
			if(rest == null) {
				rest = resDAO.getRestaurantByLocationMenu(lng, lat, small_category, 0.3);
				if(rest == null)
					rest = resDAO.getRestaurantByLocationSmallCategory(lng, lat, small_category, 0.3);
					if(rest == null) {
						rest = resDAO.getRestaurantByLocationSmallCategory(126.964831, 37.546517, small_category, 0.5);
						if(rest != null)
							speech = "�б� ��ó ������ �߿��� ";
						else
							rest = new Restaurant();
					}
			}
		}	

		if(rest.getName() == null) {
			if(time.equals("����") && (!big_category.equals("��������������") || !big_category.equals("Ŀ����/ī��")))
				time = "�Ļ�";
			
			if((time.equals("����") || time.equals("�Ļ�")) && (big_category.equals("��������������") || big_category.equals("Ŀ����/ī��")))
				time = "����";
			
			rest = resDAO.getRestaurantByLocationTimeBigCategory(lng, lat, time, big_category, 0.3);		
			if(rest == null) {
				rest = resDAO.getRestaurantByLocationTimeBigCategory(126.964831, 37.546517, time, big_category, 0.5);
				speech = "�б� ��ó ������ �߿��� ���� ���ǰ� ������ ���� �߿� ";
			}
		}
			
		
		
		if(speech == null)
			speech = "";
		
		speech += "���� ��õ�ϴ� �������� " + rest.getName() + "(��)��! �ű⼭ " + rest.getMenu() + "�? ������ �� ����.";
		displayText = rest.getName() + ";" + rest.getAddress();
		
		res = setting.setMsg(speech, displayText);
		
		return res;
	}
	
	public ResponseForm setSpeechAskforSatisfying(RequestForm req) throws Exception {
		String speech = null;
		String displayText = null;
		
		int index = 0;
		boolean firstResult = false;
		boolean secondResult = false;
		while(index < req.getResult().getContexts().size()) {
			if(req.getResult().getContexts().get(index).getName().equals("get_sensors_set_first_result-followup"))
				firstResult = true;
			
			if(req.getResult().getContexts().get(index).getName().equals("get_sensors_set_second_result-followup"))
				secondResult = true;
				
			index++;
		}
		
		if(firstResult == true && secondResult == false)
			speech = "�׷�? �׷� � ������ �԰�;�? ī�װ��� �˷��ְڴ�?";
		else
			speech = "�� ��õ�� ������ �ȵ���ٴ� �ƽ���. ����� �̶�� �Է��ϸ� �ٽ� ��õ �� �ٰ�!";
		
		res = setting.setMsg(speech, displayText);
		
		return res;
	}
}
