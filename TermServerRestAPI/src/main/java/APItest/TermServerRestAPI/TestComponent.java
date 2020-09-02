package APItest.TermServerRestAPI;

import java.io.UnsupportedEncodingException;

/**
 * This is a super class that all test components extend from. They implement their own methods. 
 * Generates path, information and relevant values to look at for the endpoint accessed.
 * 
 * This class will be rewritten as an interface in a future update.
 * @author wassing
 *
 */
public abstract class TestComponent {
	
	/**
	 * Returns either SNOMED CT or FHIR depending on what endpoint we are interested in.
	 * @param queryType The type of query as specified by arguments
	 * @return The terminology associated with the query
	 */
	protected abstract String getEndpointTerminology(String queryType);
	
	/**
	 * Returns the path needed to access the direct endpoint, depending on type of query done.
	 * @param queryType The type of query as specified by arguments
	 * @return The path, e.g. fhir/ConceptMap/ or similar.
	 */
	protected abstract String getEndpointPath(String queryType);
	
	/**
	 * Returns additional information such as extra parameters, codeA and codeB are typically SNOMED CT concept ids.
	 * @param queryType The type of query as specified by arguments
	 * @param codeA The concept id to be tested
	 * @param codeB Extra for subsumption testing
	 * @param searchTerm The free search text
	 * @return The associated information needed for the API endpoint.
	 * @throws UnsupportedEncodingException
	 */
	protected abstract String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) throws UnsupportedEncodingException;
	
	/**
	 * In case of FHIR, return index of param we are looking for. TODO: Rewrite this later, as we only return one item currently.
	 * @param queryType The type of query as specified by arguments
	 * @return The index for the FHIR Mapper to check.
	 */
	protected abstract int getFhirIndexStorage(String queryType);
	
	/**
	 * Retrieves information from JSON objects from the SNOMED CT API, gathers all values associated with <key>.
	 * @param queryType The type of query as specified by arguments
	 * @return Every value associated with target (the key).
	 */
	protected abstract String getInterestingJsonKeyValues(String queryType);
}
