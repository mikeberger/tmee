package com.mbcsoft.ticketmaven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.mbcsoft.ticketmaven.ejb.CustomerBean;
import com.mbcsoft.ticketmaven.ejb.ShowBean;
import com.mbcsoft.ticketmaven.entity.Customer;
import com.mbcsoft.ticketmaven.entity.Instance;
import com.mbcsoft.ticketmaven.entity.Show;


@WebServlet("/export")
public class ExportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@XmlRootElement(name = "DBExport")
	@SuppressWarnings("unused")
	private static class XmlContainer {
		public Instance instance;
		public Collection<Customer> customer;
		public Collection<Show> show;
	}

	@EJB private CustomerBean cbean;
	@EJB private ShowBean sbean;
	static private final Logger logger = Logger.getLogger(ExportServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		try {
			Instance inst = cbean.getCurrentCustomer().getInstance();
			logger.info("Export for community = " + inst.getName());
			

			ServletOutputStream outputstream = response.getOutputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream);	
			Writer fw = new OutputStreamWriter(out, "UTF8");
			
			out.putNextEntry(new ZipEntry("export.xml"));
			JAXBContext jc = JAXBContext.newInstance(XmlContainer.class);
			Marshaller marshaller = jc.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        XmlContainer cc = new XmlContainer();
	        cc.instance = inst;
	        cc.customer = cbean.getAll();
	        cc.show = sbean.getAll();
	        
		    marshaller.marshal(cc, out);
		    fw.flush();
			out.closeEntry();
			out.close();
			
			response.setContentType("application/zip");
			outputstream.write(byteArrayOutputStream.toByteArray());
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Disposition", "attachment; filename=" + "export.zip");
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
