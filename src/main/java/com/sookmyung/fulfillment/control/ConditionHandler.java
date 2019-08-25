package com.sookmyung.fulfillment.control;

import java.util.Calendar;
import java.util.Random;

import com.sookmyung.fulfillment.db.category.CategoryDAOService;

public class ConditionHandler {
	CategoryDAOService cateDAO = null;
	
	public ConditionHandler(CategoryDAOService cateDAO) {
		this.cateDAO = cateDAO;
	}

	public String setTimeString() {
		Calendar cal = Calendar.getInstance();
		
		int hour = cal.get(cal.HOUR_OF_DAY);
		String time = null;
		
		if((hour > 9 && hour <12) || (hour > 13 && hour <18))
			time = "����";
		else if(hour > 17)
			time = "����";
		else
			time = "�Ļ�";
		
		return time;
	}
	
	public String setCategoryString(String weather) throws Exception {
		Random random = new Random();
		String category = null;
		String time = setTimeString();
		String[] snackList = {"��������������", "Ŀ����/ī��"};
		
		if(weather.equals("hot") || weather.equals("cloudy") || weather.equals("sunny"))
			weather = "all";
		
		if(time.equals("����"))
			category = snackList[random.nextInt(snackList.length)];
		else
			category = cateDAO.getCategoryByWeather(weather).getCategory();
		
		return category;
	}
}
