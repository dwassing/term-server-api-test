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
		ParametersParameterComponent rr = e.get(1);
		String result = rr.getValue().toString();
		return result;
	}	
}
