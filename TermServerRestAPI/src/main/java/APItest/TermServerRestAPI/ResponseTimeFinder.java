package APItest.TermServerRestAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ResponseTimeFinder 
{
	
	public static void main(String [] args)
	{
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
		ArrayList<String> output = new ArrayList<String>();
		int totalTime = 0;
		int totalTimesMeasured = 0;
		
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/wassing/Documents/Git/Exjobb/term-server-api-test/saved-records/" + args[0]))) //change location to where ever needed
        {

            // read line by line
            String line;
            
            while ((line = br.readLine()) != null) {
                output.addAll(Arrays.asList(line.split(" ")));
            }
            
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        
        System.out.println(output);
        System.out.println(output.size());
        
        for (String item : output) {
        	int number = 0;
        	try {
        		number = Integer.parseInt(item);
        		if ((number > 20) && (number < 50000)) { //no response time ever went past 50 seconds, and no concept id is this small. Filter away small numbers from names.
        			totalTime += number;
        			totalTimesMeasured++;
        		}
        	} catch (NumberFormatException e) {
        		//got garbage, do nothing
        	}
        }
        
        System.out.println("Total time was: " + totalTime + "; total times found was: " + totalTimesMeasured + "; and the average was: " + (totalTime/totalTimesMeasured) + " milliseconds.");
	}
}