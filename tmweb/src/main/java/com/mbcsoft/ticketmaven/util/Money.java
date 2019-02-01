/*
 *	Copyright (c) 2009
 *    Michael Berger
 *	All Rights Reserved
 *
 *	PROPRIETARY 
 *   This Software contains proprietary information that shall not be 
 *    in the possession of, distributed to, or routed to anyone except with written permission of Michael Berger.
 */
package com.mbcsoft.ticketmaven.util;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * a single place to format cents into dollars and vice versa.
 */
public class Money {
	
	/**
	 * Format cents into a dollar string.
	 * 
	 * @param cents the cents
	 * 
	 * @return the string
	 */
	public static String format(int cents)
	{
		return NumberFormat.getCurrencyInstance().format(cents/100.0);
	}
	
	/**
	 * Parses a dollar String into cents
	 * 
	 * @param s the dollar string (i.e. $5.99)
	 * 
	 * @return the int
	 * 
	 * @throws ParseException the parse exception
	 */
	public static int parse(final String s) throws ParseException 
	{		
			String ss = s;
			if( ss.indexOf("$") != 0)
				ss = "$" + ss;
			Number n = NumberFormat.getCurrencyInstance().parse(ss);
			return Double.valueOf(n.doubleValue()*100.0).intValue();
	}
}
