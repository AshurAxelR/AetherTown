package com.xrbpowered.aethertown.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class ZipBuilder {

	public interface DataPack {
		public Collection<String> listDataEntries();
		public Collection<String> listRequiredDataEntries();
		public boolean loadDataEntry(String name, InputStream in);
		public boolean saveDataEntry(String name, OutputStream out);
	}
	
	private ZipBuilder() {}

	public static boolean load(String path, DataPack data) {
		try(ZipInputStream zip = new ZipInputStream(new FileInputStream(new File(path)))) {
			HashSet<String> entries = new HashSet<>();
			HashSet<String> required = new HashSet<>(data.listRequiredDataEntries());
			
			ZipEntry zipEntry;
			while((zipEntry = zip.getNextEntry()) != null) {
				String name = zipEntry.getName();
				boolean res = data.loadDataEntry(name, zip);
				zip.closeEntry();
				if(res)
					entries.add(name);
				else if(required.contains(name))
					throw new IOException();
			}
			
			for(String name : required) {
				if(!entries.contains(name)) {
					System.err.println("Missing "+name);
					throw new IOException();
				}
			}
			
			System.out.printf("%s loaded.\n", path);
			return true;
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.printf("Can't load %s. Using default.\n", path);
			return false;
		}
	}
	
	public static boolean save(String path, DataPack data) {
		try(ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(path)))) {
			HashSet<String> required = new HashSet<>(data.listRequiredDataEntries());
			
			for(String name : data.listDataEntries()) {
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				boolean res = data.saveDataEntry(name, buf);
				
				if(res) {
					zip.putNextEntry(new ZipEntry(name));
					zip.write(buf.toByteArray());
					zip.closeEntry();
				}
				else if(required.contains(name))
					throw new IOException();
			}
			
			System.out.printf("%s saved.\n", path);
			return true;
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.printf("Can't save %s.\n", path);
			new File(path).delete();
			return false;
		}
	}
	
}
