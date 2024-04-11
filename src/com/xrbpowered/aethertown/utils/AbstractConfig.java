package com.xrbpowered.aethertown.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class AbstractConfig {

	protected final String path;
	private final boolean saveDefault;
	
	public AbstractConfig(String path, boolean saveDefault) {
		this.path = path;
		this.saveDefault = saveDefault;
	}
	
	public AbstractConfig(String path) {
		this(path, false);
	}

	public AbstractConfig() {
		this(null, false);
	}

	public abstract AbstractConfig reset();
	
	public AbstractConfig load() {
		return load(this.path);
	}

	private boolean isConfigProperty(Field fld) {
		int mod = fld.getModifiers();
		return Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
	}
	
	protected boolean applyValues(String title, HashMap<String, String> values) {
		Class<?> cls = this.getClass();
		Field[] fields = cls.getFields();
		for(Field fld : fields) {
			if(!isConfigProperty(fld))
				continue;
			try {
				String name = fld.getName();
				Class<?> type = fld.getType();
				String value = values.get(name);
				if(value!=null) {
					Object obj = checkValue(name, parseValue(name, value, type));
					if(obj!=null)
						fld.set(this, obj);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		System.out.printf("%s loaded.\n", title);
		return true;
	}
	
	public boolean load(String title, InputStream in) {
		try {
			return applyValues(title, loadValues(in));
		}
		catch(IOException e) {
			System.err.printf("Can't load %s. Using default.\n", title);
			return false;
		}
	}

	public AbstractConfig load(String path) {
		HashMap<String, String> values = loadValues(path);
		if(values!=null && applyValues(path, values))
			return this;
		
		AbstractConfig def = reset();
		if(saveDefault)
			def.save(path);
		return def;
	}
	
	public boolean save() {
		return save(this.path);
	}
	
	protected HashMap<String, String> collectValues() {
		HashMap<String, String> values = new LinkedHashMap<>();
		
		Class<?> cls = this.getClass();
		Field[] fields = cls.getFields();
		for(Field fld : fields) {
			if(!isConfigProperty(fld))
				continue;
			try {
				String name = fld.getName();
				String value = formatValue(name, fld.get(this));
				values.put(name, value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return values;
	}
	
	public boolean save(String title, OutputStream out) {
		try {
			saveValues(collectValues(), out);
			System.out.printf("%s saved.\n", title);
			return true;
		}
		catch(IOException e) {
			System.err.printf("Can't save %s\n", title);
			return false;
		}
	}
	
	public boolean save(String path) {
		if(saveValues(collectValues(), path)) {
			System.out.printf("%s saved.\n", path);
			return true;
		}
		else
			return false;
	}

	public static HashMap<String, String> loadValues(InputStream in) throws IOException {
		HashMap<String, String> values = new HashMap<>();
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(in);
		while(scan.hasNextLine()) {
			String[] s = scan.nextLine().trim().split("\\s*=\\s*", 2);
			if(s.length==2)
				values.put(s[0], s[1]);
		}
		return values;
	}

	public static HashMap<String, String> loadValues(String path) {
		try(InputStream in = AssetManager.defaultAssets.openStream(path)) {
			return loadValues(in);
		}
		catch(Exception e) {
			System.err.printf("Can't load %s. Using default.\n", path);
			return null;
		}
	}

	public static void saveValues(HashMap<String, String> values, OutputStream out) throws IOException {
		PrintStream print = new PrintStream(out);
		for(Entry<String, String> entry : values.entrySet()) {
			String key = entry.getKey();
			String v = entry.getValue();
			if(v.contains("\n"))
				System.err.printf("Warning: can't save multiline value for %s, skipping\n", key);
			else
				print.printf("%s=%s\n", key, v);
		}
	}

	public static boolean saveValues(HashMap<String, String> values, String path) {
		File file = new File(path);
		File dir = file.getParentFile();
		if(!dir.isDirectory() && !dir.mkdir()) {
			System.err.printf("Can't create save directory for %s\n", path);
			return false;
		}
		
		try(OutputStream out = new FileOutputStream(file)) {
			saveValues(values, out);
			return true;
		}
		catch(Exception e) {
			System.err.printf("Can't save %s\n", path);
			return false;
		}
	}

	protected Object parseValue(String name, String value, Class<?> type) {
		try {
			if(type==String.class)
				return value;
			else if(type==int.class)
				return Integer.parseInt(value);
			else if(type==long.class) {
				if(value.endsWith("L"))
					value = value.substring(0, value.length()-1);
				return Long.parseLong(value);
			}
			else if(type==boolean.class) {
				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
					return true;
				else if(value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
					return false;
				else
					return null;
			}
			else if(type==float.class) {
				if(value.endsWith("f"))
					value = value.substring(0, value.length()-1);
				return Float.parseFloat(value);
			}
			else if(type==double.class)
				return Double.parseDouble(value);
			else
				return null;
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	protected Object checkValue(String name, Object obj) {
		return obj;
	}
	
	protected String formatValue(String name, Object obj) {
		if(obj instanceof Long)
			return obj.toString()+"L";
		else if(obj instanceof Float)
			return String.format("%.3f", obj);
		else if(obj instanceof Double)
			return String.format("%.6f", obj);
		else
			return obj.toString();
	}
	
}
