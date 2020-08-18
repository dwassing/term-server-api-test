package APItest.TermServerRestAPI;

//this class will be rewritten using maps, after superclass is converted to an interface

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SnowOwlTestComponent extends TestComponent {
	
	@Override
	public String getEndpointTerminology(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "SNOMED CT";
		case "concept-finder":
			return "SNOMED CT";
		case "concept-top":
			return "SNOMED CT";
		case "concept-active":
			return "SNOMED CT";
		case "concept-lookup":
			return "FHIR";
		case "concept-subsumption":
			return "FHIR";
		case "concept-translation":
			return "FHIR";
		case "concept-validation":
			return "FHIR";
		}
		return "";
	}
	
	@Override
	public String getEndpointPath(String queryType) {
		switch(queryType) {
		//almost everything relevant is found in the concepts endpoint
		case "concept-query":
			return "snomed-ct/v3/MAIN/concepts/";
		case "concept-finder":
			return "snomed-ct/v3/MAIN/concepts"; //Deliberately not including a slash
		case "concept-top":
			return "snomed-ct/v3/MAIN/concepts/";
		case "concept-active":
			return "snomed-ct/v3/MAIN/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/";
		case "concept-subsumption":
			return "fhir/CodeSystem/";
		case "concept-translation":
			return "fhir/ConceptMap/";
		case "concept-validation":
			return "fhir/ValueSet/";
		}
		return "";
	}

	@Override
	public String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) throws UnsupportedEncodingException {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(codeA) + "?expand=pt()"; //expanding to include the preferred term (not the FSN)
		case "concept-finder":
			return "?expand=" + URLEncoder.encode("pt(), fsn()", StandardCharsets.UTF_8.toString()) + "&limit=50&term=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.toString()); //can use expand=fsn()
		case "concept-top":
			return Integer.toString(codeA);
		case "concept-active":
			return Integer.toString(codeA);
		case "concept-lookup":
			return "$lookup?code=" + Integer.toString(codeA) + "&system=http://snomed.info/sct/900000000000207008/version/20200309";
		case "concept-subsumption":
			return "$subsumes?codeA=" + Integer.toString(codeA) + "&codeB=" + "404684003" + "&system=http://snomed.info/sct/900000000000207008/version/20200309"; //hardcoded to clinical finding
		case "concept-translation":
			return ""; //TODO: add this, if SNOW OWL answers the issue on GitHub with a fix.
		case "concept-validation":
			return "$validate-code?code=" + Integer.toString(codeA) + "&system=http://snomed.info/sct" + 
					"&url=http://snomed.info/sct/900000000000207008/version/20200309?fhir_vs=refset/723264001"; //Lateralizable body structure reference set. ICD-10 not available.
		}
		return "";
	}
	
	
	@Override
	public int getFhirIndexStorage(String queryType) {
		switch(queryType) {
		case "concept-lookup":
			return 1;
		case "concept-subsumption":
			return 0;
		case "concept-translation":
			return 0;
		case "concept-validation":
			return 0;
		}
		return -1;
	}
	
	@Override
	public String getInterestingJsonKeyValues(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "term";
		case "concept-finder":
			return "term";
		case "concept-top":
			return "iconId"; //straight off the bat returns top concept id. Wonder if snowstorm can do the same...
		case "concept-active":
			return "active";
		}
		return "";
	}
}
