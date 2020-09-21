package org.kevin.clustering.hierarchical.rootsearching.lcrs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.kevin.clustering.util.Tree;
import org.kevin.clustering.util.TreeNote;

/**
 * LCRS_Function Algorithm
 * 
 * @author WenboXie limited-cuplecore-function: using function to caculate the
 *         height threshold (parameter is alpha)
 */
public class LCRS_Function {
	/**
	 * the value to limit the height of trees
	 */
//	int limit, k;
	int k;
	/**
	 * the parametor to caculate the limit value of tree height
	 */
	double alpha,beta;
	
	/**
	 * the funtions to caculate the limit value of tree height
	 */
	
	int getLimit_log(int n) {
		return (int)Math.log(n+1) + 1;
	}
		
	int getLimit_pow(int n, double alpha) {
		return (int)Math.pow(n, alpha) ;
	}
	
	int getLimit(int n, double alpha, double beta) {
//		return (int)Math.ceil(Math.pow(n, alpha) + beta);
		
		
		
		return (int)Math.ceil(Math.log(n + 2)) ;
	}
	/**
	 * data will be collected as map which is constructed with tree node
	 */
	Map<TreeNote, Tree> data;
	/**
	 * the count of data
	 */
	int newRootNumber;

	/**
	 * 
	 * @param data
	 * @param limit
	 * @param k
	 */
	public LCRS_Function(Double[][] data, double alpha,double beta, int k) {
		this.data = new HashMap<>();
		for (int i = 0; i < data.length; i++) {
			TreeNote root = new TreeNote(i + "", data[i]);
			this.data.put(root, new Tree(root));
		}
		this.alpha = alpha;
		this.beta = beta;
		this.k = k;
		this.newRootNumber = 0;
	}

	/**
	 * 
	 * @param data
	 * @return the map which discribe the nearest nearbor of nodes
	 */
	Map<TreeNote, TreeNote> getNearby(Map<TreeNote, Tree> data) {
		Map<TreeNote, TreeNote> nearby = new HashMap<>();
		for (Entry<TreeNote, Tree> entry : data.entrySet()) {
			TreeNote node = entry.getKey();
			Double[] vetor = node.getValue();
			TreeNote minNode = null;
			Double minDistance = Double.MAX_VALUE;
			for (Entry<TreeNote, Tree> comEntry : data.entrySet()) {
				TreeNote comNode = comEntry.getKey();
				if (node != comNode) {
					Double[] comVetor = comNode.getValue();
					double distance = Distance.EucDistance(vetor, comVetor);
					if (distance < minDistance) {
						minDistance = distance;
						minNode = comNode;
					}
				}
			}
			nearby.put(node, minNode);
		}
		return nearby;
	}

