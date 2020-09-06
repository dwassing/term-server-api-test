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
	
	private Thread thread;
	private String threadName;
	private String queryType;
	private String threadId;
	private String host;
	private long startTestTime;
	private long endTestTime;
	
	/**
	 * NOTE on static modifier:
	 * Static would cause problems with threading IF several servers were tested at once, but we do not consider this a realistic scenario
	 * for the scope of this tool. If such a scenario ever appears, simply remove static and adjust methods as required.
	 */
	private static TestComponent testComponent;
   
    private int conceptId = 404684003; //clinical finding
    private static final int[] conceptIds = {373476007, 404684003, 386689009, 75367002, //midazolam, clinical finding, hypothermia, blood pressure 
    		89644007, 262582004, 387517004, 399060005, //left ear body structure, burn of face, paracetamol, imaging observable
    		840539006, 26929004, 181216001, 85562004}; //covid-19, alzheimer's disease, entire lung, hand structure
    private String searchTerm = "blood pressure"; //search terms: blood pressure, shoulder fracture, covid
    
    /**
     * Default constructor for a thread within the rest API test.
     * @param name The name of the thread.
     * @param type The query type, see help documents.
     * @param id The thread id.
     * @param externalHost Any supplied external host, must be of syntax http://_address_/
     * @param externalConceptId Any specific concept id desired
     * @param externalSearchTerm Any free text search term
     */
    public RestAPITest(String name, String type, String id, String externalHost, String externalConceptId, String externalSearchTerm) {
    	String createmsg = "Creating ";
    	threadName = name;
    	createmsg += threadName;
    	queryType = type;
    	createmsg += queryType;
    	threadId = id;
    	createmsg += threadId;
    	if (externalHost != null) {
    		host = externalHost;
    		createmsg += " host: " + host;
    	}
    	if (externalConceptId != null) {
    		conceptId = Integer.parseInt(externalConceptId);
    		createmsg += " concept: " + conceptId;
    	} else {
    		conceptId = getRandom(conceptIds);
    	}
    	if (externalSearchTerm != null) {
    		searchTerm = externalSearchTerm;
    		createmsg += " search term: " + searchTerm;
    	}
    	//we set host depending on threadName (snowstorm or snowowl) if host is null.
    	System.out.println(createmsg);
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
    
    //The thread starting function implemented from Runnable.
  	public void start() {
  		System.out.println("Starting " + threadName + queryType + threadId);
  		if (thread == null) {
  			thread = new Thread(this, threadName);
  			thread.start ();
  		}
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
			this.startTestTime = System.currentTimeMillis();
			if (this.threadName.equals(SNOWOWL)) {
				testComponent = new SnowOwlTestComponent();
				if (this.host == null) {
					this.host = "http://localhost:8080/snowowl/";
				}
			} else if (this.threadName.equals(SNOWSTORM)) {
				testComponent = new SnowstormTestComponent();
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
			this.endTestTime = System.currentTimeMillis() - this.startTestTime;
			ArrayList<String> values = getTheValues(json, targetValue, targetIndex, terminologyType);
			String output = "Values: " + values + "; time elapsed: " + Long.toString(this.endTestTime) + " millisec."; //
			Writer outputWriter = new Writer("/home/wassing/Documents/Git/Exjobb/term-server-api-test/results.txt");
			outputWriter.write(output);
		} catch (IOException e) {
			System.out.println("Exception in NetclientGet:- " + e);
		}
	}
	
	/**
	 * Function to perform the REST API connection. Creates an URL to an endpoint given host, path and info. Then extracts
	 * json data from that endpoint. If necessary, extra characters are prepended and appended to create a proper JSON notation.
	 * @param host The host, e.g. http://localhost:8080/snowowl/
	 * @param path The path, e.g. fhir/ConceptMap/
	 * @param info The specific information to build the API call such as params.
	 * @return The resulting JSON object from the API call as a string.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private String getRawJsonDataFromHost(String host, String path, String info) throws MalformedURLException, IOException, ProtocolException{
    	
		//TODO: Return json here directly instead, unnecessary to de-serialize into string only to make a new json object from it.
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
		return json;
    }
    
	/**
	 * Given a FHIR JSON object notation, this function returns any key value one may be interested in. Experimental function
	 * that uses the HAPI FHIR IParser, see FHIRMapper class. That class currently only returns one item.
	 * @param jsonString A String representation of the json object.
	 * @param targetValue The key for the json value we are after.
	 * @param targetIndex An internally fetched index for the HAPI FHIR IParser.
	 * @param terminologyType The terminology dictates what method we use to retrieve values.
	 * @return The resulting values from our API call that we care about.
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
	 * @param jsonObj The object.
	 * @param target The target key of the object.
	 * @return The value(s) of the key (target).
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
	        	} else if (keyValue instanceof Boolean) {
	        		targetValues.add(keyValue.toString());
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
	 * @param keyValue A JSON Array containing what we are after.
	 * @return An ArrayList of the values formerly in the JSON Array.
	 */
	private static ArrayList<String> pruneKeyValue(JSONArray keyValue) {
		ArrayList<String> returnValues = new ArrayList<String>();
		for(int i = 0; i < keyValue.length(); i++){
		    returnValues.add((String)keyValue.get(i));
		}
		return returnValues;
	}
}