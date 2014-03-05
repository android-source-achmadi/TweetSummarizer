package com.twitter.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.data.DFSSearch;
import com.twitter.data.Node;
import com.twitter.data.SentenceTree;
import com.twitter.data.SimpleBestFit;

public class BuildTree {

	public static void main(String[]args){
		File file = new File("all_tweets.txt");
		//ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			CleanTweets cleanTweets = new CleanTweets();
			ArrayList<String> originalTweets = new ArrayList<String>();
			Node finalResult;
			ArrayList<String> originalPhrases = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				try {
					JsonObject o = new JsonParser().parse(line).getAsJsonObject();
					TrendingTopic trendingTopic = new Gson().fromJson(o,TrendingTopic.class);
					trendingTopic.topic = trendingTopic.topic.toLowerCase();
					trendingTopic.topic = trendingTopic.topic.replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
					originalTweets.addAll(trendingTopic.tweets);
					trendingTopic.tweets = cleanTweets.htmlToAscii(trendingTopic.tweets,trendingTopic.topic);
					Iterator<String> i = trendingTopic.tweets.iterator();
					SentenceTree sentenceTree = new SentenceTree();
					Node root = new Node(trendingTopic.topic, 1,0);
					while(i.hasNext()){
						ArrayList<String> sentence = new ArrayList<String>();
						StringTokenizer st = new StringTokenizer((String)i.next());
						while(st.hasMoreTokens()){
							sentence.add(st.nextToken());
						}
						sentenceTree.addSentence(sentence,root);
					}
					System.out.println(trendingTopic.topic);
					sentenceTree.printTree(root);
					DFSSearch search = new DFSSearch();
					root = search.DFSUpdateweight(DFSSearch.RIGHT, root);
					double rightResult = root.getMaxSumweight();
					String rightSummary = root.getMaxweightNodeString();
					root.setMaxSumweight(0);
					root = search.DFSUpdateweight(DFSSearch.LEFT, root);
					double leftResult = root.getMaxSumweight(); 
					String leftSummary = root.getSummary();
					int maxDirection = (rightResult >= leftResult)?DFSSearch.RIGHT:DFSSearch.LEFT;
					System.out.println("The Right Maximum weight : "+ rightResult);
					System.out.println("The Left Maximum weight : "+ leftResult);


					// Building Left Summary because Right Tree has the highest Weight
					if(maxDirection == DFSSearch.RIGHT) {
						System.out.println("Right Summary  & new root: " + rightSummary);
						Iterator<String> i2 = trendingTopic.tweets.iterator();
						SentenceTree leftTree = new SentenceTree();
						Node leftRoot = new Node(rightSummary, 1,0);
						while(i2.hasNext()){

							String nextTweet = (String)i2.next().replaceAll("( )+", " ");
							if(nextTweet.contains(rightSummary)){
								System.out.println("Found a matching tweet:" + nextTweet);
								ArrayList<String> leftSentence = new ArrayList<String>();
								StringTokenizer st = new StringTokenizer(nextTweet);
								String nextToken = st.nextToken() ;
								while((nextToken!= null) && (!nextToken.equals(trendingTopic.topic))){
									leftSentence.add(nextToken);
									nextToken = st.nextToken();

								}
								leftSentence.add(rightSummary);
								leftTree.addSentence(leftSentence,leftRoot);
							}
						}
						//System.out.println("Left Summary Part:");
						leftTree.printTree(leftRoot);
						DFSSearch search2 = new DFSSearch();
						finalResult = search2.DFSUpdateweight(DFSSearch.LEFT, leftRoot);
						System.out.println("The Maximum weight : "+ result.getMaxSumweight());
						System.out.println("The Summarized Tweet : "+result.getMaxweightNodeString());
					}
					// Building Right Summary because Left Tree has the highest Weight
					if(maxDirection == DFSSearch.LEFT) {
						System.out.println("Left Summary  & new root: " + leftSummary );
						Iterator<String> i2 = trendingTopic.tweets.iterator();
						SentenceTree rightTree = new SentenceTree();
						Node rightRoot = new Node(leftSummary, 1,0);
						while(i2.hasNext()){

							String nextTweet = (String)i2.next().replaceAll("( )+", " ");
							if(nextTweet.contains(leftSummary)){
								System.out.println("Found a matching tweet:" + nextTweet);
								ArrayList<String> rightSentence = new ArrayList<String>();
								StringTokenizer st = new StringTokenizer(nextTweet);
								String nextToken = st.nextToken() ;
								rightSentence.add(leftSummary);
								int flag = 0;
								while(nextToken!= null){ 
									if(flag == 1){
										rightSentence.add(nextToken);
									}
									if(nextToken.equals(trendingTopic.topic))
										flag = 1;
									nextToken = st.nextToken();
								}
							}
							rightSentence.add(leftSummary);
							rightTree.addSentence(rightSentence,rightRoot);
						}
					}
					System.out.println("Right Summary Part:");
					rightTree.printTree(rightRoot);
					DFSSearch search3 = new DFSSearch();
					finalResult = search3.DFSUpdateweight(DFSSearch.RIGHT, rightRoot);
					
				}
			}
			
			//System.out.println("The Maximum weight : "+ result.getMaxSumweight());
			//System.out.println("The Summarized Tweet : "+result.getMaxweightNodeString());

			originalPhrases = SimpleBestFit.reconstructTweet(finalResult.getMaxweightNodeString(), originalTweets);
			//					System.out.println("\n==Original==");
			//					for (String originalPhrase : originalPhrases) {
			//						System.out.println(originalPhrase);
			//					}
			//					System.out.println("==End==\n");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			continue;
		}
	}


	br.close();
} catch (FileNotFoundException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch(Exception e2){
	e2.printStackTrace();
}


}
//	public static void main(String[] args) {
//		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
//		ArrayList<String> sentence1 = new ArrayList<String>();
//		sentence1.add("Hey");
//		sentence1.add("Aw,");
//		sentence1.add("comedian");
//		sentence1.add("Soupy");
//		sentence1.add("sales");
//		sentence1.add("died");
//		sentence1.add("of");
//		sentence1.add("cancer");
//		ArrayList<String> sentence2 = new ArrayList<String>();
//		sentence2.add("Great");
//		sentence2.add("man");
//		sentence2.add("Aw,");
//		sentence2.add("comedian");
//		sentence2.add("Soupy");
//		sentence2.add("sales");
//		sentence2.add("died");
//		sentence2.add("due");
//		sentence2.add("to");
//		sentence2.add("cancer");
//		ArrayList<String> sentence3 = new ArrayList<String>();
//		sentence3.add("Great");
//		sentence3.add("Hey");
//		sentence3.add("Aw,");
//		sentence3.add("comedian");
//		sentence3.add("Soupy");
//		sentence3.add("sales");
//		sentence3.add("died");
//		sentences.add(sentence1);
//		sentences.add(sentence2);
//		sentences.add(sentence3);
//		SentenceTree st = new SentenceTree();
//		Node root = new Node("Soupy", 1,0);
//		st.addSentence(sentences.get(0),root);
//		st.addSentence(sentences.get(1),root);
//		st.addSentence(sentences.get(2),root);
//		st.printTree(root);
//		DFSSearch search = new DFSSearch();
//		Node result = search.DFSUpdateweight(DFSSearch.RIGHT, root);
//		
//		System.out.println("The Maximum weight : "+ result.getMaxSumweight());
//		System.out.println("The Summarized Tweet : "+result.getMaxweightNodeString());
//		
//
//	}

}
