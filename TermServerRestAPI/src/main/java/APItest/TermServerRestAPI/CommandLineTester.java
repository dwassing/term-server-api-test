package APItest.TermServerRestAPI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CommandLineTester 
{
	private static final String[] SUPPORTED_QUERY_TYPES = {"concept-info", "concept-finder", "concept-top", "concept-active", 
			"concept-lookup", "concept-subsumption", "concept-validation", "concept-translation", "query-random"};
	
	private static final String[] ALLOWED_ARGUMENTS = {"-server", "-query", "-requests", "-host", "-id", "-search"};
	private static Map<String, String> argumentsMap = new HashMap<String, String>();
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
		if ((Arrays.stream(args).anyMatch("-help"::equals)) || args.length == 0) {
			System.out.print("The server API tester may be invoked using the following arguments with -arg=value. \n");
			System.out.print("Required: -server -query -requests. Optional: -host -conceptid, -search \n");
			System.out.print("Supported servers: snowstorm, snowowl. \n");
			System.out.print("Suppored queries: concept-info, concept-finder, concept-top, concept-active, ");
			System.out.print("concept-lookup, concept-subsumption, concept-validation, concept-translation, query-random \n");
			System.out.print("-requests refers to the amount of threads (clients) that will send a request. \n");
			System.out.print("Addendum 1: if no concept-id is provided, one will be picked at random. \n");
			System.out.print("Addendum 2: if using search, concatenate several words with the '+' sign. \n");
			System.out.print("Addendum 3: query-random will pick any of the other query types. \n");
		} else{
			
			//initialize the map
			for (int j = 0; j < ALLOWED_ARGUMENTS.length; j++) {
				argumentsMap.put(ALLOWED_ARGUMENTS[j], null);
			}
			
			//make supplied values not null
			for(int i = 0; i < args.length; i++) {
				try{
					String[] tempVal = args[i].split("=");
					if ((Arrays.stream(ALLOWED_ARGUMENTS).anyMatch(tempVal[0]::equals))) {
						//System.out.println("Putting:" + tempVal[0] + " and " + tempVal[1]);
						argumentsMap.put(tempVal[0], tempVal[1]);
					}
				} catch (Exception e){
					System.out.println("Invalid argument, invoke the tester with -help for a list of valid arguments.");
				}
			}
			
			//check for any erroneous supplied queries
			if (!(Arrays.stream(SUPPORTED_QUERY_TYPES).anyMatch(argumentsMap.get("-query")::equals))) {
				System.out.println("Wrong query type submitted. See -help.");
			}
			
			//construct the test
			if (argumentsMap.get("-query").equals("query-random")) {
	    		for (int i = 0; i < Integer.parseInt(argumentsMap.get("-requests")); i++) {
		    		RestAPITest R1 = new RestAPITest(argumentsMap.get("-server"), argumentsMap.get("-query"), Integer.toString(i),
		    				argumentsMap.get("-host"), argumentsMap.get("-id"), argumentsMap.get("-search").replace('+', ' '));
		    		int rnd = new Random().nextInt(SUPPORTED_QUERY_TYPES.length-1); //-1 to not allow a re-pick of query-random
		    		R1.setQueryType(SUPPORTED_QUERY_TYPES[rnd]);
		    		R1.start();
	    		}
	    	} else {
	    		for (int i = 0; i < Integer.parseInt(argumentsMap.get("-requests")); i++) {
	    			RestAPITest R1 = new RestAPITest(argumentsMap.get("-server"), argumentsMap.get("-query"), Integer.toString(i),
		    				argumentsMap.get("-host"), argumentsMap.get("-id"), argumentsMap.get("-search").replace('+', ' '));
		    		R1.start();
		    	}
	    	}
		}
	}
}
