package org.kevin.clustering.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
/**
 * preprocess weka data to txt file 
 * @author WenboXie
 */
public class WekaData {

	public static void main(String[] args) throws IOException {

		/**
		 * 
		 */
		String fileName = "mfeat-pixel";
		BufferedReader br = new BufferedReader(new FileReader(new File("/Users/wenboxie/Data/uci-20070111/exp/" + fileName + ".txt")));
		BufferedWriter bwData = new BufferedWriter(new FileWriter(new File("/Users/wenboxie/Data/uci-20070111/exp/" + fileName + "(data).txt")));
		BufferedWriter bwLabel = new BufferedWriter(new FileWriter(new File("/Users/wenboxie/Data/uci-20070111/exp/" + fileName + "(label).txt")));
		String line = null;
		
		int linesize = 0;
		
		for(; (line = br.readLine()) != null; linesize++);
		
		br.close();
		br = new BufferedReader(new FileReader(new File("/Users/wenboxie/Data/uci-20070111/exp/" + fileName + ".txt")));
		
		Double[][] data = new Double[linesize][];
		for(int i = 0; (line = br.readLine()) != null; i++) {
			String[] a = line.split(",");
			int l = a.length;
			data[i] = new Double[l - 1];
			for (int j = 0; j < l - 1; j++) {
				data[i][j] = Double.parseDouble(a[j]);
//				bwData.write(a[j] + "\t");
			}
			bwLabel.write(a[l - 1] + "\n");
		}
		Double[] max = new Double[data[0].length];
		Double[] min = new Double[data[0].length];

		for (int j = 0; j < data[0].length; j++) {
			max[j] = Double.MIN_VALUE;
			min[j] = Double.MAX_VALUE;
			for (int i = 0; i < data.length; i++) {
				if (data[i][j] > max[j]) {
					max[j] = data[i][j];
				}
				if (data[i][j] < min[j]) {
					min[j] = data[i][j];
				}
			}
		}
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j< data[i].length; j++) {
				if (max[j] == min[j]) bwData.write(1 + "\t");
				else 
//					bwData.write((data[i][j] - min[j]) / (max[j] - min[j]) + "\t");
				bwData.write(data[i][j] + "\t");
			}
			bwData.write("\n");
		}

//		bwData.write("\n");
		bwData.close();
		bwLabel.close();
		br.close();
	}

}
