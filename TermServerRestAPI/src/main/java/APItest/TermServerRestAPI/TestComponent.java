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
public class TestComponent {
	
	/**
	 * Returns either SNOMED CT or FHIR depending on what endpoint we are interested in.
	 * @param queryType
	 * @return
	 */
	public String getEndpointTerminology(String queryType) {
		return "";
	}
	
	/**
	 * Returns the path needed to access the direct endpoint, depending on type of query done.
	 * @param queryType
	 * @return
	 */
	public String getEndpointPath(String queryType) {
		return "";
	}
	
	/**
	 * Returns additional information such as extra parameters, codeB is randomly generated for testing purposes.
	 * @param queryType
	 * @param codeA
	 * @param codeB
	 * @param searchTerm
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getEndpointInfo(String queryType, int codeA, int codeB, String searchTerm) throws UnsupportedEncodingException {
		return "";
	}
	
	/**
	 * In case of FHIR, return index of param we are looking for
	 * @param queryType
	 * @return
	 */
	public int getFhirIndexStorage(String queryType) {
		return -1;
	}
	
	/**
	 * In case of SNOMED CT, returns the values of the JSON objects that we are interested in
	 * @param queryType
	 * @return
	 */
	public String getInterestingJsonKeyValues(String queryType) {
		return "";
	}
}
