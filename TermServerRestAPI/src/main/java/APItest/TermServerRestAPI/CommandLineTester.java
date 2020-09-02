package APItest.TermServerRestAPI;

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CommandLineTester 
{
	private static final String[] SUPPORTED_QUERY_TYPES = {"concept-query", "concept-finder", "concept-top", "concept-active", 
			"concept-lookup", "concept-subsumption", "concept-validation", "concept-translation", "query-random"};
	
	public static void main( String[] args )
	{ 
		System.out.println("");
		//Invoke the tester from control panel with the arguments <server> <query> <# threads>
		
		//If invoked externally, provide exactly 4 arguments: <server> <query> <# threads> and either <conceptId> or <searchTerm>
		//Additional note about searchTerm: concatenate several words with the '+' sign.
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
		if ((args.length == 0) || (args[0].equals("help"))) {
			System.out.print("The server API tester may be invoked using the following arguments: \n");
			System.out.print("<server> <queryType> <# threads> and optionally either a <conceptid> or <searchTerm> \n");
			System.out.print("Supported servers: snowstorm, snowowl. \n");
			System.out.print("Suppored queryTypes: concept-query, concept-finder, concept-top, concept-active, ");
			System.out.print("concept-lookup, concept-subsumption, concept-validation, concept-translation, query-random \n");
			System.out.print("Addendum 1: if no concept-id is provided, one will be picked at random. \n");
			System.out.print("Addendum 2: if using searchTerm, concatenate several words with the '+' sign. \n");
			System.out.print("Addendum 3: query-random will pick any of the other query types. \n");
		} else if(!(args[0].equals(RestAPITest.SNOWOWL) || args[0].equals(RestAPITest.SNOWSTORM))) {
    		System.out.println("First argument MUST NOT be null and MUST be a supported terminology server, invoke the tester with the help argument for documentation..");
	    } else if (!(Arrays.stream(SUPPORTED_QUERY_TYPES).anyMatch(args[1]::equals))){
	    	System.out.println("Wrong query type specified in second argument. Check supported types.");
	    } else if (args.length > 3){ //If we have more than 3 arguments then it is a fully custom Rest API test. The constructor then deduces what type of test we are doing.
	    	try {
	    		RestAPITest R0 = new RestAPITest(args[0], args[1], args[2], Integer.parseInt(args[3]));
	    		R0.start();
	    	} catch (NumberFormatException e) { //if arg[3] is not an int, then we assume it is a search term.
	        	RestAPITest R0 = new RestAPITest(args[0], args[1], args[2], args[3].replace('+', ' '));
	        	R0.start();
	        }
	    } else if (args[2]==null || Integer.parseInt(args[2]) < 1) {
	    	System.out.println("Third argument has to be a number greater than 0.");
	    } else if (args[0].equals(RestAPITest.SNOWOWL)) { //if we made it here, create i amount of threads for the requested server.
	    	if (args[1].equals("query-random")) {
	    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
		    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWOWL, args[1], Integer.toString(i));
		    		int rnd = new Random().nextInt(SUPPORTED_QUERY_TYPES.length-1); //-1 to not allow a re-pick of query-random
		    		R1.setQueryType(SUPPORTED_QUERY_TYPES[rnd]);
		    		R1.start();
	    		}
	    	} else {
	    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
		    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWOWL, args[1], Integer.toString(i));
		    		R1.start();
		    	}
	    	}
    	} else if (args[0].equals(RestAPITest.SNOWSTORM)) {
    		if (args[1].equals("query-random")) {
	    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
		    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWSTORM, args[1], Integer.toString(i));
		    		int rnd = new Random().nextInt(SUPPORTED_QUERY_TYPES.length-1); //-1 to not allow a re-pick of query-random
		    		R1.setQueryType(SUPPORTED_QUERY_TYPES[rnd]);
		    		R1.start();
	    		}
	    	} else {
	    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
		    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWSTORM, args[1], Integer.toString(i));
		    		R1.start();
		    	}
	    	}
    	} else {
    		System.out.println("Bad server argument, invoke the tester with the help argument for documentation.");
    	}
    }
}
