package APItest.TermServerRestAPI;

//Generates path, information and relevant values to look at for the endpoint used
public class SnowOwlTestComponent {

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
			return "snomed-ct/v3/MAIN/international/20200309/concepts/";
			//return "snomed-ct/v3/MAIN%2Finternational%2F20200309/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/";
		case "concept-subsumption":
			return "fhir/CodeSystem/";
		}
		return "";
	}
	
	//returns additional information such as extra parameters, codeB is randomly generated currently.
	public String getEndpointInfo(String queryType, int codeA, int codeB) {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(codeA) + "?expand=pt()"; //expanding to include the preferred term (not the FSN)
		case "concept-lookup":
			return "$lookup?code=" + Integer.toString(codeA) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
		case "concept-subsumption":
			return "$subsumes?codeA=" + Integer.toString(codeA) + "&codeB=" + Integer.toString(codeB) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
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
		case "concept-subsumption":
			return "valueCode";
		}
		return "";
	}
}
