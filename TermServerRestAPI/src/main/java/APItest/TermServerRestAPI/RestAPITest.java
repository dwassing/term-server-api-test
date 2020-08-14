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
     * Supported query types are: concept-query, concept-finder, concept-lookup, concept-subsumption, concept-translation (snowstorm only)
     * Additional note about concept-finder, do not run more than a single thread of this query, it returns a lot of information.
     */
	
	private Thread thread;
	private String threadName;
	private String queryType;
	private String threadId;
	private String host;
	private long startTestTime;
	private long endTestTime;

    private static final SnowOwlTestComponent snowOwlTestComponent = new SnowOwlTestComponent();
    private static final SnowstormTestComponent snowstormTestComponent = new SnowstormTestComponent();
   
    private int conceptId = 404684003; //clinical finding
    private static final int[] conceptIds = {373476007, 404684003, 386689009}; //midazolam, clinical finding, hypothermia
    private static String searchTerm = "blood pressure"; //placeholder
    
    /**
     * Constructor for external testing using sample concept(s) to a supplied server, see javadoc for supported query types
     * @param name
     * @param type
     * @param exthost
     * @param externalconceptIds
     */
    public RestAPITest(String name, String type, String exthost, int externalConceptId) 
    {
        threadName = name;
        queryType = type;
        threadId = Integer.toString(9999);
        if(externalConceptId != 0)
        {
        	conceptId = externalConceptId;
        }
        if(exthost != null && exthost.length() > 0)
        {
        	host=exthost;
        }
        System.out.println("Creating " + threadName + queryType + " with host: " + host + " and concept id: " + Integer.toString(externalConceptId));
    }
    
    /**
     * Constructor for external testing using a search term to a supplied server, see javadoc for supported query types
     * @param name
     * @param type
     * @param exthost
     * @param externalSearchTerm
     */
    public RestAPITest(String name, String type, String exthost, String externalSearchTerm)
    {
    	threadName = name;
    	queryType = type;
    	if(externalSearchTerm != null && externalSearchTerm.length() > 0)
        {
        	searchTerm=externalSearchTerm;
        }
        if(exthost!=null && exthost.length() > 0)
        {
        	host=exthost;
        }
        System.out.println("Creating " + threadName + queryType + " with host: " + host +  " and search term: " + externalSearchTerm);
    }
    
    //Default constructor for a thread within the rest API test
    public RestAPITest(String name, String type, String id) 
    {
        threadName = name;
        queryType = type;
        threadId = id;
        System.out.println("Creating " + threadName + queryType + threadId);
    }
    
    public void setQueryType(String type) {
    	this.queryType = type;
    }
    
    public String getQueryType() {
    	return this.queryType;
    }
    
    public void setConceptId(int id) {
    	this.conceptId = id;
    }
    
    public int getConceptId() {
    	return this.conceptId;
    }

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
		System.out.println("Thread " +  threadName + queryType + threadId + " exiting.");
	}
	
	private void runSnowstorm() {
		try 
		{
			this.startTestTime = System.currentTimeMillis();
			int selectedId = getRandom(conceptIds);
			if (this.host == null) {
				this.host = "http://localhost:8080/";
			}
			String path = snowstormTestComponent.getEndpointPath(queryType);
			String info = snowstormTestComponent.getEndpointInfo(queryType, selectedId, getRandom(conceptIds), searchTerm);
			String targetValue = snowstormTestComponent.getInterestingJsonKeyValues(queryType);
			int targetIndex = snowstormTestComponent.getFhirIndexStorage(queryType);
			String terminologyType = snowstormTestComponent.getEndpointTerminology(queryType);
			//URL is made up of: host and port, server-name, path-to-endpoint, endpoint-specific-info
			//System.out.println(host + path + info); //debug
			String json = getRawJsonDataFromHost(this.host, path, info);
			ArrayList<String> values = getTheValues(json, targetValue, targetIndex, terminologyType);
			//long endTime = System.currentTimeMillis() - startTime;
			//System.out.println("Values: " + values + "; time elapsed: " + endTime + " millisec."); //debug
			String output = "Values: " + values + "; time elapsed: " + Long.toString(this.endTestTime) + " millisec.";
			Writer outputWriter = new Writer("/home/wassing/Documents/Git/Exjobb/term-server-api-test/results.txt");
			outputWriter.write(output);
		} 
		catch (IOException e)
		{
			System.out.println("Exception in NetClientGet:- " + e);
		}
	}
	
	private void runSnowOwl() {
		try 
		{
			this.startTestTime = System.currentTimeMillis();
			int selectedId = getRandom(conceptIds);
			if (this.host == null) {
				this.host = "http://localhost:8080/snowowl/";
			}
			String path = snowOwlTestComponent.getEndpointPath(queryType);
			String info = snowOwlTestComponent.getEndpointInfo(queryType, selectedId, getRandom(conceptIds), searchTerm);
			String targetValue = snowOwlTestComponent.getInterestingJsonKeyValues(queryType);
			int targetIndex = snowOwlTestComponent.getFhirIndexStorage(queryType);
			String terminologyType = snowOwlTestComponent.getEndpointTerminology(queryType);
			//URL is made up of: host and port, server-name, path-to-endpoint, endpoint-specific-info
			//System.out.println(host + path + info); //debug
			String json = getRawJsonDataFromHost(this.host, path, info);
			ArrayList<String> values = getTheValues(json, targetValue, targetIndex, terminologyType);
			//long endTime = System.currentTimeMillis() - startTime;
			String output = "Values: " + values + "; time elapsed: " + Long.toString(this.endTestTime) + " millisec.";
			Writer outputWriter = new Writer("/home/wassing/Documents/Git/Exjobb/term-server-api-test/results.txt");
			outputWriter.write(output);
		} 
		catch (IOException e)
		{
			System.out.println("Exception in NetClientGet:- " + e);
		}
	}
	
	private String getRawJsonDataFromHost(String host, String path, String info) throws MalformedURLException, IOException, ProtocolException{
    	URL url = new URL(host + path + info);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (this.threadName.equals(SNOWOWL)) {
			conn.setRequestMethod("GET");
			conn.setRequestProperty("content-type", "application/json;charset=utf-8");
		} else if (this.threadName.equals(SNOWSTORM)) {
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Language", "en-X-900000000000509007,en-X-900000000000508004,en");
		} else {
			throw new RuntimeException("Bad server argument in connection establishment.");
		}
		if (conn.getResponseCode() != 200)
		{
			System.out.println(conn.getResponseCode() + conn.getResponseMessage());
			throw new RuntimeException("Failed : HTTP Error code : "
					+ conn.getResponseCode() + conn.getResponseMessage());
		}
		
		String json=new Reader(conn).read();
		conn.disconnect();
		
		if (json.charAt(0) != '{') { //some endpoints return arrays instead of json objects (with arrays inside of them)
			json = "{\"extra\":" + json + '}';
		}
		this.endTestTime = System.currentTimeMillis() - this.startTestTime;
		return json;
    }
    
    private ArrayList<String> getTheValues(String jsonString, String targetValue, int targetIndex, String terminologyType){
    	ArrayList<String> returnValues = new ArrayList<String>();
    	JSONObject jsonObject = new JSONObject(jsonString);
		
		if (terminologyType.equals("FHIR")) {
			String hapiresult = new FHIRMapper(jsonString, targetIndex).getValueParameters();
			returnValues.add(hapiresult);
		} else if (terminologyType.equals("SNOMED CT")) {
			returnValues = getJsonKeyValue(jsonObject, targetValue);
		} else {
			returnValues.add("Wrong terminology specified.");
		}
		return returnValues;
    }
	
	public void start() {
		System.out.println("Starting " + threadName + queryType + threadId);
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