package org.kevin.clustering.hierarchical.rootsearching.lcrs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.kevin.clustering.estimate.Estimate;
import org.kevin.clustering.util.Distance;
import org.kevin.clustering.util.LoadData;
import org.kevin.clustering.util.Node;

public class RS {
	int l;
	/**
	 * the parametor to caculate the limit value of tree height
	 */
	double alpha;
	int noise;

	/**
	 * the funtions to caculate the limit value of tree height
	 */

	int getLimit(int n, double alpha) {
		// return (int)Math.ceil(Math.pow(n, alpha) + beta);
		if (alpha == 1.0) return n;
		return (int) Math.ceil(Math.log(n)/Math.log(alpha));
		
	}

	/**
	 * data will be collected as map which is constructed with tree node
	 */
	Map<String, Node> data;
	int dataSize;
	/**
	 * the count of data
	 */
	int newRootNumber;

	Map<String, Map<String, Double>> sim;

	/**
	 * 
	 * @param data
	 * @param limit
	 * @param k
	 */
	public RS(Double[][] sim, double alpha, int l) {
		this.data = new HashMap<>();
		for (int i = 0; i < sim.length; i++) {
			Node root = new Node(i + "");
			this.data.put(root.getName(), root);
		}
		this.sim = new HashMap<>();
		for (String node : this.data.keySet()) {
			int nodeNo = Integer.parseInt(node);
			Map<String, Double> s = new HashMap<>();

			for (String comNode : this.data.keySet()) {
				int comNodeNo = Integer.parseInt(comNode);
				s.put(comNode, sim[nodeNo][comNodeNo]);
			}
			this.sim.put(node, s);			
		}
		this.dataSize = data.size();
		this.alpha = alpha;
		this.l = l;
		this.newRootNumber = 0;
	}
	
	public RS(Map<String, Map<String, Double>> sim, double alpha, int l, int noise) {
		this.data = new HashMap<>();
		for (Entry<String, Map<String, Double>>  r : sim.entrySet()) {
			Node root = new Node(r.getKey());
			this.data.put(root.getName(), root);
			
		}
		this.noise = noise;
		this.sim = sim;
		
		this.dataSize = data.size();
		this.alpha = alpha;
		this.l = l;
		this.newRootNumber = 0;
	}
	
	Map<String, List<String>> getNearby(Set<String> data) {
		Map<String, List<String>> nearby = new HashMap<>();
		for (String node : data) {
//			System.out.println("node.name = " + node.getName());
		
			Map<String, Double> dis = this.sim.get(node);

			Double minDistance = Double.MAX_VALUE;
			List<String> nset = new ArrayList<>();
			
			for (Entry<String, Double> comEntry : dis.entrySet()) {
				
				String comNode = comEntry.getKey();
				if (!data.contains(comNode)) continue;
				
				if (node != comNode) {
					Double d = comEntry.getValue();
					if (d < minDistance) {
						nset = new ArrayList<>();
						nset.add(comNode);
						minDistance = d;
					} else if (d == minDistance) {
						nset.add(comNode);
					}
				}
			}
			nearby.put(node, nset);
		}
		return nearby;
	}
	
