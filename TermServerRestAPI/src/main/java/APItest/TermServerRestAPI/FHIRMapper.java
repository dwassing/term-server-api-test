package APItest.TermServerRestAPI;

import ca.uhn.fhir.parser.IParser;

import java.util.List;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;

import ca.uhn.fhir.context.FhirContext;

public class FHIRMapper 
{
	
	private String stringTomap="";
	
	public FHIRMapper(String json)
	{
		stringTomap=json;
	}
	
	public String getValueParameters()
	{
		
		// Create a FHIR context
		FhirContext ctx = FhirContext.forR4();
				
		// Instantiate a new parser
		IParser parser = ctx.newJsonParser();

		// Parse it
		Parameters parsed = parser.parseResource(Parameters.class, stringTomap);
		List<ParametersParameterComponent> e = parsed.getParameter();
		ParametersParameterComponent rr = e.get(e.size() - 1);
		//About the above line: so far in test cases we returned the LAST relevant element, not sure if this is
		//true for all FHIR endpoints. This needs to be considered, maybe we need to pass location of element in
		//json object if we find outlying examples. Passing locations of elements within json objects is a deeply
		//discouraged practice however and should only be considered as a very last resort.
		String result = rr.getValue().toString();
		
		return result;
	}	
}
