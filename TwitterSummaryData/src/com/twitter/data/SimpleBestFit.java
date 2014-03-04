package com.twitter.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class SimpleBestFit {
	
	public static ArrayList<String> reconstructTweet(String finalTweet, ArrayList<String> tweets) {

		ArrayList<String> expansionToCheck = new ArrayList<String>();
		Enumeration<String> hashCheck = CleanTweets.HashMap.keys();
		
		// Expansion to check.
		// After this method, we get the expansionTerms and the finalTweet without any of the expansionTerms
		while(hashCheck.hasMoreElements()){
			String key = hashCheck.nextElement();
			String val = CleanTweets.HashMap.get(key);
			if(finalTweet.contains(val)){
				expansionToCheck.add(val);
				expansionToCheck.add(key);
				finalTweet = finalTweet.replace(val, "");
				System.out.println(finalTweet);
			}
		}
		
		System.out.println(finalTweet);
		
		Hashtable<String, Boolean> resultTweets = new Hashtable<String, Boolean>();

		String[] terms = finalTweet.split("\\s+");
		
		// get all the original tweets containing terms that is not expansion keyword
		for (int i = 0; i < terms.length; i++) {
			String term = terms[i];
			ArrayList<String> tmp = new ArrayList<String>();
			for (int j = 0; j < tweets.size(); j++) {
				if (tweets.get(j).toLowerCase().contains(term)) {
					tmp.add(tweets.get(j));
				}
			}
			tweets = tmp;
		}
		
		
		// If there is no expansion in the original tweet, just return the ones not containing expansion
		if(expansionToCheck.size()==0){
			return tweets;
		}
		
		// get all original tweets containing keyword. It's OR
		for (int i = 0; i < expansionToCheck.size(); i++) {
			for (String tweet : tweets) {
				System.out.println(tweet);
				System.out.println(expansionToCheck.get(i));
				if (tweet.toLowerCase().contains(expansionToCheck.get(i))){
					if (!resultTweets.containsKey(tweet)){
						resultTweets.put(tweet, true);
					}
				}
			}
		}
		
		// result dump
		ArrayList<String> result = new ArrayList<String>();
		Enumeration<String> resultTmp = resultTweets.keys();
		while(resultTmp.hasMoreElements()){
			result.add(resultTmp.nextElement());
		}
		
		return result;
	}
	
	// This function is for CleanTweets not containing abbreviation expansion
	/*
	 * public static ArrayList<String> reconstructTweet(String finalTweet, ArrayList<String> tweets) {
		String[] terms = finalTweet.split("\\s+");
		for (int i = 0; i < terms.length; i++) {
			String term = terms[i];
			ArrayList<String> tmp = new ArrayList<String>();
			for (int j = 0; j < tweets.size(); j++) {
				if (tweets.get(j).toLowerCase().contains(term)) {
					tmp.add(tweets.get(j));
				}
			}
			tweets = tmp;
		}
		return tweets;
	}
	 * */
}
