package APItest.TermServerRestAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

public class RestAPITest implements Runnable
{
	/**
     * Supported query types are: concept-query, concept-lookup
     */
	
	private Thread thread;
	private String threadName;
	private String queryType;
    private static final SnowOwlTestComponent snowOwlTestComponent = new SnowOwlTestComponent();
    private static final SnowstormTestComponent snowstormTestComponent = new SnowstormTestComponent();
   
    public static int[] conceptIds = {373476007, 404684003, 386689009}; //midazolam, clinical finding, hypothermia
    
    //Default constructor for a thread within the rest API test
    RestAPITest(String name, String type) {
        threadName = name;
        queryType = type;
        System.out.println("Creating " + threadName + queryType);
     }
    
    public static void main( String[] args )
    {
    	if (args[0].equals("snowowl")) {
    		RestAPITest R1 = new RestAPITest("snowowl", args[1]);
    		R1.start();
    		RestAPITest R2 = new RestAPITest("snowowl", args[1]);
    		R2.start();
    	} else if (args[0].equals("snowstorm")) {
    		RestAPITest R3 = new RestAPITest("snowstorm", args[1]);
    		R3.start();
    	}
    }
    
    public static int getRandom(int[] array) {
    	int rnd = new Random().nextInt(array.length);
    	return array[rnd];
    }
    //Fixa så man gör flera requests med multitrådning. DONE
    //Fixa så att URL-strängen tar params (så vi kan mata in olika concepts bland annat) DONE
    //Fixa så man kan skapa X mängd trådar där X är argument som ges från runtime.
    //Bygg test-scenarion

	@Override
	public void run() {
		try {
			if (this.threadName.equals("snowowl")){
				try {
		    		int selectedId = getRandom(conceptIds);
		    		//String stringId = Integer.toString(selectedId);
		    		String host = "http://localhost:8080/snowowl/";
		    		String path = snowOwlTestComponent.getEndpointPath(queryType, selectedId);
		    		String info = snowOwlTestComponent.getEndpointInfo(queryType, selectedId);
		    		
		    		//URL is made up of: host path, system-name, path-to-endpoint, endpoint-specific-info
		    		//System.out.println(host + path + info); //debug
		            URL url = new URL(host + path + info);
		            
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setRequestMethod("GET");
		            conn.setRequestProperty("content-type", "application/json;charset=utf-8");
		            if (conn.getResponseCode() != 200) {
		                throw new RuntimeException("Failed : HTTP Error code : "
		                        + conn.getResponseCode() + conn.getResponseMessage());
		            }
		            InputStreamReader in = new InputStreamReader(conn.getInputStream());
		            BufferedReader br = new BufferedReader(in);
		            String output;
		            while ((output = br.readLine()) != null) {
		                System.out.println(output);
		            }
		            conn.disconnect();
		
		        } catch (IOException e) {
		            System.out.println("Exception in NetClientGet:- " + e);
		        }
			} else if (this.threadName.equals("snowstorm")) {
				try {
		    		int selectedId = getRandom(conceptIds);
		    		//String stringId = Integer.toString(selectedId);
		    		String host = "http://localhost:8080/snowowl/"; //CHANGE THIS TO SNOWSTORM URL
		    		String path = snowstormTestComponent.getEndpointPath(queryType, selectedId);
		    		String info = snowstormTestComponent.getEndpointInfo(queryType, selectedId);
		    		
		    		//URL is made up of: host path, system-name, path-to-endpoint, endpoint-specific-info
		    		//System.out.println(host + path + info); //debug
		            URL url = new URL(host + path + info);
		            
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setRequestMethod("GET");
		            conn.setRequestProperty("content-type", "application/json;charset=utf-8");
		            if (conn.getResponseCode() != 200) {
		                throw new RuntimeException("Failed : HTTP Error code : "
		                        + conn.getResponseCode() + conn.getResponseMessage());
		            }
		            InputStreamReader in = new InputStreamReader(conn.getInputStream());
		            BufferedReader br = new BufferedReader(in);
		            String output;
		            while ((output = br.readLine()) != null) {
		                System.out.println(output);
		            }
		            conn.disconnect();
		
		        } catch (IOException e) {
		            System.out.println("Exception in NetClientGet:- " + e);
		        }
			} else {
				System.out.println("Wrong argument provided.");
				System.out.println(threadName + queryType);
			}
		} catch (Exception e) {
			System.out.println("Thread " + threadName + " interrupted, or something else went wrong.");
		}
		System.out.println("Thread " +  threadName + " exiting.");
	}
	
	public void start() {
		System.out.println("Starting " + threadName + queryType);
		if (thread == null) {
			thread = new Thread(this, threadName);
			thread.start ();
		}
	}
}

/**System.out.println("Running " +  threadName );
try {
   for(int i = 4; i > 0; i--) {
      System.out.println("Thread: " + threadName + ", " + i);
      // Let the thread sleep for a while.
      Thread.sleep(50);
   }
} catch (InterruptedException e) {
   System.out.println("Thread " +  threadName + " interrupted.");
}
System.out.println("Thread " +  threadName + " exiting.");
**/