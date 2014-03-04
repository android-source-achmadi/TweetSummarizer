package com.twitter.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class SentenceTree {
	public HashMap<Double,ArrayList<Node>> LeftDistNodeList = new HashMap<Double,ArrayList<Node>>();
	public HashMap<Double,ArrayList<Node>> RightDistNodeList = new HashMap<Double,ArrayList<Node>>();
	public File stopWords = new File("StopWords.txt");


	/** Check if a word is a stop word 
	 * 
	 * @param word
	 * @return true if its a Stop Word, if not false
	 */
	public boolean isStopWord(String word) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(stopWords));
			String line;
			while ((line = br.readLine()) != null) {

				if(word.equals(line) )
					return true;

			}

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return false;
	}
	/**
	 * Check if a word is a child (in inner node list) on one side of a node (decided by the direction)
	 * @param word: word
	 * @param adjacent: adjacnet node
	 * @param direction: direction deciding the list, whether on the left on right
	 * @return return node that is the same as word, else return NULL node if not found.
	 */
	public Node isAChild(String word, Node adjacent, int direction) {

		Node child = new Node("NULL",0,0);
		Iterator<Node> itl = adjacent.getLeft().iterator();
		Iterator<Node> itrw = adjacent.getRight().iterator();
		if(direction == DFSSearch.RIGHT){
			while(itrw.hasNext()){
				Node temp2 = itrw.next();
				String label2 = temp2.getLabel();
				if(label2.equals(word)){
					child = temp2;
					break;
				}
			}
		}
		if(direction == DFSSearch.LEFT){
			while(itl.hasNext()){
				Node temp = itl.next();
				String label = temp.getLabel();
				if(label.equals(word)){
					child = temp;
					break;
				}
			}
		}

		return child;
	}

	/**
	 * Check to see whether a word with specific distance belongs to the distance list (corresponding to distance)
	 * @param word: Word
	 * @param distance: Distance
	 * @param direction: Direction
	 * @return Node corresponding distance in list (direction) 
	 */
	public Node inDistNodeList(String word, double distance,int direction){
		Double dist = new Double(distance);
		Node flagNode = new Node("NULL",0,0);
		if(direction == DFSSearch.LEFT){
			if(LeftDistNodeList.containsKey(dist)) {
				ArrayList<Node> tempList = LeftDistNodeList.get(dist);
				int length = tempList.size();
				for(int i=0 ; i<length;i++) {
					Node temp = tempList.get(i);
					// increase frequency if node is found
					if(temp.getLabel().equals(word)){
						double freq;

						freq = temp.getcount() + 1;
						temp.setcount(freq);

						flagNode = temp;
					}
				}
			}
		}
		if(direction == DFSSearch.RIGHT){
			if(RightDistNodeList.containsKey(dist)) {
				ArrayList<Node> tempList = RightDistNodeList.get(dist);
				int length = tempList.size();
				for(int i=0 ; i<length;i++) {
					Node temp = tempList.get(i);
					if(temp.getLabel().equals(word)){
						double freq;

						freq = temp.getcount() + 1;
						temp.setcount(freq);

					}
				}
			}
		}
		return flagNode;
	}

	/**
	 * Add node with corresponding distance and direction into DistanceList
	 * @param node: Node
	 * @param distance: Corresponding distance
	 * @param direction: Corresponding direction List
	 */
	public void addToDistList(Node node,double distance,int direction) {
		if(direction == DFSSearch.LEFT){
			if(LeftDistNodeList.containsKey(distance)) {
				ArrayList<Node> tempList = LeftDistNodeList.get(distance);
				tempList.add(node);
			}
			else {
				ArrayList<Node> tempList = new ArrayList<Node>();
				Double dist =  new Double(distance);
				tempList.add(node);
				LeftDistNodeList.put(dist,tempList);
			}	
		}
		if(direction == DFSSearch.RIGHT){
			if(RightDistNodeList.containsKey(distance)) {
				ArrayList<Node> tempList = RightDistNodeList.get(distance);
				tempList.add(node);
			}
			else {
				ArrayList<Node> tempList = new ArrayList<Node>();
				Double dist =  new Double(distance);
				tempList.add(node);
				RightDistNodeList.put(dist,tempList);
			}
		}
	}

	/**
	 * Add word to corresponding tree, coming from the root and with corresponding direction.
	 * @param word: Word
	 * @param adjacent: Root
	 * @param direction: Direction deciding the corresponding left|right list.
	 * @return Constructed node
	 */
	public Node addWordToTree(String word, Node adjacent, int direction){
		Node currentWord = isAChild(word,adjacent,direction);
		Node x = inDistNodeList(word,adjacent.getDistance()+1,direction);

		//Found in the Sentence Tree
		if(currentWord.getLabel().equals(word)){
			//Alredy present, increment count
		}

		String currentLabel = currentWord.getLabel();
		//double currentDist = adjacent.getDistance() +1.0;

		String xLabel = x.getLabel();
		//System.out.println("currentLabel = "+currentLabel);

		// Node is absent in both Tree and Distance List
		if(currentLabel.equals("NULL") && xLabel.equals("NULL")) {
			//System.out.println("Node is absent in both Tree and Distance List ");
			currentWord = new Node(word,1.0,0.0);



			if(direction == DFSSearch.LEFT){
				// no left node,create newnode
				adjacent.getLeft().add(currentWord);
				currentWord.getRight().add(adjacent);
			}
			if(direction == DFSSearch.RIGHT) {
				//no right node,create newnode
				adjacent.getRight().add(currentWord);
				currentWord.getLeft().add(adjacent);
			}
			addToDistList(currentWord,adjacent.getDistance()+1,direction);
		}	

		if(currentLabel.equals("NULL") && (xLabel.equals(word))) {
			//System.out.println("Node is present in the Tree at the same distance but adjacent to some other node ");
			//Already present at the same distance, increment count and modify the left,right ref

			double w = x.getcount();
			currentWord.setcount(w);
			currentWord.setLabel(word);

			if(direction == DFSSearch.LEFT){
				// no left node,create newnode
				adjacent.getLeft().add(currentWord);
				currentWord.getRight().add(adjacent);
			}
			if(direction == DFSSearch.RIGHT) {
				//no right node,create newnode
				adjacent.getRight().add(currentWord);
				currentWord.getLeft().add(adjacent);
			}
		}
		double distance = adjacent.getDistance() + 1.0;
		currentWord.setDistance(distance);
		calculateWeight(currentWord);
		return currentWord;
	}

	public void calculateWeight(Node node){
		double weight = node.getcount() - node.getDistance() * Math.log(node.getcount());
		if(!isStopWord(node.getLabel()))
			node.setweight(weight);
		else
			node.setweight(0);
	}

	/**
	 * MAIN: Add sentence to the root node. This constructs the terms graph.
	 * @param sentence
	 * @param root
	 */
	public void addSentence(ArrayList<String> sentence, Node root) {
		int length = sentence.size();
		String rootWord = root.getLabel();
		int rootIndex = sentence.indexOf(rootWord);
		Node temp = root;

		for(int i=rootIndex-1;i>=0;i--) { // Left Tree

			temp = addWordToTree(sentence.get(i),temp,DFSSearch.LEFT);
			//System.out.println("Current Node: "+temp.getLabel()+" Frequency = "+temp.getcount()+" Distance = "+temp.getDistance()+ " Weight = "+temp.getweight()+"\n");
		}
		temp = root;
		for(int i=rootIndex+1;i<length;i++) { // Right Tree

			temp = addWordToTree(sentence.get(i),temp,DFSSearch.RIGHT);
			//System.out.println("Right Current Node: "+temp.getLabel()+" Frequency = "+temp.getcount()+" Distance = "+temp.getDistance()+ " Weight = "+temp.getweight()+"\n");
		}

	}

	/**
	 * Print tree corresponding to distance node list
	 * @param root: Start node
	 */
	public void printTree(Node root){

		int distListLength = LeftDistNodeList.size();
		System.out.println("Root Node : " + root.getLabel());
		System.out.println("Left Tree :");
		for(int i=1;i<=distListLength;i++) {
			Double d = new Double(i);
			ArrayList<Node> tempList = LeftDistNodeList.get(d);
			if(tempList != null) {
				int nodeListLength = tempList.size();
				for(int j=0 ; j<nodeListLength;j++) {
					Node temp = tempList.get(j);
//					System.out.println("Distance: " + i + " ,\tLabel: " + temp.getLabel() + " ,\tCount: "+ temp.getcount() +" ,\tWeight: "+temp.getweight());
				}
			}
		}
		System.out.println();
		System.out.println("Right Tree :"); 
		distListLength = RightDistNodeList.size();
		//while(RkeyIterator.hasNext()){
		//	Double key = RkeyIterator.next();
		for(int k=1;k<=distListLength;k++) {
			Double d = new Double(k);
			ArrayList<Node> RtempList = RightDistNodeList.get(d);
			if(RtempList != null) {
				int RnodeListLength = RtempList.size();
				for(int l=0 ; l<RnodeListLength;l++) {
					Node temp = RtempList.get(l);
//					System.out.println("Distance: " + k + " ,\tLabel: " + temp.getLabel()+ " ,\tCount: "+ temp.getcount()+" ,\tWeight: "+temp.getweight());
				}
			}
		}
	}

}
