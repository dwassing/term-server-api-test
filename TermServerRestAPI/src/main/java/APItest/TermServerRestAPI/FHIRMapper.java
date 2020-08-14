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
			rr = e.get(0);
			System.out.println(rr);
		} else { //if ran against snowowl or snowstorm.
			Parameters parsed = parser.parseResource(Parameters.class, stringToMap);
			List<ParametersParameterComponent> e = parsed.getParameter();
			rr = e.get(this.soughtIndex);
			result = rr.getValue().toString();
			
			//Below piece of code is a debugger to post all items returned by the IParser.
			/**for (int i = 0; i < e.size(); i++) {
				System.out.println(e.get(i).getValue().toString());
			}**/
		}
		
		return result;
	}	
}
