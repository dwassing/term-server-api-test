package APItest.TermServerRestAPI;

//Generates path, information and relevant values to look at for the endpoint used
public class SnowstormTestComponent {
	
	//returns either SNOMED CT or FHIR depending on what endpoint we are interested in.
	public String getEndpointTerminology(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "SNOMED CT";
		case "concept-lookup":
			return "FHIR";
		case "concept-subsumption":
			return "FHIR";
		}
		return "";
	}
	
	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "MAIN/concepts/";
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
			return Integer.toString(codeA);
		case "concept-lookup":
			return "$lookup?system=http://snomed.info/sct&code=" + Integer.toString(codeA);
		case "concept-subsumption":
			return "$subsumes?system=http://snomed.info/sct&codeA=" + Integer.toString(codeA) + "&codeB=" + Integer.toString(codeB);
		}
		return "";
	}
	
	//In case of FHIR, return index of param we are looking for
	public int getFhirIndexStorage(String queryType) {
		switch(queryType) {
		case "concept-lookup":
			return 0;
		case "concept-subsumption":
			return 0;
		}
		return -1;
	}
	
	//In case of SNOMED CT, returns the values of the JSON objects that we are interested in
	public String getInterestingJsonKeyValues(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "term";
		}
		return "";
	}
}
