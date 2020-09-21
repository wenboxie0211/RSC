package org.kevin.clustering.util;

import java.util.HashSet;
import java.util.Set;

public class TreeNote {
	Double[] value;
	Set<TreeNote> childSet = new HashSet<TreeNote>();
	TreeNote l,r;
	String noteName;
	public Integer index;
	
	public TreeNote(TreeNote node) {
		this.value = new Double[node.value.length];
		for (int i = 0; i < this.value.length; i++) {
			this.value[i] = node.value[i];
		}
		for (TreeNote c : node.childSet) {
			this.childSet.add(new TreeNote(c));
		}
		this.noteName = node.noteName;
		this.index = node.index;
	}
	public TreeNote(String nodeName, TreeNote node) {
		this.l = node.getL();
		this.r = node.getR();
		for (TreeNote c : node.childSet) {
			this.childSet.add(new TreeNote(c));
		}
		this.noteName = node.noteName;
		this.index = node.index;
	}
	public TreeNote(String name) {
		this.noteName = name;
		this.childSet = new HashSet<TreeNote>();
		this.index = 0;
	}
	public TreeNote(String name, TreeNote l, TreeNote r) {
		this.noteName = name;
		this.childSet = new HashSet<TreeNote>();
		this.l=l;
		this.r=r;
		this.index = 0;
	}
	public void setL(TreeNote l) {
		this.l = l;
	}
	public void setR(TreeNote r) {
		this.r = r;
	}
	
	public TreeNote(String name, Double[] v) {
		this.noteName = name;
		this.value = v;
		this.childSet = new HashSet<TreeNote>();
		this.index = 0;
	}
	
	public TreeNote(Double[] v) {
		this.value = v;
		this.childSet = new HashSet<TreeNote>();
		this.index = 0;
	}
	
	public void setValue(Double[] v) {
		this.value = v;
	}
	
	public void addChild(TreeNote child) {
		this.childSet.add(child);
	}
	
	public Double[] getValue() {
		return this.value;
	}
	public TreeNote getL() {
		return this.l;
	}
	public TreeNote getR() {
		return this.r;
	}
	
	public Set<TreeNote> getChildSet() {
		return this.childSet;
	}
	
	public void removeChildSet() {
		this.childSet = new HashSet<TreeNote>();
	}
	
	
	public String getName() {
		return this.noteName;
	}
	
	public void deleteChildNote(String childName) {
		for (TreeNote note : this.childSet) {
			if(note.getName().equals(childName)) {
				this.childSet.remove(note);
				break;
			}
		}
	}
	public void deleteChildNote(TreeNote child) {
		for (TreeNote note : this.childSet) {
			if(note == child) {
				this.childSet.remove(note);
//				System.out.println("��" + noteName + "��ɾȥ��" + note.getName());
				break;
			}
		}
	}
}
