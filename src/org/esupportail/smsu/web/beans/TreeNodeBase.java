package org.esupportail.smsu.web.beans;

import java.util.List;

import org.apache.myfaces.custom.tree2.TreeNode;

public class TreeNodeBase implements TreeNode {

	String description;
	
	String identifier;
	
	String type;
	
	public int getChildCount() {
		return getChildren().size();
	}

	public List getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		return this.description;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public String getType() {
		return this.type;
	}

	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
		
	}

	public void setLeaf(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setType(String type) {
		this.type = type;
		
	}

}
