/**
 * 
 */
package org.matcher.words;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MatchersFileReader {
	private String filePath;
	
	public MatchersFileReader(String  filePath) {
		this.filePath = filePath;
	}
	
	public String replaceFileExtenssion(String fileName) {
		String result=fileName;
		int idx=0;
		if(fileName!=null && (idx=fileName.indexOf('.'))>0) {
				result = fileName.substring(0,idx);
		}
		return result;
	}
	
	public List<String> getFileContents() {
		List<String> contentList = new ArrayList<String>();
		File file = new File(filePath);		
		BufferedReader reader=null;
		try{
			if(file.exists() && file.isFile() && file.canRead() ){
				reader =  new BufferedReader(new FileReader(file));
				String line=null;
				while((line = reader.readLine()) != null ){
					contentList.add(replaceFileExtenssion(line));
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try{
				reader.close();				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return contentList;
	}
	
	public static void main(String []a) {
		MatchersFileReader r=new MatchersFileReader(null);
		System.out.println(r.replaceFileExtenssion("FtoProductSpecificType-Enrichment.java"));
	}
}
