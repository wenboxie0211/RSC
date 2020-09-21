package org.kevin.clustering.util;

public class Distance {
	public double getEucDistance(Double[] a, Double[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			d += Math.pow((a[i] - b[i]), 2);
		}
		return Math.pow(d, 0.5);
	}
	
	public static double getCosDistance(Double[] a, Double[] b) {
		double m = 0.0;
		for (int i = 0; i < b.length; i++) {
			m += a[i] * b[i];
		}
		double va = 0.0, vb = 0.0;
		for (int i = 0; i < b.length; i++) {
			va += a[i] * a[i];
			vb += b[i] * b[i];
		}
		return m / (Math.pow(va, 0.5) * Math.pow(vb, 0.5));
	}
	public static double getCosDistance(double[] a, double[] b) {
		double m = 0.0;
		for (int i = 0; i < b.length; i++) {
			m += a[i] * b[i];
		}
		double va = 0.0, vb = 0.0;
		for (int i = 0; i < b.length; i++) {
			va += a[i] * a[i];
			vb += b[i] * b[i];
		}
		return m / (Math.pow(va, 0.5) * Math.pow(vb, 0.5));
	}
	
	public static double EucDistance(Double[] a, Double[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			d += Math.pow((a[i] - b[i]), 2);
		}
		return Math.pow(d, 0.5);
	}public static double EucDistance(Float[] a, Float[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			d += Math.pow((a[i] - b[i]), 2);
		}
		return Math.pow(d, 0.5);
	}
	public static double EucDistance(double[] a, double[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			d += Math.pow((a[i] - b[i]), 2);
		}
		return Math.pow(d, 0.5);
	}
	public static double getManhattanDistance(Double[] a, Double[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			d += Math.abs(a[i] - b[i]);
		}
		return d;
	}
	public static double getChebyshevDistance(Double[] a, Double[] b) {
		double d = 0;
		for (int i = 0; i < b.length; i++) {
			if(d < Math.abs(a[i] - b[i])) d = Math.abs(a[i] - b[i]);
		}
		return d;
	}
	public static double getCorrelationDistance(Double[] a, Double[] b) {

		double sumX = 0.0;  
        double sumY = 0.0;  
        double sumX_Sq = 0.0;  
        double sumY_Sq = 0.0;  
        double sumXY = 0.0;  

        for (int i = 0; i < b.length; i++) {
        	 sumX += a[i];  
             sumY += b[i];  
             sumX_Sq += Math.pow(a[i], 2);  
             sumY_Sq += Math.pow(b[i], 2);  
             sumXY += a[i] * b[i];  
		}
  
        double numerator = sumXY - sumX * sumY / b.length;  
        double denominator = Math.sqrt((sumX_Sq - sumX * sumX / b.length)  
                * (sumY_Sq - sumY * sumY / b.length));  
        if (denominator == 0) {  
            return 0;  
        }  
        return 1-numerator / denominator;  
	}
}
