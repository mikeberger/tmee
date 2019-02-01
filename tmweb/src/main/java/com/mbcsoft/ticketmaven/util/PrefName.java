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


/**
 * this class contains the program preference keys and default values. Most are
 * edited via the main options UI
 */
public class PrefName {


	/** The preference name */
	private String name_;

	/** The default value */
	private Object default_;

	/**
	 * Instantiates a new pref name.
	 * 
	 * @param name the name
	 * @param def the default value
	 */
	public PrefName(String name, Object def) {
		setName(name);
		setDefault(def);
	}

	/**
	 * Sets the name.
	 * 
	 * @param name_ the new name
	 */
	void setName(String name_) {
		this.name_ = name_;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	String getName() {
		return name_;
	}

	/**
	 * Sets the default.
	 * 
	 * @param default_ the new default
	 */
	void setDefault(Object default_) {
		this.default_ = default_;
	}

	/**
	 * Gets the default.
	 * 
	 * @return the default
	 */
	Object getDefault() {
		return default_;
	}

	/**
	 * database folder (part of jdbc url)
	 */
	static public PrefName DBDIR = new PrefName("dbdir", "");

	/**
	 * max rows to show in various tables before putting out a warning that
	 * filtering is needed
	 */
	static public PrefName MAXQUERYROWS = new PrefName("maxqueryrows",
			Integer.valueOf(10000));

	/** default format for date/time */
	static public PrefName DATEFORMAT = new PrefName("dateformat",
			"EEE MMM dd yyyy  h:mm a");

	/** community name for printing on tickets */
	static public PrefName COMMUNITY = new PrefName("community",
			"Community_Name");

	/** default socket port to use for the limited set of socket messages in TM */
	static public PrefName SOCKETPORT = new PrefName("socketport", Integer.valueOf(
			2992));

	/** default seats at a table */
	static public PrefName TBLSEATS = new PrefName("table_seats", Integer.valueOf(
			10));

	/** default sales tax percent */
	static public PrefName SALESTAX = new PrefName("salestax", "6.5");

	/** automatic backup folder - default is none - feature off */
	static public PrefName BACKUPDIR = new PrefName("backupdir", "");

	/**
	 * option to number seats based on chairs actually present, not column umber
	 * in the seat grid
	 */
	static public PrefName NUMBER_SEATS_FROM_LEFT = new PrefName(
			"number_from_left", "false");

	/** The default font. */
	static public PrefName DEFFONT = new PrefName("defaultfont", "");

	/** The LNF. */
	static public PrefName LNF = new PrefName("lnf",
			"com.jgoodies.looks.plastic.PlasticXPLookAndFeel");

	/** The COUNTRY. */
	static public PrefName COUNTRY = new PrefName("country", "");

	/** The LANGUAGE. */
	static public PrefName LANGUAGE = new PrefName("language", "");

	/** window size - used by various windows to remember their size */
	static public PrefName HELPVIEWSIZE = new PrefName("helpviewsize",
			"-1,-1,-1,-1,N");

	/** window size - used by various windows to remember their size */
	static public PrefName SEATGRIDSIZE = new PrefName("seatgridsize",
			"-1,-1,-1,-1,N");

	/** window size - used by various windows to remember their size */
	public static PrefName MAINVIEWSIZE = new PrefName("mainviewsize",
			"-1,-1,-1,-1,Y");

	/** window size - used by various windows to remember their size */
	public static PrefName OPTIONSVIEWSIZE = new PrefName("optionsviewsize",
			"-1,-1,-1,-1,N");

	/** default ticket background for auditorium ticket */
	public static PrefName LOGOFILE = new PrefName("logofile", "samples/grayfade.gif");

	/** default ticket background for Table reservation */
	public static PrefName RLOGOFILE = new PrefName("rlogofile", "samples/rainbow.gif");

	/** where to put the main UI tabs - top or left */
	public static PrefName TABSIDE = new PrefName("tabside", "Left");
	
	/** option to use the system tray */
	static public PrefName SYSTRAY = new PrefName("systray", "false");
	
	/** allow packages to be updated */
	static public PrefName UPDPKG = new PrefName("updpkg", "false");
	
	static public PrefName LICENSE_KEY = new PrefName("licensekey", "");
	
	static public PrefName FAVOR_AISLE_REQUESTS = new PrefName("favor_aisle_requests", "true");

	public static PrefName DEBUG = new PrefName("debug", "false");
	
	public static PrefName DBPROMPT = new PrefName("dbprompt", "false");
	
	public static PrefName ALLOWLAYOUTEDIT = new PrefName("allowLayoutEdit", "false");
	
	public static PrefName MEMBAR_TIMEOUT = new PrefName("membrar-timeout", -1);
	public static PrefName SERVER = new PrefName("server", "");

}
