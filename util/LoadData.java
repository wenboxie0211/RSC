package org.kevin.clustering.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoadData {

	public static Double[][] getData(String f) throws IOException {
		Double[][] data;
		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
		String line;
		int length = 0;
		while((line = br.readLine()) != null) {
			length++ ;
		}
		data = new Double[length][];
		br = new BufferedReader(new FileReader(new File(f)));
		for(int i = 0; (line = br.readLine()) != null; i++) {
			String[] a = line.split("[\t,]");
			data[i] = new Double[a.length];
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = Double.parseDouble(a[j]);
			}
		}	
		return data;
	}
	
	public static String[] getLabel(String f) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
		String line;
		int length = 0;
		while((line = br.readLine()) != null) {
			length++ ;
		}
		String[] label = new String[length];
		br = new BufferedReader(new FileReader(new File(f)));
		for(int i = 0; (line = br.readLine()) != null; i++) {
			String[] a = line.split("[\t,]");
			label[i] = a[0];			
		}	
		return label;
	}
}
