package org.kevin.clustering.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kevin.clustering.estimate.Estimate;
import org.kevin.clustering.partition.Kmeans;

public class EstimateNolabelResults {
public static void main(String[] args) throws IOException {
		

	String[] label = LoadData.getLabel("E:\\roughsetdata\\weka\\balance-scale(label).txt");
	String[] cluster = LoadData.getLabel("E:\\roughsetdata\\results\\balance-scale(AverageLinkage).txt");
	double RI = Estimate.RI(cluster, label);
	System.out.print(RI);

		
	}
	
}
