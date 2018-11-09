package com.kNoAPP.DevShortcuts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public abstract class DataHandler {
	
	public static JSON ACTIONS_JSON = new JSON("/", "/actions.json");
	
	protected String subtree, filename, outerPath;
	protected File file;
	protected boolean wasCreated;
	
	public DataHandler(String filename) {
		this("", filename);
	}
	
	public DataHandler(String subtree, String filename) {
		this.subtree = subtree;
		this.filename = filename;
		this.file = new File(System.getProperty("user.dir") + subtree, filename);
		this.outerPath = file.getAbsolutePath();
		
		if(wasCreated = !file.exists()) {
			try {
				file.getParentFile().mkdirs();
				exportResource();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean wasCreated() {
		return wasCreated;
	}
	
	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 *
	 * @param resourcePath
	 *            ie.: "SmartLibrary.dll"
	 *            ie.: "data/SmartLibrary.dll"
	 * @throws Exception
	 */
	private void exportResource() throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;

		try {
			stream = DataHandler.class.getResourceAsStream(filename);
			if(stream == null) throw new Exception("Cannot get resource \"" + filename + "\" from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(outerPath);
			while((readBytes = stream.read(buffer)) > 0) resStreamOut.write(buffer, 0, readBytes);
		} catch (Exception ex) { 
			throw ex;
		} finally {
			if(stream != null) stream.close();
			if(resStreamOut != null) resStreamOut.close();
		}
	}
	
	public static class JSON extends DataHandler {
		
		private Object cached;
		
		public JSON(String filename) {
			super(filename);
		}
		
		public JSON(String subtree, String filename) {
			super(subtree, filename);
		}
		
		public Object getJSON() {
			JSONParser parser = new JSONParser();
			try {
				cached = parser.parse(new FileReader(outerPath));
			} catch (Exception e) { e.printStackTrace(); }
			return cached;
		}
		
		public Object getCachedJSON() {
			if(cached == null) return getJSON();
			return cached;
		}
		
		public void saveJSON(JSONObject obj) {
			try(FileWriter fw = new FileWriter(outerPath)) {
				fw.write(obj.toJSONString());
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		public void saveJSON(JSONArray obj) {
			try(FileWriter fw = new FileWriter(outerPath)) {
				fw.write(obj.toJSONString());
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public static class PROPS extends DataHandler {
		
		private Properties cached = new Properties();
		
		public PROPS(String filename) {
			super(filename);
		}
		
		public PROPS(String subtree, String filename) {
			super(subtree, filename);
		}
		
		public Properties getProperties() {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				cached.load(is);
			} catch(Exception e) { is = null; }
			return cached;
		}
		
		public Properties getCachedProperties() {
			if(cached.isEmpty()) return getProperties();
			return cached;
		}
		
		public void saveProperties(Properties props) {
			try {
				OutputStream out = new FileOutputStream(file);
				props.store(out, null);
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}