package com.azwave.androidzwave.zwave.utils;

import java.io.FileInputStream;
import java.io.InputStream;

import org.w3c.dom.Document;

public class XMLManagerSon extends XMLManager {

	public XMLManagerSon(Log log) {
		super(log);
	}

	protected Document readAsset(String filename) throws Exception {
		InputStream inputStream = new FileInputStream(path + filename);
		db = dbf.newDocumentBuilder();
		return db.parse(inputStream);
	}

	public void readDeviceClasses() {
		readDeviceClasses("device_classes.xml");
	}

	public void readManufacturerSpecific() {
		readManufacturerSpecific("manufacturer_specific.xml");
	}
}