	/**
	 * searching roots in one level
	 * 
	 * @param data
	 * @return map in which keys are the index of the roots and values are root
	 *         nodes
	 */
	Map<TreeNote, Tree> searchRoot(Map<TreeNote, Tree> data) {
		/**
		 * a) Put all the nodes into a candidate set;
		 */
		Map<TreeNote, Tree> rootMap = new HashMap<>();
		Map<TreeNote, Tree> waiteMap = new HashMap<>();
		for (Entry<TreeNote, Tree> dataEntry : data.entrySet()) {
			TreeNote root = dataEntry.getKey();
			TreeNote newRoot = new TreeNote(root);
			waiteMap.put(newRoot, new Tree(newRoot));
		}
		Map<TreeNote, TreeNote> nearby = getNearby(waiteMap);
		/**
		 * b) If the candidate set is not empty, choose a node from the
		 * candidate set randomly, and add it into a List L as the starting
		 * node, otherwise finish this clusteringlevel ;
		 */
		while (waiteMap.size() > 0) {

			TreeNote[] keys = waiteMap.keySet().toArray(new TreeNote[0]);
			TreeNote child = keys[new Random(System.currentTimeMillis()).nextInt(keys.length)];
			List<TreeNote> list = new LinkedList<>();
			list.add(child);

			while (true) {
				/**
				 * c) Search the nearest node of C and regard it as its parent
				 * node P;
				 */
				TreeNote parent = nearby.get(child);

				/**
				 * d) If P has already been in the list L, create a new node R
				 * which is the centroid of C and P. Treat L as a tree with root
				 * R and return to step b) ;
				 */
				if (list.contains(parent)) {

					Double[] childValue = child.getValue();
					Double[] parentValue = parent.getValue();
					Double[] v = new Double[childValue.length];
					for (int i = 0; i < v.length; i++) {
						v[i] = (childValue[i] + parentValue[i]) / 2;
					}
					String newRootName = "root." + this.newRootNumber;
					this.newRootNumber++;
					TreeNote newRoot = new TreeNote(newRootName, v);
					rootMap.put(newRoot, new Tree(newRoot));
//					 System.out.println("5:rootMap.put(" + newRoot.getName() +
//					 ")");

					// TreeNote p = newRoot, c = null;
					// System.out.print("5:" + p.getName());
					// for (int i = 0; i < list.size(); i++) {
					// c = list.get(list.size() - i - 1);
					// c.index = i + 1;
					// p.addChild(c);
					// System.out.print("<-" + c.getName());
					// p = c;
					// }

					parent.index = 1;
					child.index = 1;
					newRoot.addChild(child);
					newRoot.addChild(parent);
//					 System.out.println("5:" + newRoot.getName() + "<-" +
//					 parent.getName());

					/**
					 * update the height of each node
					 */
					TreeNote p = child, c = null;

//					 System.out.print("5:" + newRoot.getName() + "<-" +
//					 child.getName());
					for (int i = 0; i < list.size(); i++) {
						c = list.get(list.size() - i - 1);
						if (c.index == 0) {
							c.index = p.index + 1;
							p.addChild(c);
//							System.out.print(index + "<-" + c.getName());
							p = c;
						} else {
//							System.out.println("d: " + c.getName() + " index is not 0 -> " + c.index);
						}
					}

//					 System.out.print("\n");
					for (TreeNote element : list) {
						waiteMap.remove(element);
//						 System.out.println("5:waiteMap.remove(" +
//						 element.getName() + ")");
					}
					break;
				}

				/**
				 * e) If P is a node of a tree T, join L into T as a sub-tree.
				 * Due to the limitation of tree height, some nodes in the top
				 * of the list L should be removed and treat each of them as a
				 * new root. Return to step b);
				 */
				if (!waiteMap.containsKey(parent)) {
					int i = 0;
					TreeNote p = parent, c = null;
//					 System.out.print("4:" + p.getName());
					for (; i < list.size(); i++) {
				
						c = list.get(list.size() - i - 1);
						if (c.index == 0) {
							c.index = p.index + 1;
							p.addChild(c);
//							System.out.println("index : " + c.index);
							p = c;
						} else {
//							System.out.println("e: " + c.getName() + " index is not 0 -> " + c.index);
						}
					}
					
					for (TreeNote element : list) {
						waiteMap.remove(element);
//						 System.out.println("4:waiteMap.remove(" +
//						 element.getName() + ")");
					}

					break;
				}
				/**
				 * f)Regard P as C and return to step c).
				 */
//				
				list.add(parent);
//				 System.out.println("6:" + parent.getName() + "<-" +
//				 child.getName());
				child = parent;
			}
					
		}
//		System.out.println("rootmap.size=" + rootMap.size());
		
		/**
		 * g) when the clustering level is end, cut the leaves whose index is out of the height limit
		 */
		Set<TreeNote> leaves = new HashSet<>();
		for (Entry<TreeNote, Tree> rootMapEntry : rootMap.entrySet()) {
			TreeNote root = rootMapEntry.getKey();
			Tree tree = rootMapEntry.getValue();
//			System.out.println("tree size is :" + tree.getNotes().size()); 
			Set<TreeNote> rootNodes = tree.getLeavesInThisLevel();
			int limit = getLimit(rootNodes.size(), this.alpha, this.beta);
			
//			int limit = getLimit_log(rootNodes.size());
			
//			System.out.println("tree :" + root.getName() + "; size is " + rootNodes.size() + "; limit is " + limit);
			leaves.addAll(CutLeaves(root, limit));			
		}
		for (TreeNote treeNote : leaves) {
			rootMap.put(treeNote, new Tree(treeNote));
		}
		return rootMap;
	}

