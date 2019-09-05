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
package com.mbcsoft.ticketmaven.ticketprint;

import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

public class TicketPrinter {

	@SuppressWarnings("unused")
	private final long serialVersionUID = 1L;

	private class TicketComp implements Comparator<Ticket> {

		@Override
		public int compare(Ticket o1, Ticket o2) {
			return o1.getCustomerName().compareTo(o2.getCustomerName());
		}

	}

	public void printShow(Show s, ByteArrayOutputStream os) throws Exception {

		System.setProperty("java.awt.headless", "true");

		Collection<Ticket> tset = s.getTicketsCollection();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets.addAll(tset);
		Collections.sort(tickets, new TicketComp());
		int _numberOfPages = (tickets.size() / 10) + 1;

		if (tickets.isEmpty())
			return;
		TicketPanel tp = new TicketPanel(tickets);
		TicketFormat tf = new TicketFormat();
		tf.loadDefault();
		tp.setDefaultFormat(tf);

		PageFormat pf = new PageFormat();
		Paper paper = new Paper();
		paper.setSize(8.5 * 72, 11 * 72);
		paper.setImageableArea(0.875 * 72, 0.625 * 72, 6.75 * 72, 9.75 * 72);
		pf.setPaper(paper);
		tp.setPages(_numberOfPages);
		pf.setOrientation(PageFormat.PORTRAIT);

		float w = (float) pf.getWidth();
		float h = (float) pf.getHeight();
		Document document = new Document(new Rectangle(w, h));

		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();

		PdfContentByte cb = writer.getDirectContent();

		for (int currentPage = 0; currentPage < _numberOfPages; ++currentPage) {
			document.newPage(); // not needed for page 1, needed for >1

			PdfTemplate template = cb.createTemplate(w, h);
			Graphics2D g2d = new PdfGraphics2D(template, w, h);
			tp.print(g2d, pf, currentPage);
			g2d.dispose();

			cb.addTemplate(template, 0, 0);
		}

		document.close();
	}

	/*
	 * static public void main(String args[]) throws Exception {
	 * 
	 * 
	 * Show sh = new Show(); sh.setCost(1); sh.setName("my show"); sh.setPrice(2);
	 * sh.setTime(new Timestamp(System.currentTimeMillis())); Set<Ticket> tkts = new
	 * HashSet<Ticket>();
	 * 
	 * 
	 * for( int i = 0; i < 25; i++) { Customer c = new Customer();
	 * c.setFirstName("mike"); c.setLastName("b" + i); Seat s = new Seat();
	 * s.setLabel("" + i); s.setRow("A"); s.setSeat(i);
	 * 
	 * Ticket t = new Ticket(); t.setSeat(s); t.setCustomer(c); t.setShow(sh);
	 * 
	 * tkts.add(t);
	 * 
	 * } sh.setTicketsCollection(tkts);
	 * 
	 * new TicketPrinter().printShow(sh); }
	 */

}
