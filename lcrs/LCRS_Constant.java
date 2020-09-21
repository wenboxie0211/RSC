package org.kevin.clustering.hierarchical.rootsearching.lcrs;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.kevin.clustering.util.Distance;
import org.kevin.clustering.util.LoadData;
import org.kevin.clustering.util.Tree;
import org.kevin.clustering.util.TreeNote;
/**
 * LCRS Algorithm
 * @author WenboXie
 */
public class LCRS_Constant {
	/**
	  * the value to limit the height of trees 
	  */
	int limit, k;
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
	public LCRS_Constant(Double[][] data, int limit, int k) {
		this.data = new HashMap<>();
		for (int i = 0; i < data.length; i++) {
			TreeNote root = new TreeNote(i + "", data[i]);
			this.data.put(root, new Tree(root));
		}
		this.limit = limit;
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
			for (Entry<TreeNote, Tree> comEntry: data.entrySet()) {
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
	 * @param data
	 * @return map in which keys are the index of the roots and values are root nodes
	 */
	Map<TreeNote, Tree> searchRoot(Map<TreeNote, Tree> data) {		
		/**
		 * a)	Put all the nodes into a candidate set;
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
		  * b)	If the candidate set is not empty, choose a node from the candidate set
		  *	randomly, and add it into a List L as the starting node, otherwise finish this clusteringlevel ;
		  */
		while(waiteMap.size() > 0) {
			
			TreeNote[] keys = waiteMap.keySet().toArray(new TreeNote[0]);
			TreeNote child = keys[new Random(System.currentTimeMillis()).nextInt(keys.length)];			
			List<TreeNote> list = new LinkedList<>();
			list.add(child);
	
			while(true) {
				/**
				 * c)	Search the nearest node of C and regard it as its parent node P;
				 */
				TreeNote parent = nearby.get(child);							
				
				/**
				 * d)	If P has already been in the list L, create a new node R which is the centroid 
				 *	of C and P. Treat L as a tree with root R and return to step b) ;
				 */	
				if(list.contains(parent)) {

					Double[] childValue = child.getValue();
					Double[] parentValue = parent.getValue();
					Double[] v = new Double[childValue.length];
					for (int i = 0; i < v.length; i++) {
						v[i] = (childValue[i] + parentValue[i]) / 2;
					}
					String newRootName = "root." + this.newRootNumber;
					this.newRootNumber ++;
					TreeNote newRoot = new TreeNote(newRootName, v);
					
					rootMap.put(newRoot, new Tree(newRoot));
//					System.out.println("5:rootMap.put(" + newRoot.getName() + ")");
					
//					TreeNote p = newRoot, c = null;
//					System.out.print("5:" + p.getName());
//					for (int i = 0; i < list.size(); i++) {
//						c = list.get(list.size() - i - 1);
//						c.index = i + 1;
//						p.addChild(c);
////						System.out.print("<-" + c.getName());
//						p = c;
//					}
					
					parent.index = 1;
					child.index = 1;
					newRoot.addChild(child);
					newRoot.addChild(parent);
//					System.out.println("5:" + newRoot.getName() + "<-" + parent.getName());
					
					/**
					 * update the height of each node
					 */
					TreeNote p = child, c = null;
					
//					System.out.print("5:" + newRoot.getName() + "<-" + child.getName());
					for (int i = 0, index = 1; i < list.size(); i++) {
						c = list.get(list.size() - i - 1);
						if (c != child && c != parent) {
							c.index = ++index;
							p.addChild(c);
//							System.out.print("<-" + c.getName());
							p = c;
						}
					}
					
//					System.out.print("\n");
					for (TreeNote element : list) {
						waiteMap.remove(element);
//						System.out.println("5:waiteMap.remove(" + element.getName() + ")");
					}					
					break;
				}
				
				/**
				 * e)	If P is a node of a tree T, join L into T as a sub-tree. Due to the limitation of 
				 * tree height, some nodes in the top of the list L should be removed and treat each
				 * of them as a new root. Return to step b);
				 */
				if(!waiteMap.containsKey(parent)) {
					Integer index = new Integer(parent.index);
					int i = 0;
					TreeNote p = parent, c = null;
//					System.out.print("4:" + p.getName());
					for (; index < this.limit && i < list.size(); i++, index++) {
						c = list.get(list.size() - i - 1);
						c.index = index + 1;
						p.addChild(c);
//						System.out.print("<-" + c.getName());
						p = c;
					}
//					System.out.print("\n");
					for (; i < list.size(); i++) {
						TreeNote fNode = list.get(list.size() - i - 1);
						rootMap.put(fNode, new Tree(fNode));
//						System.out.println("4:rootMap.put(" + fNode.getName() + ")");
//						System.out.println("4:" + fNode.getName() + "<-Null");
					}
					
					for (TreeNote element : list) {
						waiteMap.remove(element);
//						System.out.println("4:waiteMap.remove(" + element.getName() + ")");
					}

					break;
				}
				/**
				 * f)	If the length of the list exceed the limitation, some nodes in the top of the list L 
				 *	should be removed and treat each of them as a new root. Regard P as C and return
				 *	to step c).
				 */					
				if (list.size() > this.limit) {
					TreeNote fNode = list.get(0);
					rootMap.put(fNode, new Tree(fNode));
//					System.out.println("6:rootMap.put(" + fNode.getName() + ")");
					list.remove(0);
					waiteMap.remove(fNode);
//					System.out.println("6:waiteMap.remove(" + fNode.getName() + ")");
//					System.out.println("6:" + fNode.getName() + "<-Null");
				} 					
				list.add(parent);
//				System.out.println("6:" + parent.getName() + "<-" + child.getName());
				child = parent;				
			}			
		}		
		return rootMap;
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
		this.newRootNumber ++;
		
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
	 * @return the clustering results
	 */
	public Integer[] getCluster() {
		
		Map<TreeNote, Tree> rootMap = new HashMap<>(this.data);
		while (true) {
			Map<TreeNote, Tree> newRootMap = searchRoot(rootMap);
//			System.out.println("rootMap.size()=" + rootMap.size());
//			for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
//				Tree T = rootEntry.getValue();
//				Set<String> c = T.getNotes();
//				System.out.println("root=" + T.getRoot().getName() + ", notes is " + c.size());
//			}
			if (newRootMap.keySet().size() > k) {
				rootMap = newRootMap;
			} else break;
		}
		
		while (rootMap.size() > k) {
			Map<TreeNote, Tree> newRootMap = Join(rootMap);
			rootMap = newRootMap;
		}
		
		Integer[] cluster = new Integer[this.data.size()];
		int k = 0;
//		System.out.println("rootMap.size()=" + rootMap.size());
		for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
			Tree T = rootEntry.getValue();
			Set<String> c = T.getNotes();
//			System.out.println("root=" + T.getRoot().getName() + ", notes is " + c.size());
//			System.out.println("\"" + k + "\"");
			for (String i : c) {
//				System.out.println(i);
				if(!i.contains("root")) {
					cluster[Integer.parseInt(i)] = k;
//					System.out.println("cluster[" + i + "] = " + k);
//					System.out.println(i + "\t" + k);
				}				
			}
			k ++;
		}
		
		return cluster;
	}

	
	public static void main(String[] args) throws IOException {
		
//		String[] dataName = {"breast-w","ecoli","glass","ionosphere","iris","kdd_synthetic_control","mfeat-fourier","mfeat-karhunen","mfeat-zernike"};
//
//		int[] k = {2,8,2,2,6,6,10,10,10};
//		
//		String[] dataName = {"optdigits","segment","sonar","vehicle","waveform-5000"};
//	
//		int[] k = {9,7,2,4,3};
//	
//	
		
//		String[] dataName = {"sonar"};
////		
//		int[] k = {2};
//		Double[][] FM = new Double[50][k.length], AA = new Double[50][k.length];
//		
//		for (int p = 0; p < k.length; p++) {
//			System.out.println(dataName[p]);
////			LCRS LS = new LCRS(LoadData.getData("E:\\roughsetdata\\weka\\" + dataName[p] + "(data).txt"), 3, k[p]);
////			CentroidLinkage cl = new CentroidLinkage(LoadData.getData("E:\\roughsetdata\\weka\\" + dataName[p] + "(data).txt"), k[p]);
//			for (int l = 1; l <= 1; l++) {
//				
//			
//				for (int i = 0; i < 50; i++) {				
//	//				Integer[] cluster = cl.getCluster();
//	//				CupleCore cc = new CupleCore(LoadData.getData("E:\\roughsetdata\\weka\\" + dataName[p] + "(data).txt"), k[p]);
//					LCRS LS = new LCRS(LoadData.getData("E:\\roughsetdata\\weka\\" + dataName[p] + "(data).txt"), l, k[p]);
//					Integer[] cluster = LS.getCluster();
//					Estimate es = new Estimate(cluster, LoadData.getLabel("E:\\roughsetdata\\weka\\" + dataName[p] + "(label).txt"));
//	//	
//					FM[i][p] = es.getFM(); AA[i][p] = es.getAA();
//					System.out.println("FM:" + FM[i][p] + "\tAA:" + AA[i][p]);
//				}
//				
//				BufferedWriter bwFM = new BufferedWriter(new FileWriter(new File("E:\\roughsetdata\\results\\LimitedCupleCore\\LimitedCupleCore-" + dataName[p] + "-FM-" + l + ".txt")));
////				bwFM.write("Limited." + dataName[p] + ".FM\n");
//				BufferedWriter bwAA = new BufferedWriter(new FileWriter(new File("E:\\roughsetdata\\results\\LimitedCupleCore\\LimitedCupleCore-" + dataName[p] + "-AA-" + l + ".txt")));
////				bwAA.write("Limited." + dataName[p] + ".AA\n");
//				for (int i = 0; i < AA.length; i++) {
//					bwAA.write(AA[i][p] + "\n");
//					bwFM.write(FM[i][p] + "\n");
//				}
//				
//				bwFM.close();
//				bwAA.close();
//			}
//		}
		
		LCRS_Constant LS = new LCRS_Constant(LoadData.getData("E:\\roughsetdata\\Rrandom(200).txt"), 100, 3);
		Integer[] cluster = LS.getCluster();
//		for (int i = 0; i < cluster.length; i++) {
//			System.out.println(cluster[i]);
//		}
		
	}
}
