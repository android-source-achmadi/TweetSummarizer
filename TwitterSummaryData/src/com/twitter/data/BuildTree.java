package com.twitter.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
		File file = new File("demo_tweets.txt");
		//ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		Gson gson = new Gson();
		ArrayList<TrendingTopic> summarizedTrendingTopics = new ArrayList<TrendingTopic>();
		JsonObject summarizedTrendingTopicsObject = new JsonObject();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			FileWriter fstream = new FileWriter("rawData.json", false); //true tells to append data.
		    BufferedWriter out = null;
		    
		    out = new BufferedWriter(fstream);
			String line;
			int idCounter = 0;
			CleanTweets cleanTweets = new CleanTweets();
			ArrayList<String> originalTweets = new ArrayList<String>();
			Node finalResult = new Node();
			ArrayList<String> originalPhrases = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				try {
					JsonObject o = new JsonParser().parse(line).getAsJsonObject();
					TrendingTopic trendingTopic = new Gson().fromJson(o,TrendingTopic.class);
					trendingTopic.topic = trendingTopic.topic.toLowerCase();
					trendingTopic.topic = trendingTopic.topic.replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
					originalTweets.clear();
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
					System.out.println("============================================");
					System.out.println("Trending Topic: "+ trendingTopic.topic);
					trendingTopic.id = ++idCounter;
				//	sentenceTree.printTree(root);
					DFSSearch search = new DFSSearch();
					root = search.DFSUpdateweight(DFSSearch.RIGHT, root);
					double rightResult = root.getMaxSumweight();
					String rightSummary = root.getMaxweightNodeString();
					root.setMaxSumweight(0);
					root = search.DFSUpdateweight(DFSSearch.LEFT, root);
					double leftResult = root.getMaxSumweight(); 
					String leftSummary = root.getSummary();
					int maxDirection = (rightResult >= leftResult)?DFSSearch.RIGHT:DFSSearch.LEFT;
					// Building Left Summary because Right Tree has the highest Weight
					if(maxDirection == DFSSearch.RIGHT) {
					//	System.out.println("Right Summary  & new root: " + rightSummary);
						Iterator<String> i2 = trendingTopic.tweets.iterator();
						SentenceTree leftTree = new SentenceTree();
						Node leftRoot = new Node(rightSummary, 1,0);
						while(i2.hasNext()){

							String nextTweet = (String)i2.next().replaceAll("( )+", " ");
							if(nextTweet.contains(rightSummary)){
						//		System.out.println("Found a matching tweet:" + nextTweet);
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
				//	leftTree.printTree(leftRoot);
						DFSSearch search2 = new DFSSearch();
						finalResult = search2.DFSUpdateweight(DFSSearch.LEFT, leftRoot);
					}
					// Building Right Summary because Left Tree has the highest Weight
					if(maxDirection == DFSSearch.LEFT) {
						//System.out.println("Left Summary  & new root: " + leftSummary );
						Iterator<String> i2 = trendingTopic.tweets.iterator();
						SentenceTree rightTree = new SentenceTree();
						Node rightRoot = new Node(leftSummary, 1,0);
						while(i2.hasNext()){

							String nextTweet = (String)i2.next().replaceAll("( )+", " ");
							if(nextTweet.contains(leftSummary)){
								ArrayList<String> rightSentence = new ArrayList<String>();
								StringTokenizer st = new StringTokenizer(nextTweet);
								String nextToken = st.nextToken() ;
								rightSentence.add(leftSummary);
								int flag = 0;
								while(st.hasMoreTokens()){ 
									if(flag == 1){
										rightSentence.add(nextToken);
									}
									if(nextToken.equals(trendingTopic.topic))
										flag = 1;
									if(st.hasMoreTokens())
										nextToken = st.nextToken();
								}
								rightSentence.add(leftSummary);
								rightTree.addSentence(rightSentence,rightRoot);
							}
							
						}
					//	System.out.println("Right Summary Part:");
						//rightTree.printTree(rightRoot);
						DFSSearch search3 = new DFSSearch();
						finalResult = search3.DFSUpdateweight(DFSSearch.RIGHT, rightRoot);
					}
			//System.out.println("The Maximum weight : "+ result.getMaxSumweight());
			System.out.println("The Summarized Tweet : "+finalResult.getMaxweightNodeString());

			originalPhrases = SimpleBestFit.reconstructTweet(finalResult.getMaxweightNodeString(), originalTweets);
								System.out.println("Summarized Tweet: ");
								for (String originalPhrase : originalPhrases) {
									trendingTopic.autoSummary=originalPhrase;
									System.out.print(originalPhrase);
									System.out.println("============================================");
									break;
							}
		trendingTopic.tweets.clear();
		trendingTopic.tweets.addAll(originalTweets);
		while(trendingTopic.tweets.size()>200){
			trendingTopic.tweets.remove(trendingTopic.tweets.size()-1);
		}
		FileWriter individualFstream = new FileWriter("dataUI/"+trendingTopic.id+".json", false); //true tells to append data.
		BufferedWriter individualOut = new BufferedWriter(individualFstream);
		JsonObject individualTopic = new JsonObject();
		individualTopic.add(""+trendingTopic.id, (new JsonParser().parse(gson.toJson(trendingTopic))).getAsJsonObject());
		individualOut.write(gson.toJson(individualTopic));
		individualOut.close();
		trendingTopic.tweets.clear();
		summarizedTrendingTopicsObject.add(""+trendingTopic.id,(new JsonParser().parse(gson.toJson(trendingTopic))).getAsJsonObject());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			continue;
		}
	}
	String jsonData = gson.toJson(summarizedTrendingTopicsObject);
	out.write(jsonData);	
	out.close();
	br.close();
} catch (FileNotFoundException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch(Exception e2){
	e2.printStackTrace();
}

}

}
