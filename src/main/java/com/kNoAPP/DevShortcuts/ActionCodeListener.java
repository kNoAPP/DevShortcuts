package com.kNoAPP.DevShortcuts;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kNoAPP.DevShortcuts.DataHandler.JSON;

public class ActionCodeListener implements NativeKeyListener {
	
	private volatile boolean valid = true;
	private boolean block = false;
	private List<Action> actions = new ArrayList<Action>();
	private String typedChars = "";
	
	public ActionCodeListener(JSON json) {
		JSONArray objs = (JSONArray) json.getCachedJSON();
		for(Object obj : objs) {
			JSONObject jobj = (JSONObject) obj;
			
			String key = (String) jobj.get("key");
			String executable = (String) jobj.get("executable");
			String shortcut = (String) jobj.get("shortcut");
			int fallback = jobj.get("fallback") != null ? Math.toIntExact((long) jobj.get("fallback")) : 0;
			boolean exit = jobj.get("exit") != null ? (boolean) jobj.get("exit") : false;
			
			if(key != null) {
				if(executable != null) actions.add(new Action(key, executable));
				else if(shortcut != null) actions.add(new Action(key, shortcut, fallback));
				else if(exit) actions.add(new Action(key));
				System.out.println("");
			}
		}
	}
	
	private void check() {
		for(Action a : actions) {
			if(typedChars.contains(a.getKey())) {
				switch(a.getActionType()) {
				case Action.EXIT:
					close();
					break;
				case Action.EXECUTABLE:
					try {
						Runtime.getRuntime().exec("cmd.exe /c start \"\" \"" + a.getExecutable() + "\"");
					} catch (IOException e) { e.printStackTrace(); }
					break;
				case Action.SHORTCUT:
					try {
						Robot robot = new Robot();
						block = true;
						for(int i=0; i<a.getKey().length(); i++) {
							robot.keyPress(KeyEvent.VK_BACK_SPACE);
							robot.keyRelease(KeyEvent.VK_BACK_SPACE);
						}
						new Keyboard().type(a.getShortcut());
						for(int i=0; i<a.getFallback(); i++) {
							robot.keyPress(KeyEvent.VK_LEFT);
							robot.keyRelease(KeyEvent.VK_LEFT);
						}
						block = false;
					} catch (AWTException e) { e.printStackTrace(); }
					break;
				default:
					break;
				}
				
				typedChars = typedChars.replaceFirst("\\Q" + a.getKey() + "\\E", "");
			}
		}
		
		System.out.println(typedChars);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		//Not Used
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		//Not Used
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		if(block) return;
		
		typedChars += e.getKeyChar();
		Runnable clear = () -> {
			check();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ex) { ex.printStackTrace(); }
			if(typedChars.length() > 0) typedChars = typedChars.substring(1);
		};
		
		new Thread(clear).start();
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void close() {
		valid = false;
		GlobalScreen.removeNativeKeyListener(this);
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) { e.printStackTrace(); }
		
		System.exit(0);
	}
	
	public static class Action {
		
		public static final int EXIT = 0;
		public static final int EXECUTABLE = 1;
		public static final int SHORTCUT = 2;
		
		private String key;
		private int actionType;
		private String executable, shortcut;
		private int fallback;
		
		public Action(String key) {
			this.key = key;
			actionType = EXIT;
		}
		
		public Action(String key, String executable) {
			actionType = EXECUTABLE;
			this.key = key;
			this.executable = executable;
		}
		
		public Action(String key, String shortcut, int fallback) {
			actionType = SHORTCUT;
			this.key = key;
			this.shortcut = shortcut;
			this.fallback = fallback;
		}
		
		public int getActionType() {
			return actionType;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}
		
		public String getExecutable() {
			return executable;
		}
		
		public void setExecutable(String executable) {
			this.executable = executable;
		}
		
		public String getShortcut() {
			return shortcut;
		}
		
		public void setShortcut(String shortcut) {
			this.shortcut = shortcut;
		}
		
		public int getFallback() {
			return fallback;
		}
		
		public void setFallback(int fallback) {
			this.fallback = fallback;
		}
	}
}
