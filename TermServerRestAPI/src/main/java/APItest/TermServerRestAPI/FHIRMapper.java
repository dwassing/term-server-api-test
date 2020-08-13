package APItest.TermServerRestAPI;

import ca.uhn.fhir.parser.IParser;

import java.util.List;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;

import ca.uhn.fhir.context.FhirContext;

public class FHIRMapper 
{
	
	private String stringToMap = "";
	private int soughtIndex;
	private ParametersParameterComponent rr;
	
	public FHIRMapper(String json, int targetIndex)
	{
		stringToMap = json;
		soughtIndex = targetIndex;
	}
	
	public String getValueParameters()
	{
		
		// Store the result
		String result = "";
		// Create a FHIR context
		FhirContext ctx = FhirContext.forR4();
				
		// Instantiate a new parser
		IParser parser = ctx.newJsonParser();

		// Parse it
		if (this.soughtIndex == 999) { //if testing
			Parameters parsed = parser.parseResource(Parameters.class, stringToMap);
			List<ParametersParameterComponent> e = parsed.getParameter();
			rr = e.get(0); //e.size() - 1
			System.out.println(rr);
			//About the above line: so far in test cases we returned the LAST relevant element, not sure if this is
			//true for all FHIR endpoints. This needs to be considered, maybe we need to pass location of element in
			//json object if we find outlying examples. Passing locations of elements within json objects is a deeply
			//discouraged practice however and should only be considered as a very last resort.
		} else { //if ran against snowowl or snowstorm.
			System.out.println(stringToMap);
			Parameters parsed = parser.parseResource(Parameters.class, stringToMap);
			List<ParametersParameterComponent> e = parsed.getParameter();
			for (int i = 0; i < e.size(); i++) {
				System.out.println(e.get(i).getValue().toString());
			}
			rr = e.get(this.soughtIndex);
			result = rr.getValue().toString();
		}
		
		return result;
	}	
}
