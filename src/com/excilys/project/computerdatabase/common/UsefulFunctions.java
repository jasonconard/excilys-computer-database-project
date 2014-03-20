package com.excilys.project.computerdatabase.common;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UsefulFunctions {

	/*
	 * Convert String to Date
	 */
	public static java.util.Date stringToDate(String stringDate){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		try {
			date = formatter.parse(stringDate);
		} catch (ParseException e) {
			System.err.println("Date parsing error (yyyy-MM-dd) :"+stringDate);
		}
		return date;
	}
}
