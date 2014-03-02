package com.twitter.evaluation;

public class RougeOne {
	
	public static void main(String[] args){
		String[] manualSummary = {"this,is a sentence","like this"};
		String[] generatedSummary = {"this is ","like that"};
		RougeOne rOne = new RougeOne();
		float recall = rOne.getRecall(manualSummary, generatedSummary);
		System.out.println(recall);
		float precision = rOne.getPrecision(manualSummary, generatedSummary);
		System.out.println(precision);
		float fMeasure = 2*(precision * recall)/(precision+recall);
		System.out.println(fMeasure);
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
