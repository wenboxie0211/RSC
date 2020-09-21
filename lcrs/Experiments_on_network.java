package org.kevin.clustering.hierarchical.rootsearching.lcrs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kevin.clustering.estimate.Estimate;
import org.kevin.clustering.util.Distance;
import org.kevin.clustering.util.LoadData;

public class Experiments_on_network {

	public static void main(String[] args) throws IOException {	

		int times = 1;
		int level = 3; 
		int noise = 5;
		Double min_alpha = 2.0, max_alpha = 3.0;
		
		String dataname = "LFR-1000";
		String f = "/Users/wenboxie/Data/network/"+ dataname +"/ectd."+ dataname+".csv";
//		String f = "/Users/wenboxie/Data/network/netsci/out.netsci2.csv";
		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
		Map<String, Map<String, Double>> sim = new HashMap<>();
		String line=br.readLine();
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/wenboxie/Data/network/subelj_euroroad/weight.subelj_euroroad")));
//		bw.write("Source,Target,Weight\n");
//		for(int i=0;(line = br.readLine()) != null;i++) {
//			String[] a = line.split(",");
//
//			Map<String, Double> b = new HashMap<>();
//			for (int j = 0; j < a.length; j++) {
//				if (i==j) continue;
//				double s = Double.parseDouble(a[j]);
//				if (s == 0.0||s > 5000.0) continue;
//				else b.put(j+"", s);
//				bw.write(i+","+j+","+s+"\n");
//			}
//			sim.put(i+"", b);
//		}
//		br.close();
//		bw.close();
//	
		
				while((line = br.readLine()) != null) {
					String[] a = line.split(",");
					String S = a[0];
					String T = a[1];
					Double W = Double.parseDouble(a[2]);
//					Double W = Double.parseDouble(a[6]);
		
					if (W==0.0) continue;
		
					Map<String, Double> b = sim.get(S);
					if(b==null){
						b=new HashMap<>();
						//				b.put(a[1], 1/Double.parseDouble(a[2]));
						b.put(T, W);
						sim.put(S, b);
					} else{
						//				b.put(a[1], 1/Double.parseDouble(a[2]));
						b.put(T, W);
					}
		
					b = sim.get(T);
					if(b==null){
						b=new HashMap<>();
						//				b.put(a[0], 1/Double.parseDouble(a[2]));
						b.put(S, W);
						sim.put(T, b);
					} else{
						//				b.put(a[0], 1/Double.parseDouble(a[2]));
						b.put(S,W);
					}
		
				}
				br.close();
		
		
//		

		
//		BufferedWriter bwRI = new BufferedWriter(
//				new FileWriter(new File("/Users/wenboxie/Data/rs-exp/network/soc-advogato.txt")));
//		bwRI.write("alpha\tlevel\tRI\n");
//		BufferedWriter bwAA = new BufferedWriter(
//				new FileWriter(new File("/Users/wenboxie/Data/rs-exp/rs/LCRS_FUNCTION_SIM-" + dataName + "-AA.txt")));
//
//		
		for (int l = level; l <= level; l++) {

			for (double alpha = min_alpha; alpha < max_alpha+0.1; alpha+=0.1) {
//				double RI = 0.0;
//				Map<String,String>[] clusters = new Map [times];
//				for (int i = 0; i < times; i++) {
					RS rs = new RS(sim, alpha, l, noise);
					Map<String, String> cluster = rs.getCluster();
//					clusters[i] = cluster;
					
					
//					for (int j = 0; j < cluster.size(); j++) {
//						System.out.print( cluster[j] + ",");
//					}
//					Estimate es = new Estimate(cluster,
//							LoadData.getLabel("/Users/wenboxie/Data/uci-20070111/exp/" + dataName + "(label).txt"));
//					RI += es.getRI();
//					AA[i]= es.getAA();
//					System.out.println("FM:" + FM[i][p] + "\tAA:" + AA[i][p]);
//				}
				BufferedWriter bwCluster = new BufferedWriter(
						new FileWriter(new File("/Users/wenboxie/Data/network/" + dataname + "/result-rs.csv")));
//					BufferedWriter bwCluster = new BufferedWriter(
//							new FileWriter(new File("/Users/wenboxie/Data/network/netscience/label-clear"+level+".csv")));
//				bwCluster.write("Id,Label,Interval-clear-"+ l +"\n");
					bwCluster.write("Id,ectd-"+ l +"\n");
//				for (int i = 0; i < clusters[0].size(); i++) {
					for (Entry<String, String> map : cluster.entrySet()) {
//						bwCluster.write(map.getKey()+ ","+map.getKey() + "," + map.getValue()+"\n");
						bwCluster.write(map.getKey()+  "," + map.getValue()+"\n");
					}
				
				
//				}
				bwCluster.close();
				
				Double L = 0.0;
				Map<String, Double> ls_map = new HashMap<>();
				Map<String, Double> ds_map = new HashMap<>();
				
				for (Entry<String, String> map : cluster.entrySet()) {
					String a = map.getKey();
					String a_l = map.getValue();
					for (Entry<String, Double> ws : sim.get(a).entrySet()) {
						String b = ws.getKey();
						if (a.equals(b)) continue;
						Double w = ws.getValue();
						String b_l = cluster.get(b);
						
						L+=w;
						
						Double ds = ds_map.get(a_l);
						if (ds==null) {
							ds_map.put(a_l, w);
						} else {
							ds_map.put(a_l, ds+w);
						}
						
						if (!a_l.equals(b_l)) continue;
						
						Double ls = ls_map.get(a_l);
						if (ls==null) {
							ls_map.put(a_l, w);
						} else {
							ls_map.put(a_l, w+ls);
						}			
					}	
				}
				
				L = L/2;
				
				Double Q = 0.0;
				for (Entry<String, Double> lsE : ls_map.entrySet()) {
					String C = lsE.getKey();
					Double ls = lsE.getValue();
					Double ds = ds_map.get(C);
					Q += ls/(L*2) - Math.pow(ds/(2*L), 2);
				}
				
				System.out.println(l);
				
				System.out.println("Q="+Q);
//				double K = Estimate.Fleiss_kappa(clusters);
//				for (int i = 0; i < times; i++) {
//					bwAA.write(AA[i] + "\t");
//					bwRI.write(RI[i] + "\t");
//				}
//				System.out.println(alpha +"\t" + l + "\t" + RI/times );
//				bwRI.write(alpha +"\t" + l + "\t" + RI/times + "\n");
//				bwRI.write(alpha +"\t" + l + "\t" + K + "\n");
		}
//				bwAA.write("\n");

				
				
		}
//		bwRI.close();
//		bwAA.close();
		System.out.println("complete!");

	}

}
