package com.twitter.data;
/*TODO: try to remove hashtags*/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.gson.Gson;

public class SummaryData {
	static Properties applicationProperties;
    static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY";
    static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET";
    static final String PREF_KEY_OAUTH_TOKEN = "PREF_KEY_OAUTH_TOKEN";
    static final String PREF_KEY_OAUTH_SECRET = "PREF_KEY_OAUTH_SECRET";
    static Twitter twitter;
    
    static{
    	InputStream inputStream=null;
    	try{
	    	inputStream  = SummaryData.class.getResourceAsStream("twitterApplication.properties");
	    	applicationProperties = new Properties();
	    	applicationProperties.load(inputStream);
	    	
    	}catch(Exception ioe){
    		//System.out.println("The applicationProperties file was not found. Exiting Application");
    		//System.exit(1);
    	}finally{
    		try{
    			inputStream.close();
    		}catch(IOException e){
    			//System.out.println("Exception closing Application Properties input stream");
    		}
    	}
    }
    
    public static void main(String[] args){
    	Gson gson = new Gson();
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true);
    	cb.setOAuthConsumerKey(applicationProperties.getProperty(TWITTER_CONSUMER_KEY));
        cb.setOAuthConsumerSecret(applicationProperties.getProperty(TWITTER_CONSUMER_SECRET));
        cb.setOAuthAccessToken(applicationProperties.getProperty(PREF_KEY_OAUTH_TOKEN));
        cb.setOAuthAccessTokenSecret(applicationProperties.getProperty(PREF_KEY_OAUTH_SECRET));
        int count = 0;
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	twitter = tf.getInstance();
    	SummaryData twitterSummaryData = new SummaryData();
    	String[] trendingTopics = twitterSummaryData.getTrendingTopics();
    	HashMap<String,ArrayList> twitterDataSet = new HashMap<String,ArrayList>();
    	try  
		{
		    FileWriter fstream = new FileWriter("4Mar9pm.txt", false); //true tells to append data.
		    BufferedWriter out = null;
		    out = new BufferedWriter(fstream);
	    	for(int i=0;i<trendingTopics.length;i++){
		    	Query query = new Query(trendingTopics[i]);
		    	ArrayList<String> tweetsData = new ArrayList<String>();
		    	/*TODO: Change this date dynamically to 24 hours*/
		    	query.setSince("2014-02-21");
		    	query.setCount(100);
		    	QueryResult result;
				try {
					do{
						result = twitter.search(query);
				    	for (Status tweet : result.getTweets())  
				    	{  
				    		if("en".equals(tweet.getIsoLanguageCode()) && !tweet.getText().contains("F0LL0WERS")  && !tweet.getText().toLowerCase().contains("followers")){
				    			if(tweet.getHashtagEntities().length<=3)
				    				tweetsData.add(tweet.getText());
				    		}
				    	}
					}while((query=result.nextQuery())!=null);
				String[] tweetsArray = new String[10];
				/*write the tweets to a file */
					if(tweetsData.size()>=250){
						String json = gson.toJson(new TrendingTopic(URLDecoder.decode(trendingTopics[i], "ASCII"),tweetsData));
						//System.out.println(json);
						out.write(json+"\n");
					}
				}catch (TwitterException e) {
					e.printStackTrace();
				} 
	    	}
	    	
	    	out.close();
	    }catch (IOException e)
		{
		    //System.out.println("Error: " + e.getMessage());
		} 
    }
    
    public String[] getTrendingTopics(){
    	String[] trendingTopicsS = new String[10];
    	/*UK,USA,Canada,Australia,England*/
    	int[] weoids = {23424975,23424977,23424775,23424748};
    	HashSet<String> trendingTopics = new HashSet<String>();
    	try{
    		for(int weoid : weoids){
		    	Trends trends = twitter.getPlaceTrends(weoid);
		    	Trend[] trendsArray = trends.getTrends();
		    	for(Trend trend: trendsArray){
		    		if(trend.getName().startsWith("#")){
		    			continue;
		    		}
		    		trendingTopics.add(trend.getQuery());
		    	}
    		}
    	}catch(Exception e){
    		//System.out.println("Exception while retreiving trends "+e);
    	}
    	return trendingTopics.toArray(trendingTopicsS);
    }
    
}

class TrendingTopic{
	String topic;
	ArrayList<String> tweets;
	int id;
	String autoSummary;

	public TrendingTopic(String topic,ArrayList<String> tweets){
		this.topic = topic;
		this.tweets = tweets;
		this.autoSummary = " ";
	}
}
