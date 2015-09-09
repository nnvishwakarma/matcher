/**
 * 
 */
package org.matcher.words;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 *
 */
public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	private static final int threadPoolSize =8;
	
	public static void main(String a[]){
		PropertyConfigurator.configure("log4j.properties");
		System.out.println("Start Of Program :: "+System.currentTimeMillis());
		String fileName ="C:/work/Controls.txt";
		MatchersFileReader fileReader = new MatchersFileReader(fileName);
		MatcheMaster matcherMaster = new MatcheMaster(fileReader.getFileContents(), 3,threadPoolSize);
		System.out.println("Matcing Begins :: "+System.currentTimeMillis());
		matcherMaster.doMatching();
		System.out.println("Matcing End :: "+System.currentTimeMillis());
		matcherMaster.printMatchResult();
		System.out.println("END Of Program :: "+System.currentTimeMillis());
	}
}
