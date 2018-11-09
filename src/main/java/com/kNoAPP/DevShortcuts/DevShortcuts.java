package com.kNoAPP.DevShortcuts;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

public class DevShortcuts {

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
		
		GlobalScreen.addNativeKeyListener(new ActionCodeListener(DataHandler.ACTIONS_JSON));
	}
}
