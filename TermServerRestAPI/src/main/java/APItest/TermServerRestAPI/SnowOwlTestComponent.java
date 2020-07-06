package APItest.TermServerRestAPI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//URL encoder seems to not be needed after all.

//Generates endpoint used, request method, and extra params for the main function
public class SnowOwlTestComponent {

	//returns the path needed to access the direct endpoint, depending on type of query done
	public String getEndpointPath(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return "snomed-ct/v3/MAIN/international/20200309/concepts/";
			//return "snomed-ct/v3/MAIN%2Finternational%2F20200309/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/"; //TODO: add path of FHIR lookup
		}
		return "";
	}
	
	public String getEndpointInfo(String queryType, int code) {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(code); //TODO: maybe return the ID here. Currently done in main class.
		case "concept-lookup":
			return "$lookup?code=" + Integer.toString(code) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
			//return "$lookup?code=" + URLEncoder.encode(Integer.toString(code), StandardCharsets.UTF_8) + "&system=" + URLEncoder.encode("http://snomed.info/sct/900000000000207008/version/20200309", StandardCharsets.UTF_8);
		}
		return "";
	}
}
