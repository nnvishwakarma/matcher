/**
 * 
 */
package org.matcher.words;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;


public class Matcher implements Callable<List<String>>{
	
	private static final Logger logger = Logger.getLogger(Matcher.class);
	private String taskName;
	private String stringToMatch;
	private List<String> targetData;
	
	public Matcher(String taskName,String stringToMatch,List<String> targetData) {
		this.taskName=taskName;
		this.stringToMatch=stringToMatch;
		this.targetData=targetData;
		//System.out.println("Matcher Created For :: "+taskName +" Matching Token :: "+stringToMatch);
	}
	
	/**
	 * @return the stringToMatch
	 */
	public String getStringToMatch() {
		return stringToMatch;
	}

	/**
	 * @param stringToMatch the stringToMatch to set
	 */
	public void setStringToMatch(String stringToMatch) {
		this.stringToMatch = stringToMatch;
	}

	/**
	 * @return the targetData
	 */
	public List<String> getTargetData() {
		return targetData;
	}

	/**
	 * @param targetData the targetData to set
	 */
	public void setTargetData(List<String> targetData) {
		this.targetData = targetData;
	}
	

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public List<String> call() throws Exception {
		
		List<String> resList=null;
		Set<String> tempSet = new HashSet<String>();		
		tempSet.add(getTaskName()); /*Initial Match is Task Itself*/
		for(String item : getTargetData()) {
			if(isMatchFoundCase1(getStringToMatch(), item)) {
				tempSet.add(item);
			}
		}		
		resList = new ArrayList<String>(tempSet);
		
		return resList;
	}
	
	public boolean isMatchFoundCase1(String toMatch,String matcher){
		if(matcher.endsWith(toMatch)) {
			return true;
		}
		else { 
			return false;
		}
	}

}
