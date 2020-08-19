package APItest.TermServerRestAPI;

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Tester 
{
	private static String[] supportedQueryTypes = {"concept-query", "concept-finder", "concept-top", "concept-active", 
			"concept-lookup", "concept-subsumption", "concept-validation"}; //, "concept-translation"
	
	public static void main( String[] args )
	{ 
		//Invoke the tester from control panel with the arguments <server> <query> <# threads>
		
		//If invoked externally, provide exactly 4 arguments: <server> <query> <host> and either <conceptId> or <searchTerm>
		//Additional note about searchTerm: concatenate several words with the '+' sign.
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
	    if((args[0]==null) || !(args[0].equals(RestAPITest.SNOWOWL) || args[0].equals(RestAPITest.SNOWSTORM))) {
    		System.out.println("First argument MUST NOT be null and MUST be a supported terminology.");
	    } else if (!(Arrays.stream(supportedQueryTypes).anyMatch(args[1]::equals))){
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
	    	for (int i = 0; i < Integer.parseInt(args[2]); i++) {
	    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWOWL, args[1], Integer.toString(i));
	    		//int rnd = new Random().nextInt(supportedQueryTypes.length);
	    		//R1.setQueryType(supportedQueryTypes[rnd]);
	    		R1.start();
	    	}
    	} else if (args[0].equals(RestAPITest.SNOWSTORM)) {
    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
	    		RestAPITest R2 = new RestAPITest(RestAPITest.SNOWSTORM, args[1], Integer.toString(i));
	    		//int rnd = new Random().nextInt(supportedQueryTypes.length);
	    		//R2.setQueryType(supportedQueryTypes[rnd]);
	    		R2.start();
	    	}
    	} else {
    		System.out.println("Bad server argument, read the code documentation. Exiting.");
    	}
    }
}
