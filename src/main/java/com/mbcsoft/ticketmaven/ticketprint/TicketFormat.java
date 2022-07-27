package com.mbcsoft.ticketmaven.ticketprint;

/*-
 * #%L
 * tmee
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Color;

/*******************************************************************************
 * Copyright (C) 2019 Mike Berger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 This whole class needs a rewrite as it is just cut&paste from a desktop app
 **/
@XmlRootElement(name = "TicketFormat")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketFormat {

	public TicketFormat() {
		initPrefs();
	}

	/** available number of lines of text per ticket */
	public static final int NUM_LINES = 8;
	public static final int NUM_STUB_LINES = 2;
	public static final String PREFIX = "line";
	public static final String STUB_PREFIX = "stub";

	// default line texts for Tickets
	private static final String lineDefaults[] = { "{club}", "Presents", "{show}", "{date}",
			"{price} per person (Tax Included)", "{name}", "Row {row}   Seat {seat}", "No Refunds or Exchanges" };

	// default line texts for stubs
	private static final String stubLineDefaults[] = { "{name}", "Row {row} Seat {seat}" };

	
	/**
	 * Class Line holds the formatting info for 1 line of a Ticket
	 */
	@XmlRootElement(name = "Line")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Line implements Serializable {

		public Line() {
			super();
		}

		private static final long serialVersionUID = 1L;

		@XmlJavaTypeAdapter(ColorAdapter.class)
		private Color color;
		private String text;
		private String font;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getFont() {
			return font;
		}

		public void setFont(String font) {
			this.font = font;
		}

	}

	private static class ColorAdapter extends XmlAdapter<String, Color> {
		@Override
		public Color unmarshal(String s) {
			int ci = Integer.parseInt(s);
			return new Color(ci);
		}

		@Override
		public String marshal(Color c) {
			return Integer.toString(c.getRGB());
		}
	}

	/**
	 * Gets the default line texts for a layout type.
	 * @return the line defaults
	 */
	static public String[] getLineDefaults() {
		return lineDefaults;
	}

	/**
	 * Inits the preferences for the global ticket format defaults - the ugly scheme
	 * of pref names is being kept for backwards compatibility with older versions.
	 *
	 */
	 private void initPrefs() {
		String f = getPref(PREFIX + "0font", "not-set");
		if (f.equals("not-set")) {
			String fonts[] = { "Arial-BOLD-11", "", "Arial-BOLD-11", "", "", "", "", "" };
			Color colors[] = { Color.RED, Color.BLACK, Color.BLUE, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					Color.BLACK };
			for (int i = 0; i < NUM_LINES; i++) {
				putPref(PREFIX + i + "color", Integer.toString(colors[i].getRGB()));
				putPref(PREFIX + i + "text", getLineDefaults()[i]);
				putPref(PREFIX + i + "font", fonts[i]);

			}
		}
		f = getPref(STUB_PREFIX + "0font", "not-set");
		if (f.equals("not-set")) {
			for (int i = 0; i < NUM_STUB_LINES; i++) {
				putPref(STUB_PREFIX + i + "color", Integer.toString(Color.BLACK.getRGB()));
				putPref(STUB_PREFIX + i + "text", stubLineDefaults[i]);
				putPref(STUB_PREFIX + i + "font", "");

			}
		}
	}

	// due to legacy XML format and JAXB
	private static class Lines {
		public Line[] line = new Line[NUM_LINES];
		public Line[] stubline = new Line[NUM_STUB_LINES];
	}

	/*******************************
	 * End of Statics
	 ********************************/

	// name of the background image file
	private String imageFilename = null;

	// array of Lines to hold the formatting rules for each line of ticket text
	private Lines lines = new Lines();

	private boolean use_stub = false;

	/**
	 * Instantiates a new ticket format.
	 * 
	 * @param layoutType the layout type
	 */
	public TicketFormat(String layoutType) {
		for (int i = 0; i < NUM_LINES; i++) {
			lines.line[i] = null;
		}
		for (int i = 0; i < NUM_STUB_LINES; i++) {
			lines.stubline[i] = null;
		}
	}

	/**
	 * Gets the image filename.
	 * 
	 * @return the image filename
	 */
	public String getImageFilename() {
		return imageFilename;
	}

	/**
	 * Gets formatting of a line by line number.
	 * 
	 * @param num the line number
	 * 
	 * @return the Line object
	 */
	public Line getLine(int num) {
		return lines.line[num];
	}

	public Line getStubLine(int num) {
		return lines.stubline[num];
	}

	/**
	 * Load this instance of TicketFormat with the global defaults
	 */
	public void loadDefault() {

		//setImageFilename(getPref(PrefName.LOGOFILE));

		for (int i = 0; i < NUM_LINES; i++) {
			Line l = new Line();
			l.setText(getPref(PREFIX + i + "text", ""));
			l.setFont(getPref(PREFIX + i + "font", ""));
			int ci = getIntPref(PREFIX + i + "color", 0);
			l.setColor(new Color(ci));
			lines.line[i] = l;
		}

		for (int i = 0; i < NUM_STUB_LINES; i++) {
			Line l = new Line();
			l.setText(getPref(STUB_PREFIX + i + "text", ""));
			l.setFont(getPref(STUB_PREFIX + i + "font", ""));
			int ci = getIntPref(STUB_PREFIX + i + "color", 0);
			l.setColor(new Color(ci));
			lines.stubline[i] = l;
		}

		//use_stub = getBoolPref(USE_STUB);

	}

	/**
	 * Save the contents of this Ticketformat instance to the global defaults
	 */
	public void saveDefault() {

		//putPref(PrefName.LOGOFILE, this.imageFilename);

		for (int i = 0; i < NUM_LINES; i++) {
			Line l = lines.line[i];
			putPref(PREFIX + i + "text", l.getText());
			putPref(PREFIX + i + "font", l.getFont());
			putPref(PREFIX + i + "color", Integer.toString(l.getColor().getRGB()));
		}

		for (int i = 0; i < NUM_STUB_LINES; i++) {
			Line l = lines.stubline[i];
			putPref(STUB_PREFIX + i + "text", l.getText());
			putPref(STUB_PREFIX + i + "font", l.getFont());
			putPref(STUB_PREFIX + i + "color", Integer.toString(l.getColor().getRGB()));
		}

		//putPref(USE_STUB, use_stub ? "true" : "false");

	}

	/**
	 * Sets the image filename.
	 * 
	 * @param imageFilename the new image filename
	 */
	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	/**
	 * Sets the line format for a given line.
	 * 
	 * @param num  the line number
	 * @param line the Line object
	 */
	void setLine(int num, Line line) {
		this.lines.line[num] = line;
	}

	/**
	 * copy constructor
	 * 
	 * @param orig the original
	 */
	public TicketFormat(TicketFormat orig) {
		imageFilename = orig.imageFilename;
		for (int i = 0; i < NUM_LINES; i++) {
			if (orig.lines.line[i] != null) {
				lines.line[i] = new Line();
				lines.line[i].color = new Color(orig.lines.line[i].color.getRGB());
				lines.line[i].text = orig.lines.line[i].text;
				lines.line[i].font = orig.lines.line[i].font;
			}
		}
		for (int i = 0; i < NUM_STUB_LINES; i++) {
			if (orig.lines.stubline[i] != null) {
				lines.stubline[i] = new Line();
				lines.stubline[i].color = new Color(orig.lines.stubline[i].color.getRGB());
				lines.stubline[i].text = orig.lines.stubline[i].text;
				lines.stubline[i].font = orig.lines.stubline[i].font;
			}
		}

		use_stub = orig.use_stub;
	}

	/**
	 * UnMarshall a TicketFormat from XML
	 * 
	 * @param s the XML string
	 * 
	 * @return the ticket format
	 * 
	 * @throws Exception the exception
	 */
	 public TicketFormat fromXml(String s) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(TicketFormat.class);
		Unmarshaller u = jc.createUnmarshaller();
		TicketFormat tf = (TicketFormat) u.unmarshal(new StringReader(s));

		// transition - stub may be missing in db
		if (tf.lines.stubline[0] == null) {

			for (int i = 0; i < NUM_STUB_LINES; i++) {
				Line l = new Line();
				l.setText(getPref(STUB_PREFIX + i + "text", ""));
				l.setFont(getPref(STUB_PREFIX + i + "font", ""));
				int ci = getIntPref(STUB_PREFIX + i + "color", 0);
				l.setColor(new Color(ci));
				tf.lines.stubline[i] = l;
			}
		}
		return tf;
	}

	/**
	 * Marshall a Ticket Format to XML
	 * 
	 * @return the XML string
	 * @throws Exception
	 */
	public String toXml() throws Exception {
		JAXBContext jc = JAXBContext.newInstance(TicketFormat.class);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(this, sw);
		return sw.toString();
	}


	public boolean hasStub() {
		return use_stub;
	}

	public void setHasStub(boolean use_stub) {
		this.use_stub = use_stub;
	}
	
	//
	// stub out the desktop preferences stuff for now
	//
	private HashMap<String,String> prefmap = new HashMap<String,String>();
	private String getPref(String name, String def) {
		String p =  prefmap.get(name);
		if( p == null )
			return def;
		return p;
	}
	
	private int getIntPref(String name, int def) {
		String v = prefmap.get(name);
		if( v == null )
		{
			return def;
		}
		return Integer.parseInt(v);
	}
	
	private void putPref(String name, String val) {
		prefmap.put(name, val);
	}
}
