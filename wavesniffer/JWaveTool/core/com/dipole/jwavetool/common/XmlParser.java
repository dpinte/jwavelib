package com.dipole.jwavetool.common;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.dipole.jwavetool.frame.Description;
import com.dipole.jwavetool.frame.FrameRelation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {
	private DocumentBuilder db;
	
	public XmlParser(){
		try {
			this.db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.fatal(e.getMessage());
		}
	}
	
	public void buildCmdRelation(){
		Log.trace("Enter buildRelation");
		
		Element root = null;
		NodeList nodes = null;
		NodeList childNodes = null;
		Node node = null;
		Node childNode = null;
		NamedNodeMap atr = null;
		NamedNodeMap childAtr = null;
		HashMap <Integer, FrameRelation> map = new HashMap <Integer, FrameRelation> ();
		FrameRelation rel = null;
		
		try {
			Document doc = this.db.parse(getClass().getResourceAsStream(Common.XML_PATH +"cmdRelation.xml"));
			root = doc.getDocumentElement();
			
			nodes = root.getElementsByTagName("frame");
			Log.debug("total nodes: "+ nodes.getLength());
			
			for(int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Log.debug("found node: "+ node.getNodeName());
					
					if(node.hasAttributes()) {
						atr = node.getAttributes();
						rel = new FrameRelation(Integer.parseInt(atr.getNamedItem("cmd").getNodeValue().substring(2), 16));
					}
					
					if(node.hasChildNodes()){
						childNodes = ((Element)node).getElementsByTagName("res");
						Log.debug("found "+ childNodes.getLength() +"childs");
						
						for(int j = 0; j < childNodes.getLength(); j++) {
							childNode = childNodes.item(j);
							
							if((childNode.getNodeType() == Node.ELEMENT_NODE) && (childNode.hasAttributes())) {
								childAtr = childNode.getAttributes();
								rel.addRelation(Integer.parseInt(childAtr.getNamedItem("cmd").getNodeValue().substring(2), 16),
												0, Integer.parseInt(childAtr.getNamedItem("level").getNodeValue()));
							}
						}
					}
				}
				map.put(rel.getCmd(), rel);
			}
			Common.cmdRelation = map;
		} catch (SAXException e) {
			Log.fatal(e.getMessage());
		} catch (IOException e) {
			Log.fatal(e.getMessage());
		}
		
		Log.trace("Quit buildRelation\n");
	}
	
	//FIXME: merge buildCmdDescrition to buildParamDescription
	
	public void buildCmdDescription() {
		Log.trace("Enter buildCmdDescription");
		
		Document doc = null;
		Element root = null;
		NodeList nodes = null;
		NodeList childNodes = null;
		Node node  = null;
		Node childNode = null;
		NamedNodeMap atr = null;
		HashMap <Integer, Description> map = new HashMap <Integer, Description> ();
		Description desc = null;
		String tmpStr;
		
		try {
			doc = this.db.parse(this.getClass().getResourceAsStream(Common.XML_PATH +"cmdDescription.xml"));
			root = doc.getDocumentElement();
			
			nodes = root.getElementsByTagName("cmd");
			Log.debug("total nodes: "+ nodes.getLength());
			
			for(int i = 0; i < nodes.getLength(); i++) {
				desc = new Description();
				node = nodes.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Log.debug("found node: "+ node.getNodeName());
					
					if(node.hasAttributes()){
						atr = node.getAttributes();
						tmpStr = atr.getNamedItem("val").getNodeValue().split("\"")[0];
						
						Log.debug("    val: "+ tmpStr);
						
						/* need to remove the 0x form the string*/
						desc.setValue(Integer.valueOf(tmpStr.substring(2), 16));
					}
					
					if(node.hasChildNodes()){
						
						childNodes = ((Element)node).getElementsByTagName("*");
						Log.debug("found "+ childNodes.getLength() +"childs");
					
						for(int j = 0; j < childNodes.getLength(); j++){
							childNode = childNodes.item(j);
							
							if(childNode.getNodeType() == Node.ELEMENT_NODE) {
								
								if(childNode.getNodeName().equals("name")) {
									tmpStr = childNode.getTextContent();
									desc.setName(tmpStr);
									Log.debug("    Name: "+ tmpStr);
									
								} else if (childNode.getNodeName().equals("desc")) {
									tmpStr = childNode.getTextContent();
									desc.setDescription(tmpStr);
									Log.debug("    desc: "+ tmpStr);
								}
							} 
						}							
					}
				}
				map.put(desc.getValue(), desc);
			}
			Common.cmdDescription = map;
		} catch (SAXException e) {
			Log.fatal(e.getMessage());
		} catch (IOException e) {
			Log.fatal(e.getMessage());
		}
		
		Log.trace("Quit buidCmdDescription\n");
	}
	
	public void buildParamDescription() {
		Log.trace("Enter buildParamDescription");
		
		Document doc = null;
		Element root = null;
		NodeList nodes = null;
		NodeList childNodes = null;
		Node node  = null;
		Node childNode = null;
		NamedNodeMap atr = null;
		HashMap <Integer, Description> map = new HashMap <Integer, Description> ();
		Description desc = null;
		String tmpStr;
		
		try {
			doc = this.db.parse(this.getClass().getResourceAsStream(Common.XML_PATH +"paramDescription.xml"));
			root = doc.getDocumentElement();
			
			nodes = root.getElementsByTagName("param");
			Log.debug("total nodes: "+ nodes.getLength());
			
			for(int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					desc = new Description();

					Log.debug("found node: "+ node.getNodeName());
					if(node.hasAttributes()){
						atr = node.getAttributes();
						tmpStr = atr.getNamedItem("val").getNodeValue().split("\"")[0];
						
						Log.debug("    val: "+ tmpStr);
						
						/* need to remove the 0x form the string*/
						desc.setValue(Integer.valueOf(tmpStr.substring(2), 16));
					}
					
					if(node.hasChildNodes()) {
						
						childNodes = ((Element)node).getElementsByTagName("*");
						Log.debug("found "+ childNodes.getLength() +"childs");
					
						for(int j = 0; j < childNodes.getLength(); j++){
							childNode = childNodes.item(j);
							
							if(childNode.getNodeType() == Node.ELEMENT_NODE) {
								
								if(childNode.getNodeName().equals("name")) {
									tmpStr = childNode.getTextContent();
									desc.setName(tmpStr);
									Log.debug("    Name: "+ tmpStr);
									
								} else if (childNode.getNodeName().equals("desc")) {
									tmpStr = childNode.getTextContent();
									desc.setDescription(tmpStr);
									Log.debug("    desc: "+ tmpStr);
								}
							} 
						}							
					}
				}
				map.put(desc.getValue(), desc);
			}
			Common.paramDescription = map;
		} catch (SAXException e) {
			Log.fatal(e.getMessage());
		} catch (IOException e) {
			Log.fatal(e.getMessage());
		}
		
		Log.trace("Quit buildParamDescription\n");
	}
}