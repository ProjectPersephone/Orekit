package fr.cs.aerospace.orekit;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
  public static Test suite() { 

    TestSuite suite = new TestSuite("fr.cs.aerospace.orekit"); 

    /** suite.addTest(RDateTest.suite());
    suite.addTest(CartesianParametersTest.suite());
    suite.addTest(KeplerianParametersTest.suite());
    suite.addTest(KeplerianDerivativesAdderTest.suite());
    suite.addTest(EquinoctialParametersTest.suite());
    suite.addTest(EquinoctialDerivativesAdderTest.suite());
    */
    suite.addTest(fr.cs.aerospace.orekit.extrapolation.AllTests.suite());
    //suite.addTest(fr.cs.aerospace.orekit.perturbations.AllTests.suite());
    return suite; 

  }
}
