package APItest.TermServerRestAPI;

import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Tester 
{
	private static String[] supportedQueryTypes = {"concept-query", "concept-lookup"};
	
	public static void main( String[] args )
	{ 
		//Invoke the tester with the arguments <server> <query> <# threads>
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
	    if((args[0]==null) || !(args[0].equals(RestAPITest.SNOWOWL) || args[0].equals(RestAPITest.SNOWSTORM))) {
    		System.out.println("First argument MUST NOT be null and MUST be a supported terminology.");
	    } else if (!(Arrays.stream(supportedQueryTypes).anyMatch(args[1]::equals))){
	    	System.out.println("Wrong query type specified in second argument. Check supported types.");
	    } else if (args[2]==null || Integer.parseInt(args[2]) < 1) {
	    	System.out.println("Third argument has to be a number greater than 0.");
	    } else if (args[0].equals(RestAPITest.SNOWOWL)) {
	    	for (int i = 0; i < Integer.parseInt(args[2]); i++) {
	    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWOWL, args[1], Integer.toString(i));
	    		R1.start();
	    	}
    	} else if (args[0].equals(RestAPITest.SNOWSTORM)) {
    		for (int i = 0; i < Integer.parseInt(args[2]); i++) {
	    		RestAPITest R2 = new RestAPITest(RestAPITest.SNOWOWL, args[1], Integer.toString(i));
	    		R2.start();
	    	}
    	} else {
    		System.out.println("Bad server argument. Exiting.");
    	}
    }
}
