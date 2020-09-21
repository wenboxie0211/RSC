package org.kevin.clustering.util;

import java.util.HashSet;
import java.util.Set;

public class Node {
	Set<String> childSet;
	String l,r;
	String nodeName;
	public Integer index;
	
	public Node(String name) {
		this.nodeName = name;
		this.childSet = new HashSet<String>();
		this.index = 0;
		l = name;
		r = name;
	}
	public Node(String name, String L, String R) {
		this.nodeName = name;
		this.childSet = new HashSet<String>();
		this.l=L;
		this.r=R;
		this.index = 0;
	}
	public String getName() {
		return this.nodeName;
	}
	public void setL(String L) {
		this.l = L;
	}
	public void setR(String R) {
		this.r = R;
	}
	public String getL() {
		return this.l;
	}
	public String getR() {
		return this.r;
	}
	public void addChild(String child) {
		this.childSet.add(child);
	}
	public Set<String> getChildSet() {
		return this.childSet;
	}
	public void removeChildSet() {
		this.childSet = new HashSet<String>();
	}
}
