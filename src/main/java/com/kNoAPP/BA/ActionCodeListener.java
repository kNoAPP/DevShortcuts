package com.kNoAPP.BA;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ActionCodeListener implements NativeKeyListener {
	
	private volatile boolean valid = true;
	private List<Action> actions = new ArrayList<Action>();
	private String typedChars = "";
	
	public ActionCodeListener(File json) {
		JSONParser parser = new JSONParser();
		try {
			JSONArray objs = (JSONArray) parser.parse(new FileReader(json));
			for(Object obj : objs) {
				JSONObject jobj = (JSONObject) obj;
				actions.add(new Action((String) jobj.get("key"), (String) jobj.get("execute")));
			}
		} catch (Exception e) { 
			e.printStackTrace();
			close();
		}
	}
	
	private void check() {
		for(Action a : actions) {
			if(a.getKey() != null && a.getExecutable() != null && typedChars.contains(a.getKey())) {
				if(a.getExecutable().equals("exit")) close();
				else {
					try {
						Runtime.getRuntime().exec("cmd.exe /c start \"\" \"" + a.getExecutable() + "\"");
					} catch (IOException e) { e.printStackTrace(); }
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
	
	private static class Action {
		
		private String key, execute;
		
		public Action(String key, String execute) {
			this.key = key;
			this.execute = execute;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getExecutable() {
			return execute;
		}
	}
}
