package com.kNoAPP.BA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

public class BarcodeActions {

	public static void main(String[] args) {
		File actions = new File(getLocation(), "actions.json");
		if(!actions.exists()) {
			try {
				exportResource("/actions.json");
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
		
		GlobalScreen.addNativeKeyListener(new ActionCodeListener(actions));
	}
	
	private static String getLocation() {
		try {
			return new File(BarcodeActions.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
		} catch (URISyntaxException e) { e.printStackTrace(); }
		return null;
	}
	
	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 *
	 * @param resourceName
	 *            ie.: "/SmartLibrary.dll"
	 * @return The path to the exported resource
	 * @throws Exception
	 */
	public static String exportResource(String resourceName) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			stream = BarcodeActions.class.getResourceAsStream(resourceName);
			if(stream == null) throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = getLocation();
			resStreamOut = new FileOutputStream(jarFolder + resourceName);
			while((readBytes = stream.read(buffer)) > 0) resStreamOut.write(buffer, 0, readBytes);
		} catch (Exception ex) { 
			throw ex;
		} finally {
			stream.close();
			resStreamOut.close();
		}

		return jarFolder + resourceName;
	}
}
