package APItest.TermServerRestAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SnowstormTestComponent extends TestComponent {
	
	@Override
	public String getEndpointTerminology(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "SNOMED CT";
		case "concept-finder":
			return "SNOMED CT";
		case "concept-top":
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
	
	@Override
	public String getEndpointPath(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "MAIN/concepts/";
		case "concept-finder":
			return "MAIN/concepts/";
		case "concept-top":
			return "browser/MAIN/concepts/";
		case "concept-lookup":
			return "fhir/CodeSystem/";
		case "concept-subsumption":
			return "fhir/CodeSystem/";
		case "concept-translation":
			return "fhir/ConceptMap/";
		}
		return "";
	}
	
	@Override
	public String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) throws UnsupportedEncodingException {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(codeA);
		case "concept-finder":
			return "?activeFilter=true&term=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.toString()) + "&offset=0&limit=50";
		case "concept-top": 
			return Integer.toString(codeA) + "/parents?form=inferred&includeDescendantCount=false";
		case "concept-lookup":
			return "$lookup?system=http://snomed.info/sct&code=" + Integer.toString(codeA);
		case "concept-subsumption":
			return "$subsumes?system=http://snomed.info/sct&codeA=" + Integer.toString(codeA) + "&codeB=" + Integer.toString(codeB);
		case "concept-translation": //hardcoded. The HAPI FHIR mapper currently returns only if a translation to ICD10 exists or not (true or false).
			return "$translate?code=" + Integer.toString(codeA) + 
					"&system=http://snomed.info/sct&source=http://snomed.info/sct?fhir_vs&target=http://hl7.org/fhir/sid/icd-10" + 
					"&url=http://snomed.info/sct/900000000000207008/version/20200309?fhir_cm=447562003";
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
			return "conceptId";
		}
		return "";
	}
}
