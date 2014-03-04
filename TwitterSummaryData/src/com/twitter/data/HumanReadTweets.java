package com.twitter.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HumanReadTweets{
	public static void main(String[] args){
		File file = new File("selected_tweets.txt");
		File fileOut = new File("human_select_tweets.txt");
		
		
		BufferedReader br;
		BufferedWriter bw;
		try {
			FileWriter fw = new FileWriter(fileOut.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				JsonObject o = new JsonParser().parse(line).getAsJsonObject();
				TrendingTopic trendingTopic = new Gson().fromJson(o,TrendingTopic.class);
				trendingTopic.topic = trendingTopic.topic.toLowerCase();
				bw.write("Topic "+trendingTopic.topic+"\n");
				int count = 0;
				for(String tweet: trendingTopic.tweets){
					bw.write(tweet+"\n");
					count++;
					if(count>=100) break;
				}
				bw.write("=======================================\n");
			}
			bw.close();
		}catch(IOException e){
			System.out.println("File not found");
	}
	}
}