/*
 *	Copyright (c) 2009
 *    Michael Berger
 *	All Rights Reserved
 *
 *	PROPRIETARY 
 *   This Software contains proprietary information that shall not be 
 *    in the possession of, distributed to, or routed to anyone except with written permission of Michael Berger.
 */
package com.mbcsoft.ticketmaven.ticketprint;

import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Seat;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.entity.Ticket;

public class TicketPrinter {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	public static void printShow(Show s) throws Exception {

			System.setProperty("java.awt.headless", "true"); 

			Collection<Ticket> tickets = s.getTicketsCollection();
			int _numberOfPages = (tickets.size() / 10) + 1;

			if( tickets.isEmpty()) return;
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

			float w = (float)pf.getWidth();
			float h = (float)pf.getHeight();
			Document document = new Document(new Rectangle(w,h));

	        PdfWriter writer = PdfWriter.getInstance(document,
	                new FileOutputStream("/tmp/tickets.pdf"));
	        document.open();

	        PdfContentByte cb = writer.getDirectContent();

	        for (int currentPage= 0; currentPage < _numberOfPages; ++currentPage) {
	        	document.newPage(); // not needed for page 1, needed for >1

	            PdfTemplate template= cb.createTemplate(w, h);
	            Graphics2D g2d= new PdfGraphics2D(template, w, h );
	        	tp.print(g2d,pf,currentPage);
	            g2d.dispose();

	            cb.addTemplate(template, 0, 0);
	        }

	        document.close();
	}
	
	static public void main(String args[]) throws Exception {
		
		
		Show sh = new Show();
		sh.setCost(1);
		sh.setName("my show");
		sh.setPrice(2);
		sh.setTime(new Timestamp(System.currentTimeMillis()));
		Set<Ticket> tkts = new HashSet<Ticket>();

		for( int i = 0; i < 25; i++)
		{
			Customer c = new Customer();
			c.setFirstName("mike");
			c.setLastName("b" + i);
			Seat s = new Seat();
			s.setLabel("" + i);
			s.setRow("A");
			s.setSeat(i);
			
			Ticket t = new Ticket();
			t.setSeat(s);
			t.setCustomer(c);
			t.setShow(sh);

			tkts.add(t);

		}
		sh.setTicketsCollection(tkts);
		
		new TicketPrinter().printShow(sh);
	}

}
