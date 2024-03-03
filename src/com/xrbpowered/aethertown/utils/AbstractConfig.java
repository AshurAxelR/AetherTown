package com.xrbpowered.aethertown.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class AbstractConfig {

	protected final String path;
	
	public AbstractConfig(String path) {
		this.path = path;
	}

	public AbstractConfig load() {
		return load(this.path);
	}

	private boolean isConfigProperty(Field fld) {
		int mod = fld.getModifiers();
		return Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
	}
	
	public AbstractConfig load(String path) {
		HashMap<String, String> values = loadValues(path);
		if(values==null)
			return this;
		
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
			}
		}
		System.out.printf("%s loaded.\n", path);
		return this;
	}
	
	public void save() {
		save(this.path);
	}
	
	public void save(String path) {
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
		if(saveValues(values, path))
			System.out.printf("%s saved.\n", path);
	}
	
	public static HashMap<String, String> loadValues(String path) {
		try {
			HashMap<String, String> values = new HashMap<>();
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			while(in.hasNextLine()) {
				String[] s = in.nextLine().trim().split("\\s*=\\s*", 2);
				if(s.length==2)
					values.put(s[0], s[1]);
			}
			in.close();
			return values;
		}
		catch(Exception e) {
			System.err.printf("Can't load %s. Using default.\n", path);
			return null;
		}
	}
	
	public static boolean saveValues(HashMap<String, String> values, String path) {
		try {
			File file = new File(path);
			File dir = file.getParentFile();
			if(!dir.isDirectory() && !dir.mkdir())
				throw new IOException();
			PrintWriter out = new PrintWriter(file);
			for(Entry<String, String> entry : values.entrySet()) {
				out.printf("%s=%s\n", entry.getKey(), entry.getValue());
			}
			out.close();
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
