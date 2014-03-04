package com.twitter.data;

import java.util.ArrayList;

public class Node {
	private String label;
	private double weight;
	private ArrayList<Node> left;
	private ArrayList<Node> right;
	private Node maxweightNode;
	private double maxSumweight;
	private double count; 
	private double distance; 

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Node() {
		left = new ArrayList<Node>();
		right = new ArrayList<Node>();
	}

	public Node(String label, double count, double weight) {
		this();
		this.label = label;
		this.count = count;
		this.weight = weight;
	}
	
	public Node(String label, double weight){
		this();
		this.label = label;
		this.weight = weight;
	}

	public double getweight() {
		return weight;
	}

	public void setweight(double weight) {
		this.weight = weight;
	}

	public double getMaxSumweight() {
		return maxSumweight;
	}

	public void setMaxSumweight(double maxSumweight) {
		this.maxSumweight = maxSumweight;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getcount() {
		return count;
	}

	public void setcount(double count) {
		this.count = count;
	}

	public ArrayList<Node> getLeft() {
		return left;
	}

	public void setLeft(ArrayList<Node> left) {
		this.left = left;
	}

	public ArrayList<Node> getRight() {
		return right;
	}

	public void setRight(ArrayList<Node> right) {
		this.right = right;
	}

	public Node getMaxweightNode() {
		return maxweightNode;
	}

	public void setMaxweightNode(Node maxweightNode) {
		this.maxweightNode = maxweightNode;
	}
	
	public String getMaxweightNodeString(){
		if(this.maxweightNode != null){
			return this.label + " " + this.maxweightNode.getMaxweightNodeString();
		}
		return this.label;
	}
}
