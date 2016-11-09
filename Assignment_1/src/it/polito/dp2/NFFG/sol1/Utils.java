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
		GregorianCalendar lastUpdate = new GregorianCalendar();
		lastUpdate.setTime(c.getTime());
		try {
			gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(lastUpdate);
		} catch (DatatypeConfigurationException e) {
			System.err.println("Error in lastUpdate handling: lastUpdate = " + lastUpdate);
			e.printStackTrace();
			System.exit(1);
		}
		return gc;
	}

}
