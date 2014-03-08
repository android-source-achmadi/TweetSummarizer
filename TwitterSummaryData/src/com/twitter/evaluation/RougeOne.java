package com.twitter.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RougeOne {
	
	public static void main(String[] args){
		File file = new File("Evaluation.txt");
		String line;
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				try {
					JsonObject o = new JsonParser().parse(line).getAsJsonObject();
					String[] manualSummary = {o.get("manual").getAsString()};
					String[] generatedSummary = {o.get("auto").getAsString()};
					RougeOne rOne = new RougeOne();
					float recall = rOne.getRecall(manualSummary, generatedSummary);
					System.out.println("recall: "+recall);
					float precision = rOne.getPrecision(manualSummary, generatedSummary);
					System.out.println("precision: "+precision);
					float fMeasure = 2*(precision * recall)/(precision+recall);
					System.out.println("fMeasure: "+fMeasure);
				}catch(Exception e){
					System.out.println("Exception exception!!");
					e.printStackTrace();
				}
			}
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
			matchCount = 0;
			
			for (String generatedWord: generatedOneGram){
				for (String manualWord: manualOneGram){
					if(generatedWord.equalsIgnoreCase(manualWord)){
						matchCount++;
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
