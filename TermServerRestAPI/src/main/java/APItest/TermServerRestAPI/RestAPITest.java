package APItest.TermServerRestAPI;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class RestAPITest implements Runnable
{
	public static final String SNOWSTORM = "snowstorm";
   	public static final String SNOWOWL = "snowowl";

	/**
     * Supported query types are: concept-query, concept-lookup
     */
	
	private Thread thread;
	private String threadName;
	private String queryType;
	private String host="http://localhost:8080/snowowl/";

    private static final SnowOwlTestComponent snowOwlTestComponent = new SnowOwlTestComponent();
    private static final SnowstormTestComponent snowstormTestComponent = new SnowstormTestComponent();
   
    public static int[] conceptIds = {373476007, 404684003, 386689009}; //midazolam, clinical finding, hypothermia
    
    //Constructor for launching a test scenario with sample concepts to a certain server
    public RestAPITest(String name, String type, int [] externalconceptIds, String exthost) 
    {
        threadName = name;
        queryType = type;
        if(externalconceptIds!=null && externalconceptIds.length>0)
        {
        	conceptIds=externalconceptIds;
        }
        if(exthost!=null && exthost.length()>0)
        {
        	host=exthost;
        }
        System.out.println("Creating " + threadName + queryType + " benny version");
    }
    
    //Default constructor for a thread within the rest API test
    public RestAPITest(String name, String type) 
    {
        threadName = name;
        queryType = type;
        
        System.out.println("Creating " + threadName + queryType);
    }
        
    //Fixa så man gör flera requests med multitrådning. DONE
    //Fixa så att URL-strängen tar params (så vi kan mata in olika concepts bland annat) DONE
    //Fixa så man kan skapa X mängd trådar där X är argument som ges från runtime. IN PROGRESS
    //Fixa så att vi bara tittar på vissa key-value pair i JSON objektet som returneras. DONE
    //Bygg test-scenarion

	@Override
	public void run() 
	{
		try 
		{
			if (this.threadName.equals(SNOWOWL))
			{
				runSnowOwl();
			} 
			else if (this.threadName.equals(SNOWSTORM)) 
			{
				runSnowstorm();
			}
			else 
			{
				System.out.println("Wrong argument provided.");
				System.out.println(threadName + queryType);
			}
		} 
		catch (Exception e) {
			System.out.println("Thread " + threadName + " interrupted, or something else went wrong.");
		}
		System.out.println("Thread " +  threadName + " exiting.");
	}
	
	private void runSnowstorm() {
		try 
		{
			int selectedId = getRandom(conceptIds);
			String path = snowstormTestComponent.getEndpointPath(queryType, selectedId);
			String info = snowstormTestComponent.getEndpointInfo(queryType, selectedId);
			String targetValue = snowstormTestComponent.getInterestingJsonKeyValues(queryType, selectedId);
			String terminologyType = snowstormTestComponent.getEndpointTerminology(queryType);
			//URL is made up of: host and port, server-name, path-to-endpoint, endpoint-specific-info
			//System.out.println(host + path + info); //debug
			ArrayList<String> values = getTheValues(path, info, targetValue, terminologyType);
			System.out.println("Values " + values);

		} 
		catch (IOException e)
		{
			System.out.println("Exception in NetClientGet:- " + e);
		}
	}
	
	private void runSnowOwl() {
		try 
		{
			int selectedId = getRandom(conceptIds);
			String path = snowOwlTestComponent.getEndpointPath(queryType, selectedId);
			String info = snowOwlTestComponent.getEndpointInfo(queryType, selectedId);
			String targetValue = snowOwlTestComponent.getInterestingJsonKeyValues(queryType, selectedId);
			String terminologyType = snowstormTestComponent.getEndpointTerminology(queryType);
			//URL is made up of: host and port, server-name, path-to-endpoint, endpoint-specific-info
			//System.out.println(host + path + info); //debug
			ArrayList<String> values = getTheValues(path, info, targetValue, terminologyType);
			System.out.println("Values " + values);

		} 
		catch (IOException e)
		{
			System.out.println("Exception in NetClientGet:- " + e);
		}
	}
	
	private ArrayList<String> getTheValues(String path, String info, String targetValue, String terminologyType)
			throws MalformedURLException, IOException, ProtocolException 
	{
		URL url = new URL(host + path + info);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("content-type", "application/json;charset=utf-8");
		if (conn.getResponseCode() != 200)
		{
			throw new RuntimeException("Failed : HTTP Error code : "
					+ conn.getResponseCode() + conn.getResponseMessage());
		}
		
		String json=new Reader(conn).read();
		conn.disconnect();
		//Build the object and print interesting info.
		
		JSONObject jsonObject = new JSONObject(json);
		//System.out.println(jsonObject);
		conn.disconnect();
		if (terminologyType.equals("FHIR")) {
			String hapiresult = new FHIRMapper(json).getValueParameters();
			System.out.println("FHIR found: " + hapiresult + " ");
		}
		return getJsonKeyValue(jsonObject, targetValue);
	}
	
	public void start() {
		System.out.println("Starting " + threadName + queryType);
		if (thread == null) {
			thread = new Thread(this, threadName);
			thread.start ();
		}
	}
	
	private static int getRandom(int[] array) 
	{
    	int rnd = new Random().nextInt(array.length);
    	return array[rnd];
    }
	
	public static ArrayList<String> getJsonKeyValue(JSONObject jsonObj, String target)
	{
		Iterator<String> keys = jsonObj.keys();
		ArrayList<String> targetValues = new ArrayList<String>();
		while (keys.hasNext()){
			String key = keys.next();
	    	//Need to use Object because JSONObject and String both inherit from Object
	        Object keyValue = jsonObj.get(key);

	        //recursive iteration if objects are nested
	        //System.out.println("key: "+ key + " value: " + keyValue + " target: " + target); //debug
	        if (keyValue instanceof JSONObject) {
	        	//System.out.println("Found JSON" + keyValue); //debug
	            targetValues.addAll(getJsonKeyValue((JSONObject)keyValue, target));
	        } else if (keyValue instanceof JSONArray) {
	        	//System.out.println("Found ARRAY " + keyValue); //debug
	        	JSONArray tempArray = jsonObj.getJSONArray(key);
	        	for (int i = 0; i < tempArray.length(); i++) {
	        		Object tempValue = tempArray.get(i);
	        		if (tempValue instanceof JSONObject) {
	        			targetValues.addAll(getJsonKeyValue((JSONObject)tempValue, target));
	        		}	        		
	        	}
	        } else if (key.equals(target)) {
	        	//System.out.println("Found target: " + target + " with value " + keyValue); //debug
	        	targetValues.add((String)keyValue);
	        }
	    }
	    return targetValues;
	}
}