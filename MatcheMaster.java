/**
 * 
 */
package org.matcher.words;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


public class MatcheMaster {
	
	
	private static final Logger logger = Logger.getLogger(MatcheMaster.class);
	private int threadPoolSize;
	private List<String> targetToMatch;
	private Map<String, List<String>> matchResults;
	private final String PATTERN_1="(:?[A-Z][a-z]*)"; 
	private final int matchUpTo;
	private BlockingQueue<org.matcher.words.Matcher> matcherQueue =null;
	private boolean isMatcherGeneratingOn = false;
	private ReadWriteLock matcherGenerationStatusLock = new ReentrantReadWriteLock();
	
	public MatcheMaster(List<String> targetToMatch,int matchUpTo,int threadPoolSize) {
		this.threadPoolSize=threadPoolSize;
		this.targetToMatch = targetToMatch;
		matchResults = new ConcurrentHashMap<String,List<String>>(100,0.25F);
		this.matchUpTo=matchUpTo;
		this.matcherQueue = new LinkedBlockingQueue<org.matcher.words.Matcher>();
		System.out.println("Matcher Master Created ........");
		System.out.println("\tThread Pool Size::"+threadPoolSize);
		System.out.println("\tTarget Size :: "+targetToMatch.size());
		System.out.println("\tMatch Upto :: "+matchUpTo);
	}
	
	
	public void doMatching(){
		ExecutorService matherService = Executors.newFixedThreadPool(threadPoolSize);
		List<org.matcher.words.Matcher> matcherList=new ArrayList<org.matcher.words.Matcher>();
		int counter=0;
		try{
			
			setMatcherGeneratingOn(true);
			//startMatchingProcess();
			for (String cItem : getTargetToMatch()) {				
				org.matcher.words.Matcher matcher=new org.matcher.words.Matcher(cItem,getContentToMatch(cItem, getMatchUpTo()), getTargetToMatch());
				matcherQueue.add(matcher);
				counter++;
				if(counter%1000 == 0) {
					List<Future<List<String>>> matcherFutureResults = matherService.invokeAll(matcherList);			
					List<String> cResultList = null;
					for(Future<List<String>> cMatch : matcherFutureResults ){
							cResultList = cMatch.get();
							Collections.sort(cResultList);
							matchResults.put(cResultList.get(0),cResultList);
					}
					matcherList=new ArrayList<org.matcher.words.Matcher>();
				}
			}
			
			List<Future<List<String>>> matcherFutureResults = matherService.invokeAll(matcherList);			
			List<String> cResultList = null;
			for(Future<List<String>> cMatch : matcherFutureResults ){
					cResultList = cMatch.get();
					Collections.sort(cResultList);
					matchResults.put(cResultList.get(0),cResultList);
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setMatcherGeneratingOn(false);
			matherService.shutdown();
		}
	}
	public void startMatchingProcess(){
		ExecutorService matherService = Executors.newFixedThreadPool(threadPoolSize);
		Future<List<String>> currentMatchResult = null;
		List<String> currentMatchList = null;
		org.matcher.words.Matcher currentMatcher = null;
		String matcherToken = null;
		
		while (true){			
			try{
				if(isMatcherGeneratingOn() || !matcherQueue.isEmpty()) {
					currentMatcher = matcherQueue.poll(10,TimeUnit.MILLISECONDS);
					if(currentMatcher!=null) {
						currentMatchResult = matherService.submit(currentMatcher);
						currentMatchList = currentMatchResult.get();
						matcherToken = currentMatchList.get(0);
						Collections.sort(currentMatchList);
						matchResults.put(matcherToken,currentMatchList);
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			} finally{
				matherService.shutdown();
			}
		}
	}
	public String getContentToMatch(String content,int noOfWords) {
		StringBuffer toMatch = new StringBuffer(); 
		List<String> tokenList = new ArrayList<String>();
		Pattern pattern=Pattern.compile(PATTERN_1);
		Matcher matcher = pattern.matcher(content);
		String strIDx;
		/*Adding Tokens to List*/
		while(matcher.find()){
			strIDx=matcher.group();			
			tokenList.add(strIDx);			
		}		
		/*Creating word from token list to search*/
		for(int idx=tokenList.size()-noOfWords; (idx>0 && idx<tokenList.size());idx++){
			toMatch.append(tokenList.get(idx));
		}
		return toMatch.toString();
	}
	 
	
	
	/**
	 * @return the isMatcherGeneratingOn
	 */
	public boolean isMatcherGeneratingOn() {
		boolean res=false;
		
		matcherGenerationStatusLock.readLock().lock();
		res = isMatcherGeneratingOn;
		matcherGenerationStatusLock.readLock().unlock();
		return res;
	}


	/**
	 * @param isMatcherGeneratingOn the isMatcherGeneratingOn to set
	 */
	public void setMatcherGeneratingOn(boolean isMatcherGeneratingOn) {
		matcherGenerationStatusLock.writeLock().lock();
		this.isMatcherGeneratingOn = isMatcherGeneratingOn;
		matcherGenerationStatusLock.writeLock().unlock();
	}


	/**
	 * @return the matchUpTo
	 */
	public int getMatchUpTo() {
		return matchUpTo;
	}

	/**
	 * @return the targetToMatch
	 */
	public List<String> getTargetToMatch() {
		return targetToMatch;
	}

	/**
	 * @param targetToMatch the targetToMatch to set
	 */
	public void setTargetToMatch(List<String> targetToMatch) {
		this.targetToMatch = targetToMatch;
	}

	/**
	 * @return the matchResults
	 */
	public Map<String, List<String>> getMatchResults() {
		return matchResults;
	}

	/**
	 * @param matchResults the matchResults to set
	 */
	public void setMatchResults(Map<String, List<String>> matchResults) {
		this.matchResults = matchResults;
	}
	
	public static void main(String []a){
		MatcheMaster mm=new MatcheMaster(null,3,9);
		System.out.println(mm.getContentToMatch("FtoProductSpecificType-Enrichment",2));
	}
	public void printMatchResult(){
		System.out.println("Match Result.................");
		Iterator<String> iterator = getMatchResults().keySet().iterator();
		String cKey=null;
		while(iterator.hasNext()){
			 cKey = iterator.next();
			 System.out.println(cKey +" # "+getMatchResults().get(cKey));
		}
	}
		
}
