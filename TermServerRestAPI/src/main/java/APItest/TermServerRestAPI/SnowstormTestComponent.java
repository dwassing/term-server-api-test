package APItest.TermServerRestAPI;

//this class will be rewritten using maps, after superclass is converted to an interface

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SnowstormTestComponent extends TestComponent {
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
	public String getEndpointPath(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "MAIN/concepts/";
		case "concept-finder":
			return "MAIN/concepts/";
		case "concept-top":
			return "browser/MAIN/concepts/";
		case "concept-active":
			return "MAIN/concepts/";
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
	
	/**
	 * {@inheritDoc}
	 */
	public String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) throws UnsupportedEncodingException {
		switch(queryType) {
		case "concept-query":
			return Integer.toString(codeA);
		case "concept-finder":
			return "?term=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.toString()) + "&offset=0&limit=500";
		case "concept-top": 
			return Integer.toString(codeA) + "/parents?form=inferred&includeDescendantCount=false";
		case "concept-active": //essentially the same endpoint as concept-query
			return Integer.toString(codeA);
		case "concept-lookup":
			return "$lookup?system=http://snomed.info/sct&code=" + Integer.toString(codeA);
		case "concept-subsumption":
			return "$subsumes?system=http://snomed.info/sct&codeA=" + Integer.toString(codeA) + "&codeB=" + "404684003"; //hardcoded to clinical finding //Integer.toString(codeB);
		case "concept-translation": //hardcoded. The HAPI FHIR mapper currently returns only if a translation to ICD10 exists or not (true or false).
			return "$translate?code=" + Integer.toString(codeA) + 
					"&system=http://snomed.info/sct&source=http://snomed.info/sct?fhir_vs&target=http://hl7.org/fhir/sid/icd-10" + 
					"&url=http://snomed.info/sct/900000000000207008/version/20200309?fhir_cm=447562003";
		case "concept-validation": //hardcoded, like concept-translation above to test against the ICD 10 complex map reference set
			return "$validate-code?system=http://snomed.info/sct&code=" + Integer.toString(codeA) + 
					"&url=http://snomed.info/sct/900000000000207008/version/20200309?fhir_vs=refset/723264001"; //ICD-10 complex map reference set 447562003 available as well
		}
		return "";	
	}
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
	public String getInterestingJsonKeyValues(String queryType) {
		switch(queryType) {
		case "concept-query":
			return "term";
		case "concept-finder":
			return "term";
		case "concept-top":
			return "conceptId";
		case "concept-active":
			return "active";
		}
		return "";
	}
}
