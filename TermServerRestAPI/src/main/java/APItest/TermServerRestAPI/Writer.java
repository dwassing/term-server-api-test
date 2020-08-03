package APItest.TermServerRestAPI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Writer {

	String filename;
	
	public Writer(String file) {
		filename = file;
	}
	
	public void write(String message) {
		try(FileWriter fw = new FileWriter(filename, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
		    out.println(message);
		    out.close();
		} catch (IOException e) {
		    System.out.println("Exception in writing to file.");
		    e.printStackTrace();
		}
	}
}
