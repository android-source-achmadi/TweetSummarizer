package com.twitter.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RougeOne {
	
	public static void main(String[] args){
		File file = new File("EvaluationRaw.txt");
		String line;
		BufferedReader br;
		ArrayList<String> manualList = new ArrayList<String>();
		ArrayList<String> generatedList = new ArrayList<String>();
		String[] manualSummary = new String[1];
		String[] generatedSummary = new String[1];
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				try {
					JsonObject o = new JsonParser().parse(line).getAsJsonObject();
					manualList.add(o.get("manual").getAsString());
					generatedList.add(o.get("auto").getAsString());
				}catch(Exception e){
					System.out.println("Exception exception!!");
					e.printStackTrace();
				}
			}
			manualSummary = manualList.toArray(manualSummary);
			generatedSummary = generatedList.toArray(generatedSummary);
			RougeOne rOne = new RougeOne();
			float recall = rOne.getRecall(manualSummary, generatedSummary);
			float precision = rOne.getPrecision(manualSummary, generatedSummary);
			float fMeasure = 2*(precision * recall)/(precision+recall);
			System.out.println(precision+"\t"+recall+"\t"+fMeasure);
		
		}catch(IOException ie){
			System.out.println(ie);
		}
	}
	
	public float getRecall(String[] manualSummaries, String[] generatedSummaries){
		String manualSummary,generatedSummary;
		int matchCount=0;
		int manualSummaryCount=0;
		String[] manualOneGram = null,generatedOneGram = null;
		if(manualSummaries==null || generatedSummaries==null || manualSummaries.length!=generatedSummaries.length){
			return 0.00f;
		}
		for(int i=0;i<manualSummaries.length;i++){
			manualSummary = manualSummaries[i];
			generatedSummary = generatedSummaries[i];
			manualOneGram = manualSummary.split("[^\\p{L}\\p{Nd}]");
			generatedOneGram = generatedSummary.split("[^\\p{L}\\p{Nd}]");
			//matchCount = 0;
			
			for (String manualWord: manualOneGram){
				for (String generatedWord: generatedOneGram){
					if(generatedWord.equalsIgnoreCase(manualWord)){
						matchCount++;
						break;
					}
				}
			}
			manualSummaryCount+=manualOneGram.length;
		}
		return ((float)matchCount/(float)manualSummaryCount);
	}
	
	public float getPrecision(String[] manualSummaries, String[] generatedSummaries){
		String manualSummary,generatedSummary;
		int matchCount = 0;
		int generatedSummaryCount=0;
		String[] manualOneGram = null,generatedOneGram = null;
		if(manualSummaries==null || generatedSummaries==null || manualSummaries.length!=generatedSummaries.length){
			return 0.00f;
		}
		for(int i=0;i<manualSummaries.length;i++){
			manualSummary = manualSummaries[i];
			generatedSummary = generatedSummaries[i];
			manualOneGram = manualSummary.split("[^\\p{L}\\p{Nd}]");
			generatedOneGram = generatedSummary.split("[^\\p{L}\\p{Nd}]");
			
			
			for (String generatedWord: generatedOneGram){
				for (String manualWord: manualOneGram){
					if(generatedWord.equalsIgnoreCase(manualWord)){
						matchCount++;
					}
				}
			}
			generatedSummaryCount+=generatedOneGram.length;
		}
		return ((float)matchCount/(float)generatedSummaryCount);
	}

}
