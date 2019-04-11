package com.mbcsoft.ticketmaven.entity;

import java.sql.Timestamp;
import java.util.logging.Logger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimeDateAdapter extends XmlAdapter<String, Timestamp>{

	static private final Logger logger = Logger.getLogger(TimeDateAdapter.class.getName());

    @Override
    public String marshal(Timestamp v) throws Exception {
    	logger.info("marshall-" + v );
        return Long.toString(v.getTime());
    }

    @Override
    public Timestamp unmarshal(String v) throws Exception {
    	logger.info("um" + v + ":" + Long.parseLong(v));
        return new Timestamp(Long.parseLong(v));
    }

}