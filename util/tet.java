package org.kevin.clustering.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.kevin.clustering.estimate.Estimate;


public class tet {

	public static void main(String[] args) throws IOException {
//		String[] dataName = {"5813"};
//
//		int[] k = {4};
//		
//		for (int p = 0; p < k.length; p++) {
//
//			for (int i = 0; i < 1; i++) {
//				Double[][] data;
//				BufferedReader br = new BufferedReader(new FileReader(new File("E:\\Mcc����ѵ������\\" + dataName[p] + "\\�û�-ʱ-��������һ�׵���.txt")));
//				String line;
//				int length = 0;
//				while((line = br.readLine()) != null) {
//					length++ ;
//				}
//				data = new Double[length][];
//				br = new BufferedReader(new FileReader(new File("E:\\Mcc����ѵ������\\" + dataName[p] + "\\�û�-ʱ-��������һ�׵���.txt")));
//				for(int m = 0; (line = br.readLine()) != null; m++) {
//					String[] a = line.split("\t");
//					data[m] = new Double[a.length - 1];
//					for (int j = 0; j < data[m].length; j++) {
//						data[m][j] = Double.parseDouble(a[j + 1]);
//					}
//				}	
//				AverageLinkage al = new AverageLinkage(data, k[p]);
////				CupleCore cc = new CupleCore(LoadData.getData("E:\\roughsetdata\\" + dataName[p] + ".txt"), k[p]);
////				Integer[] cluster = cc.getCluster();
//				Integer[] cluster = al.getCluter();
//				
//				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:\\Mcc����ѵ������\\" + dataName[p] + "\\�û�-ʱ-��������һ�׵�����Cluster-" + k[p] + "-label��.txt")));
//				for (int j = 0; j < cluster.length; j++) {
//					bw.write(cluster[j] + "\n");
//				}
//				bw.close();
//				
//				Map<Integer, Double[]> avMap = new HashMap<Integer, Double[]>();
//				
//				for (int j = 0; j < data.length; j++) {
//					int c = cluster[j];
//					if (!avMap.containsKey(c)) {
//						avMap.put(c, data[j]);
//					} else {
//						Double[] av = avMap.get(c);
//						for (int l = 0; l < av.length; l++) {
//							av[l] = (av[l] + data[j][l]) / 2;
//						}
//					}
//
//				}
//				
//				bw = new BufferedWriter(new FileWriter(new File("E:\\Mcc����ѵ������\\" + dataName[p] + "\\�û�-ʱ-��������һ�׵�����Cluster-" + k[p] + "-average��.txt")));
//				for (Entry<Integer, Double[]> avEntry : avMap.entrySet()) {
//					bw.write(avEntry.getKey() + "\t");
//					for (Double v : avEntry.getValue()) {
//						bw.write(v + "\t");
//					}
//					bw.write("\n");
//				}
//				bw.close();
//				
//			}
//			
//			
//		}
//		
//	
		for(int i =0 ;i<100;i++){
		System.out.print((new Random(System.currentTimeMillis()).nextInt(4)));}
	}
}
