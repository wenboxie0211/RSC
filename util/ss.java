package org.kevin.clustering.util;

import org.kevin.clustering.estimate.Estimate;
import org.kevin.clustering.hierarchical.rootsearching.lcrs.LCRS_Function_Sim;

public class ss {

	public static void main(String[] args) {
		for (double alpha = 1.5 ,k=0.1; alpha <= 2.5; alpha+=k) {
			System.out.println(k);
			System.out.println(alpha );
		}
	}
}