	Set<String> searchRoot(Set<String> data) {
		/**
		 * a) Put all the nodes into a candidate set;
		 */
		
		Set<String> rootMap = new HashSet<>();
		Set<String> waiteMap = new HashSet<>();
		for (String root : data) waiteMap.add(root);
		
		Map<String, List<String>> nearby = getNearby(waiteMap);
		/**
		 * b) If the candidate set is not empty, choose a node from the
		 * candidate set randomly, and add it into a List L as the starting
		 * node, otherwise finish this clusteringlevel ;
		 */
		while (waiteMap.size() > 0) {
			
			String[] keys = waiteMap.toArray(new String[0]);
			String child = keys[new Random(System.currentTimeMillis()).nextInt(keys.length)];
			List<String> list = new LinkedList<>();
			list.add(child);

			while (true) {
				/**
				 * c) Search the nearest node of C and regard it as its parent
				 * node P;
				 */
				if (nearby.get(child).size() == 0) {
					rootMap.add(child);
					for (String element : list) {
						waiteMap.remove(element);
					}
					break;
				}
				String parent = nearby.get(child).get((new Random(System.currentTimeMillis()).nextInt(nearby.get(child).size())));
				
				/**
				 * d) If P has already been in the list L, create a new node R
				 * which is the centroid of C and P. Treat L as a tree with root
				 * R and return to step b) ;
				 */
				if (list.contains(parent)) {

					String newRootName = "root." + this.newRootNumber;
					this.newRootNumber++;
					Node newRoot = new Node(newRootName, child, parent);
					this.data.put(newRootName, newRoot);
					
					rootMap.add(newRootName);
//					System.out.println("5:" + parent + " and " + child +
//							" are nearest neighbor.");
//					System.out.println("5:rootMap.put(" + newRootName + ")");

					this.data.get(parent).index = 1;
					this.data.get(child).index = 1;
					newRoot.addChild(child);
					newRoot.addChild(parent);
//					System.out.println("5:" + child + "->" +
//							newRootName + "<-" + parent);
				
					/**
					 * update the height of each node
					 */
					String p = child, c = null;

					// System.out.print("5:" + newRoot.getName() + "<-" +
					// child.getName());
					for (int i = 0; i < list.size(); i++) {
						c = list.get(list.size() - i - 1);
						if (this.data.get(c).index == 0) {
							this.data.get(c).index = this.data.get(p).index + 1;
							this.data.get(p).addChild(c);
//							 System.out.print(index + "<-" + c.getName());
							p = c;
						} else {
//							 System.out.println("d: " + c.getName() + " index is not 0 -> " + c.index);
						}
					}

					// System.out.print("\n");
					for (String element : list) {
						waiteMap.remove(element);
//						System.out.println("5:waiteMap.remove(" + element + ")");
					}
					break;
				}

				/**
				 * e) If P is a node of a tree T, join L into T as a sub-tree.
				 * Due to the limitation of tree height, some nodes in the top
				 * of the list L should be removed and treat each of them as a
				 * new root. Return to step b);
				 */
				if (!waiteMap.contains(parent)) {
					int i = 0;
					String p = parent, c = null;
//					System.out.println("4:" + p + " has included in the list");
//					System.out.print("4:" + p);
					for (; i < list.size(); i++) {

						c = list.get(list.size() - i - 1);
						if (this.data.get(c).index == 0) {
							this.data.get(c).index = this.data.get(p).index + 1;
							this.data.get(p).addChild(c);
							// System.out.println("index : " + c.index);
							
							p = c;
//							System.out.print(" <- " + p);
						} else {
							// System.out.println("e: " + c.getName() + " index
							// is not 0 -> " + c.index);
						}
					}
//					System.out.print("\n");
					for (String element : list) {
						waiteMap.remove(element);
//						System.out.println("4:waiteMap.remove(" + element + ")");
					}

					break;
				}
				/**
				 * f)Regard P as C and return to step c).
				 */
				//
				list.add(parent);
//				System.out.println("6:" + parent + "<-" + child);
				child = parent;
			}

		}
		// System.out.println("rootmap.size=" + rootMap.size());
		/**
		 * h) update the similarity map
		 */
		for (String node : rootMap) {
			String l1 = this.data.get(node).getL();
			String r1 = this.data.get(node).getR();
			Double ab = this.sim.get(l1).get(r1);
			Map<String, Double> s = this.sim.get(node);
			if (s == null) {
				s = new HashMap<>();
				sim.put(node, s);
			}
			for (String comNode : rootMap) {
				String l2 = this.data.get(comNode).getL();
				String r2 = this.data.get(comNode).getR();
				
				Double cd = this.sim.get(l2).get(r2);

				Double ad = this.sim.get(l1).get(l2);
				Double bc = this.sim.get(r1).get(r2);
				Double ac = this.sim.get(l1).get(r2);
				Double bd = this.sim.get(r1).get(l2);
				Double ef ;
				try {
					ef = Math.pow((ad * ad + bc * bc + ac * ac + bd * bd - ab * ab - cd * cd) / 4, 0.5);
					s.put(comNode, ef);
				} catch (Exception e) {
					// TODO: handle exception
					
				}

			}
		}
		/**
		 * g) when the clustering level is end, cut the leaves whose index is
		 * out of the height limit
		 */
		Set<String> leaves = new HashSet<>();
		for (String root : rootMap) {
			Node r = this.data.get(root);
			
			// System.out.println("tree size is :" + tree.getNotes().size());
			Set<String> rootNodes = getLeavesInThisLevel(r.getName());
			int limit = getLimit(rootNodes.size(), this.alpha);

			// int limit = getLimit_log(rootNodes.size());
//			System.out.println("tree :" + root + "; size is " + rootNodes.size() + "; limit is " + limit);
			leaves.addAll(CutLeaves(root, limit));
		}
		
		for (String node : leaves) {
					
			Map<String, Double> s1 = this.sim.get(node);
			
			for (String comNode : rootMap) {
				Map<String, Double> s2 = this.sim.get(comNode);
				String l2 = this.data.get(comNode).getL();
				String r2 = this.data.get(comNode).getR();
				
				Double ab = this.sim.get(node).get(l2);
				Double ac = this.sim.get(node).get(r2);
				Double bc = this.sim.get(l2).get(r2);
			
				try {
					Double ad = Math.pow((ab * ab + ac * ac)/2 - (bc * bc)/4, 0.5);
//					System.out.println("sim<" + node.getName() + ", <" + comNode.getName() + ", " + ef + ">>");
					s1.put(comNode, ad);
					s2.put(node, ad);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
//		System.out.println("7:there are " + leaves.size() +
//				" leaves have been cut down.");
		for (String treeNode : leaves) {
			rootMap.add(treeNode);
//			 System.out.println("7:cut the leaf and rootMap.put(" + treeNode + ")");
		}
		
		return rootMap;
	}
	
	public Set<String> getLeavesInThisLevel(String root) {
		return getChildNoteSet(root);
	}	
	
	Set<String> getChildNoteSet(String note) {
		Set<String> childNodes = this.data.get(note).getChildSet();
		Set<String> childSet = new HashSet<String>();		
		
		if (childNodes != null) {
			for (String c : childNodes) {
				
				if (this.data.get(note).index == 
						this.data.get(c).index - 1) {
//					System.out.println(note.index + "<-" + treeNote.index);
					childSet.addAll(getChildNoteSet(c));
					childSet.add(c);	
				}
			}
				
		}
//		System.out.println("childset size is " + childSet.size());
		return childSet;
	}

	Set<String> CutLeaves(String root, int limit) {
		Set<String> leaves = new HashSet<>();

		Set<String> childNodes = this.data.get(root).getChildSet();
		Set<String> cutChild = new HashSet<>();
		
		for (String child : childNodes) {
			if (this.data.get(child).index != this.data.get(root).index + 1)
				continue;
			Set<String> c = this.data.get(child).getChildSet();
			if (c.size() != 0) {
				Set<String> l = CutLeaves(child, limit);
				if (l != null)
					leaves.addAll(l);
			} 
			if (this.data.get(child).index > limit) {
//				System.out.println("cut :" + child + ";limit :"
//						+ limit + ";index:" + this.data.get(child).index);
				leaves.add(child);
				this.data.get(child).index = 0;
				cutChild.add(child);							
			}
		}
		for (String child : cutChild) {
			this.data.get(root).getChildSet().remove(child);	
		}
		return leaves;
	}
	
	public Map<String, String> getCluster() {

		Set<String> rootMap = new HashSet<>(this.data.keySet());
		for(int l=0; l<this.l; l++) {
			Set<String> newrootMap = searchRoot(rootMap);
			if (newrootMap.size() == rootMap.size()) break;
			rootMap = newrootMap;
			if (rootMap.size() ==1) break;
			
		}
		/**
		 * remove noise
		 */
		for(int t = 2;t<=this.noise;) {
//			System.out.println("t="+t);
			boolean flag = false;
			Set<String> newrootMap = new HashSet<>();
			for (String root : rootMap) {
				if (flag == true) {
					newrootMap.add(root);
					continue;
				}
								
				Set<String> c = getNotes(root);
				//noise size
				
	//			if (c.size()==1) 
	//				System.out.println("noise node: "+c.toArray()[0]);
				if (c.size()>t+1) {
					newrootMap.add(root);
//					System.out.print(";add:"+root);
					continue;
				}
	
				flag = true;
				
				String r = null;
				double max = 0;
	
				for (String noi : c) {
					Map<String, Double> neiMap = sim.get(noi);
	//				if (neiMap.size() < 2) continue;
					for (Entry<String, Double> s : neiMap.entrySet()) {
						if (c.contains(s.getKey())) continue;
						if (s.getValue()>max) {
							r =s.getKey();
							max=s.getValue();
						}
					}
				}
				if (r!=null) {
					data.get(r).addChild(root);
//					System.out.println(r+"<-"+root);
				} else {
					newrootMap.add(root);
	//				System.out.println("noise node: "+c.toArray()[0]);
				}
			}
			
			if (flag==true) {
				rootMap = newrootMap;
			} else {
				t++;
//				System.out.println("t++");	
			}
			
		}
		
		Map<String,String> cluster = new HashMap<>();
		int k = 0;
		// System.out.println("rootMap.size()=" + rootMap.size());
		
		
		
		for (String root : rootMap) {
			
			Set<String> c = getNotes(root);
//			System.out.print(k +": "+root + "->");
			for (String i : c) {
				
				if (!i.contains("root")) {
					cluster.put(i, k+"");
//					System.out.print(i+","+k+"");
//					System.out.print(i+",");
				}
			}
//			System.out.print("\n");
			k++;
		}

		return cluster;
	}
	
	Set<String> getNotes(String root) {
		return getChildSet(root);
	}	
	
	Set<String> getChildSet(String node) {
		Set<String> childNodes = this.data.get(node).getChildSet();
		Set<String> childSet = new HashSet<String>();
		childSet.add(node);
		
		if (childNodes == null) return childSet;
		else {
			for (String treeNode : childNodes) {
				childSet.addAll(getChildSet(treeNode));
			}
			return childSet;
		}
	}
	
	public static void main(String[] args) throws IOException {

		/**
		 * data set
		 */
		// String[] dataName =
		// {"breast-w","ecoli","glass","ionosphere","iris","kdd_synthetic_control","mfeat-fourier","mfeat-karhunen","mfeat-zernike"};
		// {"optdigits","segment","sonar","vehicle","waveform-5000","letter","kdd_synthetic_control"};

		/**
		 * experiment for one set of parameter
		 */
		
		String dataName = "ecoli";

		int times = 1;
		int level = 4; 
		Double min_alpha = 2.0, max_alpha = 2.0;
		
		
//		String f = "/Users/wenboxie/Data/uci-20070111/exp/" + dataName + "(data).txt";
//		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
//		Double[][] similarity = new Double[400][400];
//		String line;
//		for (int i = 0; i < similarity.length; i++) {
//			similarity[i][i] = 0.0;
//		}
//		while((line = br.readLine()) != null) {
//			String[] a = line.split(",");
//			similarity[Integer.parseInt(a[0]) - 1][Integer.parseInt(a[1]) - 1] = 
//					Double.parseDouble(a[2]);
//		}
//		br.close();
//		
		
		Double[][] data = LoadData.getData("/Users/wenboxie/Data/uci-20070111/exp/" + dataName + "(data).txt");
		Double[][] similarity = new Double[data.length][data.length];

		for (int i = 0; i < similarity.length; i++) {
			for (int j = 0; j < similarity.length; j++) {
				similarity[i][j] = Distance.EucDistance(data[i], data[j]);
//				similarity[i][j] = Distance.getManhattanDistance(data[i], data[j]);
//				similarity[i][j] = Distance.getCorrelationDistance(data[i], data[j]);
			}
		}
		
		BufferedWriter bwRI = new BufferedWriter(
				new FileWriter(new File("/Users/wenboxie/Data/rs-exp/alpha-optimal/" + dataName + ".txt")));
		bwRI.write("alpha\tlevel\tRI\n");
//		BufferedWriter bwAA = new BufferedWriter(
//				new FileWriter(new File("/Users/wenboxie/Data/rs-exp/rs/LCRS_FUNCTION_SIM-" + dataName + "-AA.txt")));

		
		for (int l = 1; l <= level; l++) {
			
			
			System.out.println(dataName + "("+l+")");
			
			for (double alpha = min_alpha; alpha < max_alpha+0.1; alpha+=0.1) {
				double RI = 0.0;
				String[][] clusters = new String[times][];
				for (int i = 0; i < times; i++) {
//					RS lcrs = new RS(similarity, alpha, l);
//					String[] cluster = lcrs.getCluster();
//					
//					clusters[i] = cluster;
//					Estimate es = new Estimate(cluster,
//							LoadData.getLabel("/Users/wenboxie/Data/uci-20070111/exp/" + dataName + "(label).txt"));
//					RI += es.getRI();
//					AA[i]= es.getAA();
//					System.out.println("FM:" + FM[i][p] + "\tAA:" + AA[i][p]);
				}
//				BufferedWriter bwCluster = new BufferedWriter(
//						new FileWriter(new File("/Users/wenboxie/Data/rs-exp/kappa-benchmarkdata/clustering results/" + dataName + "-" + alpha+ "-"+ l + ".txt")));
//				for (int i = 0; i < clusters[0].length; i++) {
//					for (int j = 0; j < clusters.length; j++) {
//						bwCluster.write(clusters[j][i] + ",");
//					}
//					bwCluster.write("\n");
//				}
//				bwCluster.close();
//				double K = Estimate.Fleiss_kappa(clusters);
//				for (int i = 0; i < times; i++) {
//					bwAA.write(AA[i] + "\t");
//					bwRI.write(RI[i] + "\t");
//				}
				System.out.println(alpha +"\t" + l + "\t" + RI/times );
				bwRI.write(alpha +"\t" + l + "\t" + RI/times + "\n");
//				bwRI.write(alpha +"\t" + l + "\t" + K + "\n");
		}
//				bwAA.write("\n");

				
				
		}
		bwRI.close();
//		bwAA.close();
		System.out.println("complete!");
	
	}
}
