package com.twitter.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CleanTweets {
	
	/**
	 * Expansion Rules to clean tweet
	 */
	public static final Hashtable<String, String> HashMap = new Hashtable<String, String>(){
		private static final long serialVersionUID = 1L;

		{	
			put("don't", "do not");
			put("doesn't", "does not");
			put("i'm", "i am");
			put("it's", "it is");
			put("there's", "there is");
			put("he's", "he is");
			put("she's", "she is");
			put("you're", "you are");
			put("we're", "we are");
			put("they're", "they are");
			put("there're", "there are");
			put("i'd", "i would");
			put("he'd", "he would");
			put("she'd", "she would");
			put("you'd", "you would");
			put("we'd", "we would");
			put("they'd", "they would");
			put("it'll", "it will");
			put("i'll", "i will");
			put("he'll", "he will");
			put("she'll", "she will");
			put("you'll", "you will");
			put("we'll", "we will");
			put("they'll", "they will");
			
		}
	};
	
	public static void main(String[]args){
		File file = new File("test3");
		TrendingTopic[] trendingTopics = new TrendingTopic[56];
		int topicCount = 0;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			CleanTweets cleanTweets = new CleanTweets();
			while ((line = br.readLine()) != null) {
				try {
						System.out.println(line);
						JsonObject o = new JsonParser().parse(line).getAsJsonObject();
						TrendingTopic trendingTopic = new Gson().fromJson(o,TrendingTopic.class);
						trendingTopic.topic = trendingTopic.topic.toLowerCase();
						trendingTopic.topic = trendingTopic.topic.replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
						trendingTopic.tweets = cleanTweets.htmlToAscii(trendingTopic.tweets,trendingTopic.topic);
						System.out.println(trendingTopic.tweets.size());
						Iterator<String> i = trendingTopic.tweets.iterator();
						while(i.hasNext()){
							System.out.println((String)i.next());
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				break;
			}
			
			
			br.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch(Exception e2){
			e2.printStackTrace();
		}
		
		
	}
	
	ArrayList<String> htmlToAscii(ArrayList<String> tweets, String topic) throws UnsupportedEncodingException{
		ArrayList<String> tweetSentences = new ArrayList<String>();
		String[] tempSentences;
		Iterator<String> i = tweets.iterator();
		String tweet;
		System.out.println("size before 1:"+tweets.size());
		while(i.hasNext()){
			tweet = (String)i.next();
			/*Convert HTML characters to ASCII */
			tweet = StringEscapeUtils.unescapeHtml(tweet);
			/*Convert Unicode characters to ASCII*/
			byte[] bytes = tweet.getBytes("ASCII");
			tweet = new String(bytes, "ASCII");
			/*Remove any unsupported ASCII characters, ie Unicode characters not in ASCII*/
			tweet = tweet.replace("?", "");
			/*Remove HTML tags*/
			tweet = tweet.replaceAll("\\<[^>]*>","");
			/*Remove any URL's */
			tweet = removeUrl(tweet);
			/*remove RT phrases*/
			tweet = tweet.replace("RT", "");
			/*remove @ from @mention*/
			tweet = tweet.replace("@", "");
			/*convert alll tweets to lower case*/
			tweet = tweet.toLowerCase();
			/* abbreviation expansion */
			tweet = abbreviationExpand(tweet);
			/*replace multiple '.' with a single one*/
			tweet = tweet.replaceAll("\\.+", ".");
			/*replace all non-word characters*/
			tweet = tweet.replaceAll("[^_\\p{L}\\p{Nd}\\s]", " ");
			/* remove noise one single character */
			tweet = tweet.replaceAll("\\s{1}[^ai]{1}\\s{1}", " ");
			tweet = tweet.replaceAll("\\s{1}[^ai]{1}$", "");
			tweet = tweet.trim();
			tempSentences=breakIntoSentences(tweet);
			for(String tempSentence: tempSentences){
				if (tempSentence.matches(".*\\b"+topic+"\\b.*")){
				//if(tempSentence.contains(topic)){
					tweetSentences.add(tempSentence);
				}
			}
		}
		System.out.println("after "+tweetSentences.size());
		return tweetSentences;
	}
	
	private String abbreviationExpand(String line){
		String result = line;
		Enumeration<String> e = HashMap.keys();
		while(e.hasMoreElements()){
			String key = e.nextElement();
			String val = HashMap.get(key);
			result = result.replaceAll(key, val);
		}
		return result;
	}
	
	private String[] breakIntoSentences(String line){
		InputStream modelIn = null;
		String[] sentences = null;
		try {
			modelIn = new FileInputStream("en-sent.bin");
		    SentenceModel model = new SentenceModel(modelIn);
		    SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		    sentences = sentenceDetector.sentDetect(line);
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
		return sentences;
	}
	
	private String removeUrl(String commentstr)
    {
		try{
	        String commentstr1=commentstr;
	        String urlPattern = "((https?|ftp|gopher|telnet|file|http):((//)|(\\\\))*[\\w\\d:#@%/;$~_?\\+-=\\\\\\.&]*)";
	        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
	        Matcher m = p.matcher(commentstr1);
	        int i=0;
	        while (m.find()) {
	            commentstr1=commentstr1.replaceAll(m.group(i),"").trim();
	            i++;
	        }
	        return commentstr1;
	    }catch(PatternSyntaxException e){
	    	e.printStackTrace();
	    	return null;
	    }
    }
}
