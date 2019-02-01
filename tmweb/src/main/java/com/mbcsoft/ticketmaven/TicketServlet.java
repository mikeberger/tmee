package com.mbcsoft.ticketmaven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Show;
import com.mbcsoft.ticketmaven.ticketprint.TicketPrinter;


@WebServlet("/tickets")
public class TicketServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB private ShowBean rbean;
	static private final Logger logger = Logger.getLogger(TicketServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TicketServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String id = request.getParameter("id");

		
		try {
			logger.info("Ticket Print show = " + id);
			Show s = rbean.getFullShow(id);
			if( s == null ) return;
			int instance = rbean.getCurrentCustomer().getInstance().getRecordId();
			if( s.getInstance().getRecordId() != instance) {
				return;
			}

			ServletOutputStream outputstream = response.getOutputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new TicketPrinter().printShow(s, byteArrayOutputStream);
			response.setContentType("application/pdf");
			outputstream.write(byteArrayOutputStream.toByteArray());
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Disposition", "attachment; filename=" + "tickets.pdf");
			outputstream.flush();
			outputstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
