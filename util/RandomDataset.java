package org.kevin.clustering.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.kevin.clustering.estimate.Estimate;
import org.kevin.clustering.hierarchical.linkage.AverageLinkage;
import org.kevin.clustering.hierarchical.linkage.SingleLinkage;
import org.kevin.clustering.hierarchical.rootsearching.lcrs.LCRS_Function_Sim;

public class RandomDataset {

	public static void main(String[] args) throws IOException {
		/**
		 * Create the random datasets
		 */
//		Random r = new Random(System.currentTimeMillis());	
//			for (int z = 9 ; z <= 20; z++) {				
//				double size = Math.pow(2, z);
//				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/wenboxie/Data/randomdata-time/2^" + z + ".txt")));
//				for (int i = 0 ; i < size ; i++) {		
//					double x = r.nextDouble();
//					double y = r.nextDouble();
//					bw.write(x + "\t" + y + "\n");
//				}
//				bw.close();		
//			}
////	
		BufferedWriter bwTime = new BufferedWriter(
				new FileWriter(new File("/Users/wenboxie/Data/rs-exp/time-comparision/time-rs.txt")));
		bwTime.write("size\ttime(ms)\n");
				
		for (int z = 14 ; z <= 14; z++) {	
			/**
			 * Calculate the similarities
			 */
			long runtime = 0;

			Double[][] data = LoadData.getData("/Users/wenboxie/Data/randomdata-time/2^" + z +  ".txt");
			Double[][] similarity = new Double[data.length][data.length];

			for (int i = 0; i < similarity.length; i++) {
				similarity[i][i] = 0.0; 
				for (int j = 0; j < i; j++) {
					similarity[i][j] = similarity[j][i] = Distance.EucDistance(data[i], data[j]);
				}
			}
			System.out.println("Calculate the similarities of dataset: 10^" + z);
			/**
			 * Test the time consumption on algorithms
			 */
			
			/**Group method */		
//			AverageLinkage cl = new AverageLinkage(similarity, 1);
//			long star = System.currentTimeMillis();
//			Integer[] cluster = cl.getCluter();
//			long end = System.currentTimeMillis();
//			runtime += end - star;
			
			/**Nearest neighbor method */
//			SingleLinkage sl = new SingleLinkage(similarity, 1);
//			long star = System.currentTimeMillis();
//			Integer[] cluster = sl.getCluter();
//			long end = System.currentTimeMillis();
//			runtime += end - star;
			
			/**Root searching method */
			double alpha = 2.0;
			int level = 10;
			LCRS_Function_Sim lcrs = new LCRS_Function_Sim(similarity, alpha, level);
			long star = System.currentTimeMillis();
			String[] cluster = lcrs.getCluster();
			long end = System.currentTimeMillis();
			runtime += end - star;

			/**
			 * Write the time in the filed
			 */
			bwTime.write("10^"+ z + "\t"+runtime+"\n");
			System.out.println(runtime);
	
		}	
		bwTime.close();

	}
}
