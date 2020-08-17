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
    private static final int[] conceptIds = {373476007, 404684003, 386689009, 75367002, //midazolam, clinical finding, hypothermia, blood pressure 
    		89644007, 262582004, 387517004, 399060005, //left ear body structure, burn of face, paracetamol, imaging observable
    		840539006, 26929004}; //covid-19, alzheimer's disease
    private String searchTerm = "blood pressure"; //placeholder
    
    /**
     * Constructor for external testing using sample concept(s) to a supplied server, see javadoc for supported query types.
     * @param name
     * @param type
     * @param exthost
     * @param externalconceptIds
     */
    public RestAPITest(String name, String type, String id, int externalConceptId) 
    {
        threadName = name;
        queryType = type;
        threadId = id;
        if(externalConceptId != 0)
        {
        	conceptId = externalConceptId;
        }
        System.out.println("Creating " + threadName + queryType + threadId + " with concept id: " + Integer.toString(externalConceptId));
    }
    
    /**
     * Constructor for testing using an external search term, see javadoc for supported query types.
     * @param name
     * @param type
     * @param exthost
     * @param externalSearchTerm
     */
    public RestAPITest(String name, String type, String id, String externalSearchTerm)
    {
    	threadName = name;
    	queryType = type;
    	threadId = id;
    	if(externalSearchTerm != null && externalSearchTerm.length() > 0)
        {
        	searchTerm=externalSearchTerm;
        }
        
        System.out.println("Creating " + threadName + queryType + threadId + " with search term: " + externalSearchTerm);
    }
    
    /**
     * Default constructor for a thread within the rest API test.
     * @param name
     * @param type
     * @param id
     */
    public RestAPITest(String name, String type, String id) 
    {
        threadName = name;
        queryType = type;
        threadId = id;
        conceptId = getRandom(conceptIds);
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

    //Run method overridden from Runnable
	@Override
	public void run() 
	{
		try 
		{
			if (this.threadName.equals(SNOWOWL) || (this.threadName.contentEquals(SNOWSTORM)))
			{
				runServerTest();
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
	
	/**
	 * Main running class of the REST API test. Performs all other calls. Uses threadname to deduce
	 * which test component to invoke, and if no host is given it appends a matching localhost.
	 */
	private void runServerTest() {
		try {
			TestComponent testComponent = new TestComponent();
			this.startTestTime = System.currentTimeMillis();
			if (this.threadName.equals(SNOWOWL)) {
				testComponent = snowOwlTestComponent;
				if (this.host == null) {
					this.host = "http://localhost:8080/snowowl/";
				}
			} else if (this.threadName.equals(SNOWSTORM)) {
				testComponent = snowstormTestComponent;
				if (this.host == null) {
					this.host = "http://localhost:8080/";
				}
			}
			String path = testComponent.getEndpointPath(this.queryType);
			String info = testComponent.getEndpointInfo(this.queryType, this.conceptId, getRandom(conceptIds), this.searchTerm);
			String targetValue = testComponent.getInterestingJsonKeyValues(this.queryType);
			int targetIndex = testComponent.getFhirIndexStorage(this.queryType);
			String terminologyType = testComponent.getEndpointTerminology(this.queryType);
			//URL is made up of: host and port, server-name, path-to-endpoint, endpoint-specific-info
			//System.out.println(host + path + info); //debug
			String json = getRawJsonDataFromHost(this.host, path, info);
			ArrayList<String> values = getTheValues(json, targetValue, targetIndex, terminologyType);
			String output = "Values: " + values + "; time elapsed: " + Long.toString(this.endTestTime) + " millisec.";
			Writer outputWriter = new Writer("/home/wassing/Documents/Git/Exjobb/term-server-api-test/results.txt");
			outputWriter.write(output);
		} catch (IOException e) {
			System.out.println("Exception in NetclientGet:- " + e);
		}
	}
	
	/**
	 * Function to perform the REST API connection. Creates an URL to an endpoint given host, path and info. Then extracts
	 * json data from that endpoint. If necessary, extra characters are prepended and appended to create a proper JSON notation.
	 * @param host
	 * @param path
	 * @param info
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
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
    
	/**
	 * Given a FHIR JSON object notation, this function returns any key value one may be interested in. Experimental function
	 * that uses the HAPI FHIR IParser, see FHIRMapper class. That class currently only returns one item.
	 * @param jsonString
	 * @param targetValue
	 * @param targetIndex
	 * @param terminologyType
	 * @return
	 */
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
	
    //The thread starting function implemented from Runnable.
	public void start() {
		System.out.println("Starting " + threadName + queryType + threadId);
		if (thread == null) {
			thread = new Thread(this, threadName);
			thread.start ();
		}
	}
	
	//Helper to return random concept id for testing purposes.
	private static int getRandom(int[] array) 
	{
    	int rnd = new Random().nextInt(array.length);
    	return array[rnd];
    }
	
	/**
	 * Given a proper JSON object notation (with any type of nested objects and/or arrays), this function
	 * returns any key value associated with target key. If target key appears more than once, all values
	 * are returned. This function is primarily used for SNOMED CT API calls.
	 * @param jsonObj
	 * @param target
	 * @return
	 */
	private static ArrayList<String> getJsonKeyValue(JSONObject jsonObj, String target)
	{
		Iterator<String> keys = jsonObj.keys();
		ArrayList<String> targetValues = new ArrayList<String>();
		while (keys.hasNext()){
			String key = keys.next();
	    	//Need to use Object because JSONObject and String both inherit from Object
	        Object keyValue = jsonObj.get(key);

	        //recursive iteration if objects are nested
	        if (key.equals(target)) {
	        	//System.out.println("Found target: " + target + " with value " + keyValue); //debug
	        	if (keyValue instanceof JSONArray) {
	        		targetValues.addAll(pruneKeyValue((JSONArray)keyValue));
	        	} else {
	        		targetValues.add((String)keyValue);
	        	}
	        } else if (keyValue instanceof JSONObject) {
	            targetValues.addAll(getJsonKeyValue((JSONObject)keyValue, target));
	        } else if (keyValue instanceof JSONArray) {
	        	JSONArray tempArray = jsonObj.getJSONArray(key);
	        	for (int i = 0; i < tempArray.length(); i++) {
	        		Object tempValue = tempArray.get(i);
	        		if (tempValue instanceof JSONObject) {
	        			targetValues.addAll(getJsonKeyValue((JSONObject)tempValue, target));
	        		}	        		
	        	}
	        }
	    }
	    return targetValues;
	}
	
	/**
	 * Helper function to prune the JSONArray that may be the keyValue of the target we seek.
	 * @param keyValue
	 * @return
	 */
	private static ArrayList<String> pruneKeyValue(JSONArray keyValue) {
		ArrayList<String> returnValues = new ArrayList<String>();
		for(int i = 0; i < keyValue.length(); i++){
		    returnValues.add((String)keyValue.get(i));
		}
		return returnValues;
	}
}