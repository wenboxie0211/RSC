package org.kevin.clustering.hierarchical.rootsearching.lcrs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import org.kevin.clustering.hierarchical.linkage.AverageLinkage;
import org.kevin.clustering.util.Distance;
import org.kevin.clustering.util.LoadData;
import org.kevin.clustering.util.Node;


/**
 * LCRS_Function Algorithm
 * 
 * @author WenboXie limited-cuplecore-function-similarity input: using function
 *         to caculate the height threshold (parameter is alpha)
 */
public class Experiments_on_Face_Dataset {
	/**
	 * the number of cluster
	 */

	int l;
	/**
	 * the parametor to caculate the limit value of tree height
	 */
	double alpha;

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
	public Experiments_on_Face_Dataset(Double[][] sim, double alpha, int l) {
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

	/**
	 * 
	 * @param data
	 * @return the map which discribe the nearest nearbor of nodes
	 */
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

	/**
	 * searching roots in one level
	 * 
	 * @param data
	 * @return map in which keys are the index of the roots and values are root
	 *         nodes
	 */
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

				Double ef = Math.pow((ad * ad + bc * bc + ac * ac + bd * bd - ab * ab - cd * cd) / 4, 0.5);

				s.put(comNode, ef);
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
			
				Double ad = Math.pow((ab * ab + ac * ac)/2 - (bc * bc)/4, 0.5);
//				System.out.println("sim<" + node.getName() + ", <" + comNode.getName() + ", " + ef + ">>");
				s1.put(comNode, ad);
				s2.put(node, ad);
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

	/**
	 * 
	 */
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

	/**
	 * 
	 * @param rootMap
	 * @return
	 */
	Set<String> Noise(Set<String> rootMap) {
		boolean re = true;
		while(re) {
			re = false;
			String[] roots = rootMap.toArray(new String[0]);
			double[][] dis = new double[roots.length][roots.length];
			
			for (int i = 0; i < roots.length; i++) {
				for (int j = 0; j < roots.length; j++) {
					dis[i][j] = dis[j][i] = this.sim.get(roots[i]).get(roots[j]);
					
				}
			}
					
			for (int i = 0; i < roots.length; i++) {
				Set<String> c = getNotes(roots[i]);
				if (c.size() <= 3) {
					double min = Double.MAX_VALUE;
					int m_index = 0;
					for (int j = 0; j < dis[i].length; j++) {
						if (i==j) continue;
						if (dis[i][j] < min) {
							min = dis[i][j];
							m_index = j;
						}
					}
					this.data.get(roots[m_index]).addChild(roots[i]);
					rootMap.remove(roots[i]);
					re = true;
					break;
				}
				
			}
		}
		return rootMap;
		
	}
	/**
	 * 
	 * @param rootMap
	 * @return
	 */
	Set<String> Join(Set<String> rootMap) {
//		System.out.println("join + 1");
		Double minDistance = Double.MAX_VALUE;
		String[] cuple = new String[2];
		for (String root : rootMap) {
			for (String comRoot : rootMap) {
				if (root != comRoot) {
//					System.out.println("get sim<" + root.getName() + ", " + comRoot.getName() + ">");
					Double distace = this.sim.get(root).get(comRoot);
					if (minDistance > distace) {
						minDistance = distace;
						cuple[0] = root;
						cuple[1] = comRoot;
		}}}}
//		System.out.println("remove:" + cuple[0].getName()+","+cuple[1].getName());
		rootMap.remove(cuple[0]);
		rootMap.remove(cuple[1]);
		
//		for (Entry<TreeNote, Tree> rootEntry : rootMap.entrySet()) {
//			TreeNote node = rootEntry.getKey();
//			node.setL(node);
//			node.setR(node);
//		}
		String newRootName = "root." + this.newRootNumber;
		this.newRootNumber++;
		Node newRoot = new Node(newRootName, cuple[0], cuple[1]);
		this.data.put(newRootName, newRoot);
//		this.data.get(cuple[0]).index = 1;
//		this.data.get(cuple[1]).index = 1;
				
		newRoot.addChild(cuple[0]);
		newRoot.addChild(cuple[1]);
		Map<String, Double> s2 = new HashMap<>();
		for (String node : rootMap) {
			
			Map<String, Double> s1 = this.sim.get(node);
			
//			Map<TreeNote, Double> s2 = this.sim.get(newRoot);
			String l2 = newRoot.getL();
			String r2 = newRoot.getR();
			Double ab = this.sim.get(node).get(l2);
			Double ac = this.sim.get(node).get(r2);
			Double bc = this.sim.get(l2).get(r2);
			
			Double ad = Math.pow((ab * ab + ac * ac)/2 - (bc * bc)/4, 0.5);
//			System.out.println("sim<" + node.getName() + ", <" + comNode.getName() + ", " + ef + ">>");
			s1.put(newRootName, ad);
//			System.out.println("update sim:" + node.getName() +","+newRootName);
			s2.put(node, ad);
			
//			System.out.println("update sim:" + newRootName +","+node.getName());
		}
		
		this.sim.put(newRootName, s2);	
		
		rootMap.add(newRootName);
//		System.out.println("add:" + newRootName);
//		updateSim(rootMap);
		
		return rootMap;
	}

	/**
	 * pre-calculate the number of clusters
	 */
	int preClustering(Set<String> rootMap) {
		int clusters = rootMap.size();
		
		int[][] nearDim = new int[clusters][clusters];
		Map<String, Integer> m = new HashMap<>();
		
		String[] l = rootMap.toArray(new String[0]);
		
		for (int i = 0; i < l.length; i++) {
			m.put(l[i], i);
		}
		
		for (int i = 0; i < l.length; i++) {
			String node = l[i];
			Map<String, Double> dis = this.sim.get(node);

			Double minDistance = Double.MAX_VALUE;
			for (Entry<String, Double> comEntry : dis.entrySet()) {
				
				String comNode = comEntry.getKey();
				if (!rootMap.contains(comNode)) continue;
				
				if (node != comNode) {
					Double d = comEntry.getValue();
					if (d < minDistance) {
						minDistance = d;
			}}}
			
			for (Entry<String, Double> comEntry : dis.entrySet()) {
				String comNode = comEntry.getKey();
				if (!rootMap.contains(comNode)) continue;
				if (node != comNode && comEntry.getValue() == minDistance) {
					nearDim[i][m.get(comNode)] = 1;
				}				
			}
		}
		
		
		int[][] preClusterDim = new int[clusters][clusters];
		for (int i = 0; i < preClusterDim.length; i++) {
			for (int j = 0; j < preClusterDim[i].length; j++) {
				preClusterDim[i][j] = nearDim[i][j] + nearDim[j][i];
			}
		}
//		for (int i = 0; i < preClusterDim.length; i++) {
//			for (int j = 0; j < preClusterDim[i].length; j++) {
//				System.out.print("["+preClusterDim[i][j]+"]");
//			}
//			System.out.print("\n");
//		}
		
//		Map<Integer, Integer> setNo = new HashMap<>();
		int[] setNo = new int[preClusterDim.length];
		int no = 0;
		for (int i = 0; i < preClusterDim.length; i++) {
			int isRoot = 0;
			for (int j = 0; j < preClusterDim[i].length; j++) {
				if (preClusterDim[i][j] == 2) {
					isRoot = 1;
					break;
			}}
			if (isRoot == 0) continue;
			if (setNo[i] == 0) {
				for (int j = 0; j < preClusterDim[i].length; j++) {
					if (preClusterDim[i][j] != 2) continue;
					if (setNo[j] != 0) {
						setNo[i] = 1;
						break;
					}						
				}
				if (setNo[i] == 0) {
					setNo[i] = 1;
					no++;
				}
				for (int j = 0; j < preClusterDim[i].length; j++) {
					if (preClusterDim[i][j] != 2) continue;
					setNo[j] = 1;
				}
				
			} else {		
				for (int j = 0; j < preClusterDim[i].length; j++) {
					if (preClusterDim[i][j] != 2) continue;
					setNo[j] = 1;
				}
			}
		}
		return no;
	}
	
	
	
	/**
	 * use this function to start the clustering and get the results
	 * 
	 * @return the clustering results
	 */
	public String[] getCluster() {

		Set<String> rootMap = new HashSet<>(this.data.keySet());
		for(int l=0; l<this.l; l++) {
//			int pc = preClustering(rootMap);
//			System.out.println("------preCluster number is "+ pc + ". this is new clustering round-----");
//			if (pc < k) break;
//			System.out.println("------this is new clustering round-----");
			rootMap = searchRoot(rootMap);
//			System.out.println("rootMap.size()=" + rootMap.size());
			if (rootMap.size() ==1) break;
//			
//			
//			if (newRootMap.size() > k) {
//				rootMap = newRootMap;				
//			} else
//				break;
//			for (String root : rootMap) {
//				Set<String> c = getNotes(root);
//				System.out.println("\"" + k + "\"��root=" + root + ", notes is " + c.size());
//				System.out.println("->" + c);
//			}
		}
//		System.out.println("------less than k, using join fuction-----");
//		for (String root : rootMap) {
//			Set<String> c = getNotes(root);
//			System.out.println("\"" + k + "\"��root=" + root + ", notes is " + c.size());
//			System.out.println("->" + c);
//			}
//		for (String root : rootMap) {
//			Set<String> c = getNotes(root);
//			System.out.println("\"" + k + "\"��root=" + T.getRoot().getName() + ", notes is " + c.size());
//			System.out.println(c+"->");
//		}
		
//		rootMap = Noise(rootMap);
//		System.out.println("rootMap.size()=" + rootMap.size());
//		while (rootMap.size() > k) {
//			Set<String> newRootMap = Join(rootMap);
//			rootMap = newRootMap;
//			System.out.println("rootMap.size = " + rootMap.size());
//		}
//		System.out.println("------finish the clustering-----");
		String[] cluster = new String[this.dataSize];
		int k = 0;
		// System.out.println("rootMap.size()=" + rootMap.size());
		for (String root : rootMap) {
			Set<String> c = getNotes(root);
//			System.out.println("\"" + k + "\"��root=" + root + ", notes is " + c.size());
//			System.out.println("->" + c);
						 
			for (String i : c) {
				// System.out.println(i);
				if (!i.contains("root")) {
					cluster[Integer.parseInt(i)] = k+"";
					// System.out.println("cluster[" + i + "] = " + k);
					// System.out.println(i + "\t" + k);
				}
			}
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
		 * experiment for FACE
		 */
	
		int l = 2;
		Double FM = 0.0, AA = 0.0, RI = 0.0;
		double alpha = 2;
		//
		
			String f = "/Users/wenboxie/Data/ssim_cwssim.csv";
			BufferedReader br = new BufferedReader(new FileReader(new File(f)));
			Double[][] similarity = new Double[400][400];
			String line;
			for (int i = 0; i < similarity.length; i++) {
				similarity[i][i] = 0.0;
			}
			while((line = br.readLine()) != null) {
				String[] a = line.split(",");
				similarity[Integer.parseInt(a[0]) - 1][Integer.parseInt(a[1]) - 1] = 
						Double.parseDouble(a[3]);
			}
			br.close();
			

				Experiments_on_Face_Dataset rs = new Experiments_on_Face_Dataset(similarity, alpha, l);

				String[] cluster = rs.getCluster();
				Estimate es = new Estimate(cluster,
						LoadData.getLabel("/Users/wenboxie/Data/Olivetti(label).txt"));
//				//
				FM= es.getFM();
				AA= es.getAA();
				RI =es.getRI();
				System.out.println("RS -- FM:" + FM + "\tAA:" + AA+ "\tRI:" + RI);
				Set<String> cc = new HashSet<>();
				for (int j = 0; j < cluster.length; j++) {
					cc.add(cluster[j]);
				}
				AverageLinkage AL = new AverageLinkage(similarity, cc.size());
				Integer[] cluster_al = AL.getCluter();

				Estimate es_al = new Estimate(cluster_al,
						LoadData.getLabel("/Users/wenboxie/Data/Olivetti(label).txt"));
				 FM = es_al.getFM();
				AA = es_al.getAA();
				RI = es_al.getRI();
				System.out.println("Group -- FM:" + FM + "\tAA:" + AA+ "\tRI:" + RI);
				System.out.println("K = " + cc.size());
	
	}

}
