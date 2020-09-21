package org.kevin.clustering.util;

import java.util.HashSet;
import java.util.Set;

public class Tree {
	TreeNote root;
	public Tree(TreeNote root) {
		this.root = root;	
	}
	
	public TreeNote getRoot() {
		return this.root;
	}
	
	public TreeNote searchNote(TreeNote root, String aimNoteName) {
		String rootName = root.getName();
		if (!rootName.equals(aimNoteName)) {
			Set<TreeNote> childSet = root.getChildSet();
			if (childSet.size() > 0) {
				for (TreeNote childNote : childSet) {
					TreeNote result = searchNote(childNote, aimNoteName);
					if (result != null) {
						return result;
					}
				} 
				return null;
			} else {
				return null;
			}
		} else {
			return root;
		}
		
	}
	
	public Set<String> getNotes() {
		return getChildSet(this.root);
	}
	
	Set<String> getChildSet(TreeNote note) {
		Set<TreeNote> childNotes = note.getChildSet();
		Set<String> childSet = new HashSet<String>();
		childSet.add(note.getName());
		
		if (childNotes == null) return childSet;
		else {
			for (TreeNote treeNote : childNotes) {
				childSet.addAll(getChildSet(treeNote));
			}
			return childSet;
		}
		
//		Set<TreeNote> childNotes = note.getChildSet();
//		Set<String> childSet = new HashSet<String>();
//		childSet.add(note.getName());
//		if (childNotes != null) {
//			for (TreeNote treeNote : childNotes) {
//				Set<String> s = getChildSet(treeNote);
//				if (s != null) {
//					childSet.addAll(s);
//				} 
//			}
//		}
//		return childSet;
	}
	
	

	public Set<String> getLeaves() {
		return searchLeaves(this.root);
	}
	
	public Set<String> searchLeaves(TreeNote note) {
		Set<TreeNote> childNotes = note.getChildSet();
		Set<String> leavesSet = new HashSet<String>();
		if (childNotes == null) {
			leavesSet.add(note.noteName);
			return leavesSet;
		} else {
			for (TreeNote child : childNotes) {
				leavesSet.addAll(searchLeaves(child));
			}
			return leavesSet;
		}		
	}
	
	public Set<TreeNote> getLeavesInThisLevel() {
		return getChildNoteSet(this.root);
	}
	
	Set<TreeNote> getChildNoteSet(TreeNote note) {
		Set<TreeNote> childNotes = note.getChildSet();
		Set<TreeNote> childSet = new HashSet<TreeNote>();		
		
		if (childNotes != null) {
			for (TreeNote treeNote : childNotes) {
				
				if (note.index == treeNote.index - 1) {
//					System.out.println(note.index + "<-" + treeNote.index);
					childSet.addAll(getChildNoteSet(treeNote));
					childSet.add(treeNote);	
				}
			}
				
		}

//		System.out.println("childset size is " + childSet.size());
		return childSet;
	}
	
}
