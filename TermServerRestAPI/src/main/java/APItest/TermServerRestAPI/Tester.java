package APItest.TermServerRestAPI;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Tester 
{
	public static void main( String[] args )
	{ 
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
	    if(args[0]==null)
	    {
    		System.out.println("First argument must NOT be null"); //Used to say first argument must be null
	    } else if (args[0].equals(RestAPITest.SNOWOWL)) {
    		RestAPITest R1 = new RestAPITest(RestAPITest.SNOWOWL, args[1]);
    		R1.start();
    	} else if (args[0].equals(RestAPITest.SNOWSTORM)) {
    		RestAPITest R3 = new RestAPITest(RestAPITest.SNOWSTORM, args[1]);
    		R3.start();
    	} else {
    		System.out.println("Bad server argument. Exiting.");
    	}
    }
}
