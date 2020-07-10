package APItest.TermServerRestAPI;

//Generates path, information and relevant values to look at for the endpoint used
public class SnowstormTestComponent {
	
	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return "ADD-ENDPOINT-PATH-HERE";
		case "concept-lookup":
			return "fhir/CodeSystem/"; //TODO: add path of FHIR lookup
		}
		return "";
	}
	
	public String getEndpointInfo(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return ""; //TODO: maybe return the ID here. Currently done in main class.
		case "concept-lookup":
			return ""; //TODO: add path of FHIR lookup
		}
		return "";
	}
	
	//returns the values of the JSON objects that we are interested in
	public String getInterestingJsonKeyValues(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return "term"; //TODO: ADD KEY VALUE HERE
		case "concept-lookup":
			return "valueString";
		}
		return "";
	}
}
