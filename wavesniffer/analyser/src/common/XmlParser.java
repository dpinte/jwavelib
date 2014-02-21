package common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import frame.FrameAnalyser;
import frame.FrameRelation;

public class XmlParser {
	private File file;
	private DocumentBuilder db;
	
	public XmlParser(String fileName){
		this.file = new File(fileName);
		try {
			this.db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buildRelation(){
		Element root = null;
		NodeList nodes, childNodes = null;
		Node node, childNode = null;
		NamedNodeMap atr, childAtr = null;
		HashMap <Integer, FrameRelation> map = new HashMap <Integer, FrameRelation> ();
		FrameRelation rel = null;
		
		try {
			Document doc = this.db.parse(this.file);
			root = doc.getDocumentElement();
			
			nodes = root.getElementsByTagName("frame");
			
			for(int i = 0; i < nodes.getLength(); i++){
				node = nodes.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE){
					//System.out.print(node.getNodeName() +" - ");
					if(node.hasAttributes()){
						atr = node.getAttributes();
						rel = new FrameRelation(Integer.parseInt(atr.getNamedItem("cmd").getNodeValue().substring(2), 16));
						
						//System.out.print("cmd: "+ atr.getNamedItem("cmd").getNodeValue());
					}
					
					//System.out.print("\n");
					
					if(node.hasChildNodes()){
						childNodes = ((Element)node).getElementsByTagName("res");
						
						for(int j = 0; j < childNodes.getLength(); j++){
							childNode = childNodes.item(j);
							
							if(childNode.getNodeType() == Node.ELEMENT_NODE){
								//System.out.print("    "+ childNode.getNodeName() +" - ");
								
								if(childNode.hasAttributes()){
									childAtr = childNode.getAttributes();
									rel.addRelation(Integer.parseInt(childAtr.getNamedItem("cmd").getNodeValue().substring(2), 16),
													0,
													Integer.parseInt(childAtr.getNamedItem("level").getNodeValue()));
									
									//System.out.print("cmd: "+ childAtr.getNamedItem("cmd").getNodeValue());
									//System.out.print(", level: "+ childAtr.getNamedItem("level").getNodeValue());
								}
								
								//System.out.print("\n");
								//System.out.println("        parent - "+ ((Element)childNode).getElementsByTagName("parent").item(0).getTextContent());
							}
						}
					}
				}
				map.put(rel.getCmd(), rel);
				//System.out.print("\n");
			}
			FrameAnalyser.relation = map;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}