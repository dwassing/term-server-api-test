package APItest.TermServerRestAPI;

//Maybe possible to rewrite this with Maps, but would be harder to implement ambiguity in parameter depending info.

//Generates path, information and relevant values to look at for the endpoint used
public class SnowOwlTestComponent {
	
	public String getEndpointTerminology(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "SNOMED CT";
		case "concept-finder":
			return "SNOMED CT";
		case "concept-lookup":
			return "FHIR";
		case "concept-subsumption":
			return "FHIR";
		case "concept-translation":
			return "FHIR";
		}
		return "";
	}
	
	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "snomed-ct/v3/MAIN/international/20200309/concepts/";
			//return "snomed-ct/v3/MAIN%2Finternational%2F20200309/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/";
		case "concept-subsumption":
			return "fhir/CodeSystem/";
		case "concept-translation":
			return "fhir/ConceptMap"; //TODO: add this
		}
		return "";
	}
	
	//returns additional information such as extra parameters, codeB is randomly generated currently.
	public String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(codeA) + "?expand=pt()"; //expanding to include the preferred term (not the FSN)
		case "concept-finder":
			return "?activeFilter=true&term=" + searchTerm + "&offset=0&limit=50"; //TODO: Currently a placeholder from snowstorm, REPLACE ME
		case "concept-lookup":
			return "$lookup?code=" + Integer.toString(codeA) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
		case "concept-subsumption":
			return "$subsumes?codeA=" + Integer.toString(codeA) + "&codeB=" + Integer.toString(codeB) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
		case "concept-translation":
			return ""; //TODO: add this
		}
		return "";
	}
	
	//In case of FHIR, return index of param we are looking for
	public int getFhirIndexStorage(String queryType) {
		switch(queryType) {
		case "concept-lookup":
			return 1;
		case "concept-subsumption":
			return 0;
		case "concept-translation":
			return 0;
		}
		return -1;
	}
	
	//In case of SNOMED CT, returns the values of the JSON objects that we are interested in
	public String getInterestingJsonKeyValues(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "term";
		case "concept-finder":
			return "term";
		}
		return "";
	}
}
