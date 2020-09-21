package org.kevin.clustering.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PolarCoordinateTransformation {
	
	Double[][] data;
	
	public PolarCoordinateTransformation(Double[][] data) {
		this.data = data;
	}
	
	public Double[][] getPolarCoordinate() {
		Double[][] polarCoordinate = new Double[this.data.length][this.data[0].length];
		
		for (int i = 0; i < this.data.length; i++) {
//			double thou = 0;
//			for (int j = 0; j < this.data[i].length; j++) {
//				thou += Math.pow(this.data[i][j], 2);
//			}
//			
//			polarCoordinate[i][0] = Math.pow(thou, 0.5);
//			for (int j = 0; j < this.data[i].length - 1; j++) {
//				double x = this.data[i][j];
//				double y = Math.pow((thou - Math.pow(x, 2)), 0.5);
//				polarCoordinate[i][j + 1] = Math.atan2(y, x) / Math.PI * 5000;
////				polarCoordinate[i][j + 1] = Math.atan(y / x) / Math.PI * 180;
//			}
			double x = this.data[i][0];
			double y = this.data[i][1];
			polarCoordinate[i][0] = Math.pow((Math.pow(x, 2) +  Math.pow(y, 2)), 0.5);
			polarCoordinate[i][1] = Math.atan2(y, x) / Math.PI * 5000;
			
		}
		
		return polarCoordinate;
	}
	
	public static void main(String[] args) throws IOException {
		Double[][] data;
		BufferedReader br = new BufferedReader(new FileReader(new File("E:\\roughsetdata\\锟斤拷锟斤拷锟斤拷锟�.txt")));
		String line;
		int length = 0;
		while((line = br.readLine()) != null) {
			length++ ;
		}
		data = new Double[length][];
		br = new BufferedReader(new FileReader(new File("E:\\roughsetdata\\锟斤拷锟斤拷锟斤拷锟�.txt")));
		for(int i = 0; (line = br.readLine()) != null; i++) {
			String[] a = line.split("\t");
			data[i] = new Double[a.length];
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = Double.parseDouble(a[j]);
			}
		}	
		
		PolarCoordinateTransformation pc = new PolarCoordinateTransformation(data);
		Double[][] pdata = pc.getPolarCoordinate();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:\\roughsetdata\\锟斤拷锟斤拷锟斤拷锟�(锟斤拷锟斤拷锟斤拷).txt")));
		for (int i = 0; i < pdata.length; i++) {
			for (int j = 0; j < pdata[i].length; j++) {
				bw.write(pdata[i][j] + "\t");
			}
			bw.write("\n");
		}
		bw.close();
	}

}
