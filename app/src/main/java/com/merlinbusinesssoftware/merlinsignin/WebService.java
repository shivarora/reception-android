package com.merlinbusinesssoftware.merlinsignin;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WebService {
	private static final String SOAP_ACTION_GETSQL = "CCBS/GetSql";
	private static final String METHOD_NAME_GETSQL = "GetSql";
	private static final String NAMESPACE = "CCBS";
	private static final String SOAP_ACTION_CHECKSTATUS = "CCBS/CheckStatus";
	private static final String METHOD_NAME_CHECKSTATUS = "CheckStatus";

	public NodeList runSQL(String URL, String DSN, String query) {

		NodeList nodeLst = null;

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETSQL);

			request.addProperty("datasource", DSN);
			request.addProperty("query", query);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			// 2.5.7
			AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
					URL);
			androidHttpTransport.debug = true;
			androidHttpTransport.call(SOAP_ACTION_GETSQL, envelope);

			envelope.getResponse();

			try {
				String rd = androidHttpTransport.responseDump;

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(rd));

				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();

				nodeLst = doc.getElementsByTagName("row");

			} catch (Exception e) {
				System.out.println("in 1st excep");
				System.out.println(e);
			}

		} catch (Exception aE) {
			System.out.println("in 2nd excep");
			System.out.println(aE);
			aE.printStackTrace();
		}

		return nodeLst;
	}

	public String GetNode(Element fstElement, String tagName) {
		NodeList fstNmElmntLst = fstElement.getElementsByTagName(tagName);
		Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
		NodeList fstNm = fstNmElmnt.getChildNodes();
		if (fstNm.item(0) != null) {
			return fstNm.item(0).getNodeValue();
		} else {
			return "";
		}
	}

	public String checkStatus(String URL, String DSN) {

		String status;

		status = "";

		try {
			SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME_CHECKSTATUS);

			request.addProperty("datasource", DSN);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			// 2.5.7
			AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
					URL);
			androidHttpTransport.debug = true;
			androidHttpTransport.call(SOAP_ACTION_CHECKSTATUS, envelope);

			try {
				String rd = androidHttpTransport.responseDump;

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(rd));

				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();

				NodeList nodeLst = doc.getElementsByTagName("status");

				for (int s = 0; s < nodeLst.getLength(); s++) {

					Node fstNode = nodeLst.item(s);

					if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

						Element fstElmnt = (Element) fstNode;

						String code = GetNode(fstElmnt, "code");

						status = code;

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception aE) {
			aE.printStackTrace();
		}

		return status;
	}
}
