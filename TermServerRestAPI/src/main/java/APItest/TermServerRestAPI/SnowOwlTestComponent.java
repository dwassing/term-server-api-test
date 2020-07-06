package APItest.TermServerRestAPI;

//Generates path, information and relevant values to look at for the endpoint used
public class SnowOwlTestComponent {

	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return "snomed-ct/v3/MAIN/international/20200309/concepts/";
			//return "snomed-ct/v3/MAIN%2Finternational%2F20200309/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/";
		}
		return "";
	}
	
	//returns additional information such as extra parameters
	public String getEndpointInfo(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(code);
		case "concept-lookup":
			return "$lookup?code=" + Integer.toString(code) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
		}
		return "";
	}
	
	//returns the values of the JSON objects that we are interested in
	public String getInterestingValue(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return ""; //TODO: ADD KEY VALUE HERE
		case "concept-lookup":
			return ""; //TODO: ADD KEY VALUE HERE
		}
		return "";
	}
}
