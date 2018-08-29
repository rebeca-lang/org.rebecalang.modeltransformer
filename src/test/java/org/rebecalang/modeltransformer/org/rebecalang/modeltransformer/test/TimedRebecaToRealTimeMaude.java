package org.rebecalang.modeltransformer.org.rebecalang.modeltransformer.test;

import org.rebecalang.modeltransformer.Transform;


public class TimedRebecaToRealTimeMaude {
	String base = "src/test/resources/org/rebecalang/modeltransformer/testcase";
	
//	@Test 
	public void testDynamicModel() {
		String[] parameters = new String[] {
//				"--source", base + "/thermostat.rebeca",
//				"--source", base + "/prot-802-11.rebeca",
//				"--source", base + "/sensornetwork2.rebeca",
				"--source", base + "/phils.rebeca",
				"-e", "CoreRebeca",
				"-v", "2.1",
				"-o", "phils",
//				"-o", "thermostat",
//				"-t", "RTMaude",
				"-t", "akka",
		};
		
		try {
//			Transform.main(new String[0]);
			Transform.main(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TimedRebecaToRealTimeMaude testCase = new TimedRebecaToRealTimeMaude();
		testCase.testDynamicModel();
	}
}
