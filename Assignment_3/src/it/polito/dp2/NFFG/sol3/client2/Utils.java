package it.polito.dp2.NFFG.sol3.client2;

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
	 * Transforms a XMLGregorianCalendar into a Calendar object
	 * 
	 * @param c
	 *            the XMLGregorianCalendar
	 * @return the Calendar
	 */
	public static Calendar CalendarFromXMLGregorianCalendar(XMLGregorianCalendar c) {
		return c.toGregorianCalendar();
	}
}
