package APItest.TermServerRestAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RecallTester 
{
	
	public static void main(String [] args)
	{
		BasicConfigurator.configure();
		Logger.getLogger("ca.uhn.fhir.util.VersionUtil").setLevel(Level.OFF);
		Logger.getLogger("ca.uhn.fhir.context.ModelScanner").setLevel(Level.OFF);
		Logger.getRootLogger().setLevel(Level.OFF);
		ArrayList<String> missingA = new ArrayList<String>();
		ArrayList<String> missingB = new ArrayList<String>();
		String output = "";
		
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/wassing/Desktop/Snowstorm/" + args[0]))) //change location to where ever needed
        {

            // read line by line
            String line;
            
            while ((line = br.readLine()) != null) {
                output += line;
            }
            
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        Set<String> valuesFileA = new HashSet<String>(Arrays.asList(output.split(", ")));
        output = "";
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/wassing/Desktop/Snowowl/" + args[1]))) //change location to where ever needed
        {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                output += line;
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        Set<String> valuesFileB = new HashSet<String>(Arrays.asList(output.split(", ")));
        System.out.println("Set sizes are: " + valuesFileA.size() + " for A, and " + valuesFileB.size() + " for B.");
        for (String temp : valuesFileA) {
        	if(!(valuesFileB.contains(temp))) {
        		missingB.add(temp);
        	}
        }
        for (String temp : valuesFileB) {
        	if(!(valuesFileA.contains(temp))) {
        		missingA.add(temp);
        	}
        }
        if (missingB.size() > 0) {
        	System.out.println("File 2 is missing: ");
        	for(int i = 0; i < missingB.size(); i++) {
        		System.out.println(missingB.get(i));
        	}
        }
        if (missingA.size() > 0) {
        	System.out.println("File 1 is missing: ");
        	for(int i = 0; i < missingA.size(); i++) {
        		System.out.println(missingA.get(i));
        	}
        }
        if (missingA.size() == 0 && missingB.size() == 0) {
        	System.out.println("The files are identical. 100% recall in both.");
        }
	}
}