	/**
	 * 
	 */
	Set<TreeNote> CutLeaves(TreeNote root, int limit) {
		Set<TreeNote> leaves = new HashSet<>();
		
		Set<TreeNote> childNotes = root.getChildSet();
		
		for (TreeNote child : childNotes) {
			if (child.index != root.index + 1) continue;
			Set<TreeNote> c = child.getChildSet();
			if (c.size() != 0) {
				Set<TreeNote> l = CutLeaves(child, limit);
				if (l != null) leaves.addAll(l);
			} else {
				if (child.index > limit) {
//					System.out.println("cut :" + child.getName() + ";limit :" + limit + ";index:" + child.index);
					leaves.add(child);
					child.index = 0;
					child.removeChildSet();
				}
			}
		}
		return leaves;
	}
	
	
	
	
	/**
	 * 
	 * @param rootMap
	 * @return
	 */
	Map<TreeNote, Tree> Join(Map<TreeNote, Tree> rootMap) {

		Double minDistance = Double.MAX_VALUE;
		TreeNote[] cuple = new TreeNote[2];
		for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
			TreeNote root = rootEntry.getKey();
			Double[] value = root.getValue();
			for (Entry<TreeNote, Tree> comRootEntry : rootMap.entrySet()) {
				TreeNote comRoot = comRootEntry.getKey();
				if (root != comRoot) {
					Double[] comValue = comRoot.getValue();
					Double distace = Distance.EucDistance(value, comValue);
					if (minDistance > distace) {
						minDistance = distace;
						cuple[0] = root;
						cuple[1] = comRoot;
					}
				}
			}
		}

		Double[] v = new Double[cuple[0].getValue().length];
		for (int i = 0; i < v.length; i++) {
			v[i] = (cuple[0].getValue()[i] + cuple[1].getValue()[i]) / 2;
		}
		String newRootName = "root." + this.newRootNumber;
		this.newRootNumber++;

		TreeNote newRoot = new TreeNote(newRootName, v);
		newRoot.addChild(cuple[0]);
		newRoot.addChild(cuple[1]);
		rootMap.put(newRoot, new Tree(newRoot));

		rootMap.remove(cuple[0]);
		rootMap.remove(cuple[1]);

