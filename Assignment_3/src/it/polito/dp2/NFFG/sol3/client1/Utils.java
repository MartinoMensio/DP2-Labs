package it.polito.dp2.NFFG.sol3.client1;

import java.util.*;

import javax.xml.datatype.*;

/**
 * Some static utilities
 * 
 * @author Martino Mensio
 *
 */
public class Utils {

	/**
	 * Transforms a Calendar object into an XMLGregorianCalendar
	 * 
	 * @param c
	 *            the Calendar object
	 * @return the XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar XMLGregorianCalendarFromCalendar(Calendar c) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(c.getTime());
		gregorianCalendar.setTimeZone(c.getTimeZone());
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			System.err.println("Error in gregorianCalendar handling: " + gregorianCalendar);
			e.printStackTrace();
			return null;
		}
	}
}
