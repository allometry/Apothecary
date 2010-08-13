import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AttributeList;

@ScriptManifest(authors = { "Allometry" }, category = "!Herblore", name = "Apothecary", version = 1.0,
		description = "" +
				"<html>" +
				"<head>" +
				"<style type=\"text/css\">" +
				"body {background: #000 url(http://scripts.allometry.com/app/webroot/img/gui/window.jpg);" +
				"font-family: Georgia, 'Times New Roman', Times, serif;" +
				"font-size: 12px;font-weight: normal;" +
				"padding: 50px 10px 45px 10px;}" +
				"</style>" +
				"</head>" +
				"<body>" +
				"<p>Allometry Humidifier</p>" +
				"<p>Supports Fire, Water and Steam staffs</p>" +
				"<p>Astrals in inventory. Empty vials visible in bank!</p>" +
				"<p>For more info, visit the" +
				"thread on the RuneDev forums!</p>" +
				"</body>" +
				"</html>")
public class Apothecary extends Script implements PaintListener {
	
	@Override
	public boolean onStart(Map<String,String> args) {
		try {
			PotionLoader potionLoader = new PotionLoader(new URL("http://scripts.allometry.com/app/webroot/xml/apothecary.xml"));
			potionLoader.loadPotions();
			
			ArrayList<Potion> potions = potionLoader.getPotions();
			for (Potion potion : potions) {
				log(potion.toString());
			}
		} catch (MalformedURLException e) {
			log.warning(e.getLocalizedMessage());
		} catch (SAXException e) {
			log.warning(e.getLocalizedMessage());
		} catch (IOException e) {
			log.warning(e.getLocalizedMessage());
		}
		
		log.info("Loaded succesfully!");
		
		return false;
	}
	
	@Override
	public int loop() {
		return 1;
	}
		
	@Override
	public void onFinish() {
		return ;
	}
	
	@Override
	public void onRepaint(Graphics g2) {
		return ;
	}
		
	public class Potion {
		private int primaryIngredient, secondaryIngredient;
		private String name;
		
		public int getPrimaryIngredient() {
			return primaryIngredient;
		}
		
		public void setPrimaryIngredient(int primaryIngredient) {
			this.primaryIngredient = primaryIngredient;
		}
		
		public int getSecondaryIngredient() {
			return secondaryIngredient;
		}
		
		public void setSecondaryIngredient(int secondaryIngredient) {
			this.secondaryIngredient = secondaryIngredient;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String toString() {
			return "[Name: " + name + ", Primary Ingredient: " + primaryIngredient + ", Secondary Ingredient: " + secondaryIngredient + "]";
		}
	}
	
	public class Potions {
		private ArrayList<Potion> potions = new ArrayList<Potion>();

		public void addPotion(Potion potion) {
			potions.add(potion);
		}
	}
	
	public class PotionHandler extends DefaultHandler {
		private ArrayList<Potion> potions = new ArrayList<Potion>();
		private Potion potion;
		private String potionElement = "";
		
		public PotionHandler() {
			super();
		}
		
		public void startElement(String uri, String name, String qName, Attributes atts) {
			if(name.equalsIgnoreCase("potion"))
				potion = new Potion();
			else
				potionElement = name;
		}
		
		public void characters(char chars[], int start, int length) {
			String elementValue = new String(chars, start, length).trim();
			if(elementValue.trim().equals("")) return ;
			
			if(potionElement.equalsIgnoreCase("name"))
				potion.setName(elementValue);
			else if(potionElement.equalsIgnoreCase("primaryIngredient"))
				potion.setPrimaryIngredient(Integer.parseInt(elementValue));
			else if(potionElement.equalsIgnoreCase("secondaryIngredient"))
				potion.setSecondaryIngredient(Integer.parseInt(elementValue));
		}
		
		public void endElement(String uri, String name, String qName) {
			if(name.equalsIgnoreCase("potion")) {
				potions.add(potion);
				potion = null;
			}
		}
		
		public ArrayList<Potion> getPotions() {
			return potions;
		}
	}
	
	public class PotionLoader {
		private PotionHandler potionHandler;
		private URL xmlLocation;
		private XMLReader xmlReader;
		
		public PotionLoader(URL xmlLocation) {
			this.xmlLocation = xmlLocation;
		}
		
		public void loadPotions() throws SAXException, IOException {
			potionHandler = new PotionHandler();
			
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(potionHandler);
			xmlReader.setErrorHandler(potionHandler);
			xmlReader.parse(new InputSource(new InputStreamReader(xmlLocation.openStream())));
		}
		
		public ArrayList<Potion> getPotions() {
			return potionHandler.getPotions();
		}
	}
}