		return rootMap;
	}

	/**
	 * use this function to start the clustering and get the results
	 * 
	 * @return the clustering results
	 */
	public Integer[] getCluster() {

		Map<TreeNote, Tree> rootMap = new HashMap<>(this.data);
		while (true) {
			Map<TreeNote, Tree> newRootMap = searchRoot(rootMap);
			// System.out.println("rootMap.size()=" + rootMap.size());
			// for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
			// Tree T = rootEntry.getValue();
			// Set<String> c = T.getNotes();
			// System.out.println("root=" + T.getRoot().getName() + ", notes is
			// " + c.size());
			// }
			if (newRootMap.keySet().size() > k) {
				rootMap = newRootMap;
			} else
				break;
		}

		while (rootMap.size() > k) {
			Map<TreeNote, Tree> newRootMap = Join(rootMap);
			rootMap = newRootMap;
		}

		Integer[] cluster = new Integer[this.data.size()];
		int k = 0;
		// System.out.println("rootMap.size()=" + rootMap.size());
		for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
			Tree T = rootEntry.getValue();
			Set<String> c = T.getNotes();
			 System.out.println("root=" + T.getRoot().getName() + ", notes is " + c.size());
			 System.out.println("\"" + k + "\"");
			for (String i : c) {
				// System.out.println(i);
				if (!i.contains("root")) {
					cluster[Integer.parseInt(i)] = k;
					// System.out.println("cluster[" + i + "] = " + k);
					// System.out.println(i + "\t" + k);
				}
			}
			k++;
		}

		return cluster;
	}

	public static void main(String[] args) throws IOException {

		/**
		 * data set
		 */
		// String[] dataName =
		// {"breast-w","ecoli","glass","ionosphere","iris","kdd_synthetic_control","mfeat-fourier","mfeat-karhunen","mfeat-zernike"};
		//
		// int[] k = {2,8,2,2,6,6,10,10,10};
		//
		// String[] dataName =
		// {"optdigits","segment","sonar","vehicle","waveform-5000","letter"};
		//
		// int[] k = {10,7,2,4,3,26};
		//
		//
/**
 * experiment for one set of parameter
 */
		
		String[] dataName = {"iris"};
		//
		 int[] k = {6};
		 Double[][] FM = new Double[200][k.length], AA = new
		 Double[200][k.length];
		//
		 for (int p = 0; p < k.length; p++) {
			// System.out.println(dataName[p]);
	//		 LCRS LS = new LCRS(LoadData.getData("E:\\roughsetdata\\weka\\" +
	//		 dataName[p] + "(data).txt"), 3, k[p]);
	//		 LCRS_Function lcrs = new LCRS_Function(LoadData.getData("E:\\uci-20070111\\" +
	//		 dataName[p] + "(data).txt"), 1, 2);
			 for (int l = 1; l <= 1; l++) {
				
				 for (int i = 0; i < 200; i++) {
					 // Integer[] cluster = cl.getCluster();
					 // CupleCore cc = new
						 LCRS_Function lcrs = new LCRS_Function(LoadData.getData("E:\\uci-20070111\\exp\\" +
								 dataName[p] + "(data).txt"), 0.5, 1.0, k[p]);
			//		 LCRS LS = new LCRS(LoadData.getData("E:\\roughsetdata\\weka\\" +
			//		 dataName[p] + "(data).txt"), l, k[p]);
					 Integer[] cluster = lcrs.getCluster();
					 Estimate es = new Estimate(cluster,
					 LoadData.getLabel("E:\\uci-20070111\\exp\\" + dataName[p] +
					 "(label).txt"));
					 //
					 FM[i][p] = es.getFM(); AA[i][p] = es.getAA();
					 System.out.println("FM:" + FM[i][p] + "\tAA:" + AA[i][p]);
				 }
				
				 BufferedWriter bwFM = new BufferedWriter(new FileWriter(new
				 File("E:\\rs-exp\\LCRS_FUNCTION-"
				 + dataName[p] + "-FM-" + l + ".txt")));
				// bwFM.write("Limited." + dataName[p] + ".FM\n");
				 BufferedWriter bwAA = new BufferedWriter(new FileWriter(new
				 File("E:\\rs-exp\\LCRS_FUNCTION-"
				 + dataName[p] + "-AA-" + l + ".txt")));
				// bwAA.write("Limited." + dataName[p] + ".AA\n");
				 for (int i = 0; i < AA.length; i++) {
					 bwAA.write(AA[i][p] + "\n");
					 bwFM.write(FM[i][p] + "\n");
				 }
				
				 bwFM.close();
				 bwAA.close();
			 }
		 }



//		LCRS_Function LF = new LCRS_Function(LoadData.getData("E:\\rs-exp\\Rrandom(10).txt"), 0.5, 3);
//		Integer[] cluster = LF.getCluster();
//		 for (int i = 0; i < cluster.length; i++) {
//		 System.out.println(cluster[i]);
//		 }
	 
		 /**
		  * experiment for changeable set of   on one dataset
		  */
		/*	 
		 String dataName = "ecoli";
		 int k = 8;
		 double alpha_start = 0.01, alpha_end = 1, beta_start = 0.01, beta_end = 2;
		 double alpha_step = 0.02, beta_step = 0.02;
		 Double[][] AA_area = new Double[(int)((alpha_end-alpha_start)/alpha_step + 1)][(int)((beta_end-beta_start)/beta_step + 1)];
		 
		 BufferedWriter bwAA_area = new BufferedWriter(new FileWriter(new
				 File("E:\\rs-exp\\" + dataName + "-AA_Area.txt")));
		 
		 for (int i = 0; i <= (int)((alpha_end-alpha_start)/alpha_step); i++) {
			 for (int j = 0; j <= (int)((beta_end-beta_start)/beta_step); j++) {
				 AA_area [i][j]= new Double(0.0);
				 for (int r = 0; r < 50; r++) {
					 
					 LCRS_Function lcrs = new LCRS_Function(LoadData.getData("E:\\uci-20070111\\exp\\" +
							 dataName + "(data).txt"), alpha_start+i*alpha_step, beta_start+j*beta_step, k);
					 Integer[] cluster = lcrs.getCluster();
					 Estimate es = new Estimate(cluster,
					 LoadData.getLabel("E:\\uci-20070111\\exp\\" + dataName + "(label).txt"));
					 
					 AA_area [i][j] += es.getAA();
					 
				 }
				 AA_area [i][j] =  AA_area [i][j] / 50;
				 
				 bwAA_area.write((alpha_start+i*alpha_step) + "\t" + (beta_start+j*beta_step) + "\t" + AA_area[i][j] + "\n");
			}
		}
			 
		bwAA_area.close(); 
		 */
	}

}

