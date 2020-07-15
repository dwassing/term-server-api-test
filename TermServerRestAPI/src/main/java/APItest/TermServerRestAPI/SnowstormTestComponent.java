package APItest.TermServerRestAPI;

//Generates path, information and relevant values to look at for the endpoint used
public class SnowstormTestComponent {
	
	//returns either SNOMED CT or FHIR depending on what endpoint we are interested in.
	public String getEndpointTerminology(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "SNOMED CT"; //TODO: ADD KEY VALUE HERE
		case "concept-lookup":
			return "FHIR";
		case "concept-subsumption":
			return "FHIR";
		}
		return "";
	}
	
	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return "ADD-ENDPOINT-PATH-HERE";
		case "concept-lookup":
			return ""; //TODO: add path of FHIR lookup
		case "concept-subsumption":
			return "";
		}
		return "";
	}
	
	//returns additional information such as extra parameters, codeB is randomly generated currently.
	public String getEndpointInfo(String queryType, int codeA, int codeB) {
		switch(queryType) {
		case "concept-query":
			return ""; //TODO: maybe return the ID here. Currently done in main class.
		case "concept-lookup":
			return ""; //TODO: add path of FHIR lookup
		case "concept-subsumption":
			return "";
		}
		return "";
	}
	
	//returns the values of the JSON objects that we are interested in
	public String getInterestingJsonKeyValues(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return ""; //TODO: ADD KEY VALUE HERE
		case "concept-lookup":
			return "";
		case "concept-subsumption":
			return "";
		}
		return "";
	}
}
