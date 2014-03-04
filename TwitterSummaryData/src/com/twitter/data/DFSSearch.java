package com.twitter.data;

import java.util.ArrayList;

public class DFSSearch {
	public final static int LEFT = 1;
	public final static int RIGHT = 2;

	public Node DFSUpdateweight(int direction, Node node) {
		// stop condition
//		//System.out.println(node.getLabel());
		ArrayList<Node> children;
		
		if (direction == DFSSearch.LEFT) {
			children = node.getLeft();
		} else {
			children = node.getRight();
		}
		
		if (children.size() == 0) {
			node.setMaxSumweight(node.getweight());
			return node;
		}
		
		// node has been visited
		if(node.getMaxSumweight() != 0){
			//System.out.println(node.getLabel() + " has been visited");
			return node;
		}

		// recursive
		Node maxNode = null;
		for (int i = 0; i < children.size(); i++) {
			Node child = DFSUpdateweight(direction, children.get(i));
			if (maxNode == null) {
				maxNode = child;
			} else if (maxNode.getMaxSumweight() < child.getMaxSumweight()) {
				maxNode = child;
			}
		}
		node.setMaxSumweight(node.getweight() + maxNode.getMaxSumweight());
		node.setMaxweightNode(maxNode);
		return node;
	}

	public static void main(String args[]) {
		Node root = new Node("0", 1);
		Node node3 = new Node("3", 2);
		Node node1 = new Node("1", 1);
		Node node2 = new Node("2", 3);
		Node node4 = new Node("4", 4);
		Node node5 = new Node("5", 10);
		Node node6 = new Node("6", 1);
		Node node7 = new Node("7", 1);
		Node node8 = new Node("8", 1);
		node3.getRight().add(node5);
		node3.getRight().add(node6);
		node4.getRight().add(node7);
		node4.getRight().add(node8);
		node1.getRight().add(node3);
		node2.getRight().add(node3);
		node2.getRight().add(node4);
		root.getRight().add(node1);
		root.getRight().add(node2);
		DFSSearch search = new DFSSearch();
		Node result = search.DFSUpdateweight(DFSSearch.RIGHT, root);
		//System.out.println(result.getMaxSumweight());
		//System.out.println(result.getMaxweightNodeString());
	}
}
