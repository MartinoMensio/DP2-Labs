package it.polito.dp2.NFFG.sol1;

import java.util.*;

import javax.xml.datatype.*;

public class Utils {

	/**
	 * Transforms a Calendar object into an XMLGregorianCalendar
	 * 
	 * @param c
	 * @return
	 */
	public static XMLGregorianCalendar XMLGregorianCalendarFromCalendar(Calendar c) {
		XMLGregorianCalendar gc = null;
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(c.getTime());
		gregorianCalendar.setTimeZone(c.getTimeZone());
		try {
			gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			System.err.println("Error in gregorianCalendar handling: " + gregorianCalendar);
			e.printStackTrace();
			return null;
		}
		return gc;
	}

	public static Calendar CalendarFromXMLGregorianCalendar(XMLGregorianCalendar c) {
		return c.toGregorianCalendar();
	}
}
