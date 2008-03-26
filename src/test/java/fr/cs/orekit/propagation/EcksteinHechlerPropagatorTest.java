package fr.cs.orekit.propagation;

import junit.framework.*;
import fr.cs.orekit.Utils;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.MathUtils;

import fr.cs.orekit.errors.OrekitException;
import fr.cs.orekit.errors.PropagationException;
import fr.cs.orekit.frames.Frame;
import fr.cs.orekit.orbits.EquinoctialParameters;
import fr.cs.orekit.orbits.KeplerianParameters;
import fr.cs.orekit.orbits.Orbit;
import fr.cs.orekit.propagation.EcksteinHechlerPropagator;
import fr.cs.orekit.propagation.KeplerianPropagator;
import fr.cs.orekit.time.AbsoluteDate;
import fr.cs.orekit.utils.PVCoordinates;

public class EcksteinHechlerPropagatorTest extends TestCase {

    public EcksteinHechlerPropagatorTest(String name) {
        super(name);
    }

    public void testSameDateCartesian() throws PropagationException {

        // Definition of initial conditions with position and velocity
        // ------------------------------------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);
        double mu = 3.9860047e14;

        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, 584.);
        Orbit initialOrbit =
            new Orbit(initDate, new EquinoctialParameters(new PVCoordinates(position, velocity),Frame.getJ2000(), mu));

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbit = extrapolator.getSpacecraftState(extrapDate);

        assertEquals(finalOrbit.getDate().minus(extrapDate), 0.0, Utils.epsilonTest);
        assertEquals(finalOrbit.getA(), initialOrbit.getA(), Utils.epsilonTest
                     * initialOrbit.getA());
        assertEquals(finalOrbit.getEx(), initialOrbit.getEx(), Utils.epsilonE
                     * initialOrbit.getE());
        assertEquals(finalOrbit.getEy(), initialOrbit.getEy(), Utils.epsilonE
                     * initialOrbit.getE());
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getHx(), initialOrbit.getHx()),
                     initialOrbit.getHx(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getHy(), initialOrbit.getHy()),
                     initialOrbit.getHy(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getLv(), initialOrbit.getLv()),
                     initialOrbit.getLv(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getLv()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getLM(), initialOrbit.getLM()),
                     initialOrbit.getLM(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getLM()));

    }

    public void testSameDateKeplerian() throws OrekitException {

        // Definition of initial conditions with keplerian parameters
        // -----------------------------------------------------------
        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, 584.);
        Orbit initialOrbit =
            new Orbit(initDate,
                      new KeplerianParameters(7209668.0, 0.5e-4, 1.7, 2.1, 2.9,
                                              6.2, KeplerianParameters.TRUE_ANOMALY, Frame.getJ2000()));
        double mu = 3.9860047e14;

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbit = extrapolator.getSpacecraftState(extrapDate);

        assertEquals(finalOrbit.getDate().minus(extrapDate), 0.0, Utils.epsilonTest);
        assertEquals(finalOrbit.getA(), initialOrbit.getA(), Utils.epsilonTest
                     * initialOrbit.getA());
        assertEquals(finalOrbit.getEx(), initialOrbit.getEx(), Utils.epsilonE
                     * initialOrbit.getE());
        assertEquals(finalOrbit.getEy(), initialOrbit.getEy(), Utils.epsilonE
                     * initialOrbit.getE());
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getHx(), initialOrbit.getHx()),
                     initialOrbit.getHx(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getHy(), initialOrbit.getHy()),
                     initialOrbit.getHy(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getLv(), initialOrbit.getLv()),
                     initialOrbit.getLv(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getLv()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getLE(), initialOrbit.getLE()),
                     initialOrbit.getLE(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getLE()));
        assertEquals(MathUtils.normalizeAngle(finalOrbit.getLM(), initialOrbit.getLM()),
                     initialOrbit.getLM(), Utils.epsilonAngle
                     * Math.abs(initialOrbit.getLM()));

    }

    public void testAlmostSphericalBody() throws PropagationException {

        // Definition of initial conditions
        // ---------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);

        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, 584.);
        Orbit initialOrbit =
            new Orbit(initDate,
                      new EquinoctialParameters(new PVCoordinates(position, velocity),Frame.getJ2000(), mu));

        // Initialisation to simulate a keplerian extrapolation
        // To be noticed: in order to simulate a keplerian extrapolation with the
        // analytical
        // extrapolator, one should put the zonal coefficients to 0. But due to
        // numerical pbs
        // one must put a non 0 value.
        double zc20 = 0.1e-10;
        double zc30 = 0.1e-13;
        double zc40 = 0.1e-13;
        double zc50 = 0.1e-14;
        double zc60 = 0.1e-14;

        // Extrapolators definitions
        // -------------------------
        EcksteinHechlerPropagator extrapolatorAna =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, zc20, zc30, zc40, zc50, zc60);
        KeplerianPropagator extrapolatorKep = new KeplerianPropagator(new SpacecraftState(initialOrbit), mu);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbitAna = extrapolatorAna.getSpacecraftState(extrapDate);
        SpacecraftState finalOrbitKep = extrapolatorKep.getSpacecraftState(extrapDate);

        assertEquals(finalOrbitAna.getDate().minus(extrapDate), 0.0,
                     Utils.epsilonTest);
        // comparison of each orbital parameters
        assertEquals(finalOrbitAna.getA(), finalOrbitKep.getA(), 10
                     * Utils.epsilonTest * finalOrbitKep.getA());
        assertEquals(finalOrbitAna.getEx(), finalOrbitKep.getEx(), Utils.epsilonE
                     * finalOrbitKep.getE());
        assertEquals(finalOrbitAna.getEy(), finalOrbitKep.getEy(), Utils.epsilonE
                     * finalOrbitKep.getE());
        assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getHx(), finalOrbitKep.getHx()),
                     finalOrbitKep.getHx(), Utils.epsilonAngle
                     * Math.abs(finalOrbitKep.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getHy(), finalOrbitKep.getHy()),
                     finalOrbitKep.getHy(), Utils.epsilonAngle
                     * Math.abs(finalOrbitKep.getI()));
        assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLv(), finalOrbitKep.getLv()),
                     finalOrbitKep.getLv(), Utils.epsilonAngle
                     * Math.abs(finalOrbitKep.getLv()));
        assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLE(), finalOrbitKep.getLE()),
                     finalOrbitKep.getLE(), Utils.epsilonAngle
                     * Math.abs(finalOrbitKep.getLE()));
        assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLM(), finalOrbitKep.getLM()),
                     finalOrbitKep.getLM(), Utils.epsilonAngle
                     * Math.abs(finalOrbitKep.getLM()));

    }

    public void testPropagatedCartesian() throws PropagationException {
        // Definition of initial conditions with position and velocity
        // ------------------------------------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);

        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, 584.);
        Orbit initialOrbit =
            new Orbit(initDate,
                      new EquinoctialParameters(new PVCoordinates(position, velocity),Frame.getJ2000(), mu));

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100000.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbit = extrapolator.getSpacecraftState(extrapDate);

        assertEquals(finalOrbit.getDate().minus(extrapDate), 0.0, Utils.epsilonTest);

        // computation of M final orbit
        double LM = finalOrbit.getLE() - finalOrbit.getEx()
        * Math.sin(finalOrbit.getLE()) + finalOrbit.getEy()
        * Math.cos(finalOrbit.getLE());

        assertEquals(LM, finalOrbit.getLM(), Utils.epsilonAngle
                     * Math.abs(finalOrbit.getLM()));

        // test of tan ((LE - Lv)/2) :
        assertEquals(Math.tan((finalOrbit.getLE() - finalOrbit.getLv()) / 2.),
                     tangLEmLv(finalOrbit.getLv(), finalOrbit.getEx(), finalOrbit
                               .getEy()), Utils.epsilonAngle);

        // test of evolution of M vs E: LM = LE - ex*sin(LE) + ey*cos(LE)
        double deltaM = finalOrbit.getLM() - initialOrbit.getLM();
        double deltaE = finalOrbit.getLE() - initialOrbit.getLE();
        double delta = finalOrbit.getEx() * Math.sin(finalOrbit.getLE())
        - initialOrbit.getEx() * Math.sin(initialOrbit.getLE())
        - finalOrbit.getEy() * Math.cos(finalOrbit.getLE())
        + initialOrbit.getEy() * Math.cos(initialOrbit.getLE());

        assertEquals(deltaM, deltaE - delta, Utils.epsilonAngle
                     * Math.abs(deltaE - delta));

        // for final orbit
        double ex = finalOrbit.getEx();
        double ey = finalOrbit.getEy();
        double hx = finalOrbit.getHx();
        double hy = finalOrbit.getHy();
        double LE = finalOrbit.getLE();

        double ex2 = ex * ex;
        double ey2 = ey * ey;
        double hx2 = hx * hx;
        double hy2 = hy * hy;
        double h2p1 = 1. + hx2 + hy2;
        double beta = 1. / (1. + Math.sqrt(1. - ex2 - ey2));

        double x3 = -ex + (1. - beta * ey2) * Math.cos(LE) + beta * ex * ey
        * Math.sin(LE);
        double y3 = -ey + (1. - beta * ex2) * Math.sin(LE) + beta * ex * ey
        * Math.cos(LE);

        Vector3D U = new Vector3D((1. + hx2 - hy2) / h2p1, (2. * hx * hy) / h2p1,
                                  (-2. * hy) / h2p1);

        Vector3D V = new Vector3D((2. * hx * hy) / h2p1, (1. - hx2 + hy2) / h2p1,
                                  (2. * hx) / h2p1);

        Vector3D r = new Vector3D(finalOrbit.getA(), (new Vector3D(x3, U, y3, V)));

        assertEquals(finalOrbit.getPVCoordinates(mu).getPosition().getNorm(), r.getNorm(),
                     Utils.epsilonTest * r.getNorm());

    }

    public void testPropagatedKeplerian() throws PropagationException {
        // Definition of initial conditions with keplerian parameters
        // -----------------------------------------------------------
        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, 584.);
        Orbit initialOrbit =
            new Orbit(initDate,
                      new KeplerianParameters(7209668.0, 0.5e-4, 1.7, 2.1, 2.9,
                                              6.2, KeplerianParameters.TRUE_ANOMALY, Frame.getJ2000()));

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100000.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbit = extrapolator.getSpacecraftState(extrapDate);

        assertEquals(finalOrbit.getDate().minus(extrapDate), 0.0, Utils.epsilonTest);

        // computation of M final orbit
        double LM = finalOrbit.getLE() - finalOrbit.getEx()
        * Math.sin(finalOrbit.getLE()) + finalOrbit.getEy()
        * Math.cos(finalOrbit.getLE());

        assertEquals(LM, finalOrbit.getLM(), Utils.epsilonAngle);

        // test of tan((LE - Lv)/2) :
        assertEquals(Math.tan((finalOrbit.getLE() - finalOrbit.getLv()) / 2.),
                     tangLEmLv(finalOrbit.getLv(), finalOrbit.getEx(), finalOrbit
                               .getEy()), Utils.epsilonAngle);

        // test of evolution of M vs E: LM = LE - ex*sin(LE) + ey*cos(LE)
        // with ex and ey the same for initial and final orbit
        double deltaM = finalOrbit.getLM() - initialOrbit.getLM();
        double deltaE = finalOrbit.getLE() - initialOrbit.getLE();
        double delta = finalOrbit.getEx() * Math.sin(finalOrbit.getLE())
        - initialOrbit.getEx() * Math.sin(initialOrbit.getLE())
        - finalOrbit.getEy() * Math.cos(finalOrbit.getLE())
        + initialOrbit.getEy() * Math.cos(initialOrbit.getLE());

        assertEquals(deltaM, deltaE - delta, Utils.epsilonAngle
                     * Math.abs(deltaE - delta));

        // for final orbit
        double ex = finalOrbit.getEx();
        double ey = finalOrbit.getEy();
        double hx = finalOrbit.getHx();
        double hy = finalOrbit.getHy();
        double LE = finalOrbit.getLE();

        double ex2 = ex * ex;
        double ey2 = ey * ey;
        double hx2 = hx * hx;
        double hy2 = hy * hy;
        double h2p1 = 1. + hx2 + hy2;
        double beta = 1. / (1. + Math.sqrt(1. - ex2 - ey2));

        double x3 = -ex + (1. - beta * ey2) * Math.cos(LE) + beta * ex * ey
        * Math.sin(LE);
        double y3 = -ey + (1. - beta * ex2) * Math.sin(LE) + beta * ex * ey
        * Math.cos(LE);

        Vector3D U = new Vector3D((1. + hx2 - hy2) / h2p1, (2. * hx * hy) / h2p1,
                                  (-2. * hy) / h2p1);

        Vector3D V = new Vector3D((2. * hx * hy) / h2p1, (1. - hx2 + hy2) / h2p1,
                                  (2. * hx) / h2p1);

        Vector3D r = new Vector3D(finalOrbit.getA(), (new Vector3D(x3, U, y3, V)));

        assertEquals(finalOrbit.getPVCoordinates(mu).getPosition().getNorm(), r.getNorm(),
                     Utils.epsilonTest * r.getNorm());

    }

    public void testPropagatedEquinoctial() throws PropagationException {

        // Comparison with a given extrapolated orbit
        // -----------------------------------------
        AbsoluteDate initDate = new AbsoluteDate(AbsoluteDate.CNES1950Epoch, 12584. * 86400.);

        double a = 7200000.;
        double exp = .9848e-4; // e * cos(pom)
        double eyp = .17367e-4; // e * sin(pom)
        double i = 1.710423;
        double gom = 1.919862;
        double pso_M = 0.5236193; // M + pom

        double e = Math.sqrt(exp * exp + eyp * eyp);
        double pom = Math.atan2(eyp, exp);
        double ex = e * Math.cos(pom + gom);
        double ey = e * Math.sin(pom + gom);
        Orbit initialOrbit =
            new Orbit(initDate,
                      new EquinoctialParameters(a, ex, ey,
                                                Math.tan(i / 2) * Math.cos(gom),
                                                Math.tan(i / 2) * Math.sin(gom),
                                                pso_M + gom,
                                                EquinoctialParameters.MEAN_LATITUDE_ARGUMENT, Frame.getJ2000()));
        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = (12587. - 12584.) * 86400.; // extrapolation duration in
        // seconds
        AbsoluteDate extrapDate = new AbsoluteDate(initDate, delta_t);

        SpacecraftState finalOrbit = extrapolator.getSpacecraftState(extrapDate);
        // the final orbit

        a = 7187990.1979844316;
        exp = 7.766165990293499E-4; // e * cos(pom)
        eyp = 1.054283074113609E-3; // e * sin(pom)
        i = 1.7105407051081795;
        gom = 1.9674147913622104;
        pso_M = 4.42298640282359; // M + pom

        e = Math.sqrt(exp * exp + eyp * eyp);
        pom = Math.atan2(eyp, exp);
        ex = e * Math.cos(pom + gom);
        ey = e * Math.sin(pom + gom);
        assertEquals(finalOrbit.getDate().minus(extrapDate), 0.0, Utils.epsilonTest);
        assertEquals(finalOrbit.getA(), a, 10. * Utils.epsilonTest * finalOrbit.getA());
        assertEquals(finalOrbit.getEx(), ex, Utils.epsilonE * finalOrbit.getE());
        assertEquals(finalOrbit.getEy(), ey, Utils.epsilonE * finalOrbit.getE());
        assertEquals(finalOrbit.getHx(), Math.tan(i / 2.) * Math.cos(gom),
                     Utils.epsilonAngle * Math.abs(finalOrbit.getHx()));
        assertEquals(finalOrbit.getHy(), Math.tan(i / 2.) * Math.sin(gom),
                     Utils.epsilonAngle * Math.abs(finalOrbit.getHy()));
        assertEquals(finalOrbit.getLM(), pso_M + gom, Utils.epsilonAngle
                     * Math.abs(finalOrbit.getLM()));

    }

    public void testUndergroundOrbit() {

        try {
            // for a semi major axis < equatorial radius
            Vector3D position = new Vector3D(7.0e6, 1.0e6, 4.0e6);
            Vector3D velocity = new Vector3D(-500.0, 800.0, 100.0);

            Orbit initialOrbit =
                new Orbit(new AbsoluteDate(AbsoluteDate.J2000Epoch, 0.0),
                          new EquinoctialParameters(new PVCoordinates(position, velocity),Frame.getJ2000(), mu));

            // Extrapolator definition
            // -----------------------
            EcksteinHechlerPropagator extrapolator =
                new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

            // Extrapolation at the initial date
            // ---------------------------------
            double delta_t = 0.0;
            AbsoluteDate extrapDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, delta_t);
            extrapolator.getSpacecraftState(extrapDate);
        } catch (PropagationException oe) {
            // expected behaviour
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    public void testTooEllipticalOrbit() {
        try {
            // for an eccentricity too big for the model
            Vector3D position = new Vector3D(7.0e6, 1.0e6, 4.0e6);
            Vector3D velocity = new Vector3D(-500.0, 8000.0, 1000.0);

            Orbit initialOrbit =
                new Orbit(new AbsoluteDate(AbsoluteDate.J2000Epoch, 0.0),
                          new EquinoctialParameters(new PVCoordinates(position, velocity),Frame.getJ2000(), mu));

            // Extrapolator definition
            // -----------------------
            EcksteinHechlerPropagator extrapolator =
                new EcksteinHechlerPropagator(new SpacecraftState(initialOrbit), ae, mu, c20, c30, c40, c50, c60);

            // Extrapolation at the initial date
            // ---------------------------------
            double delta_t = 0.0;
            AbsoluteDate extrapDate = new AbsoluteDate(AbsoluteDate.J2000Epoch, delta_t);
            extrapolator.getSpacecraftState(extrapDate);
        } catch (PropagationException oe) {
            // expected behaviour
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    public static Test suite() {
        return new TestSuite(EcksteinHechlerPropagatorTest.class);
    }

    private static double tangLEmLv(double Lv, double ex, double ey) {
        // tan ((LE - Lv) /2)) =
        return (ey * Math.cos(Lv) - ex * Math.sin(Lv))
        / (1 + ex * Math.cos(Lv) + ey * Math.sin(Lv) + Math.sqrt(1 - ex * ex
                                                                 - ey * ey));
    }

    public void setUp() {
        mu  = 3.9860047e14;
        ae  = 6.378137e6;
        c20 = -1.08263e-3;
        c30 = 2.54e-6;
        c40 = 1.62e-6;
        c50 = 2.3e-7;
        c60 = -5.5e-7;
    }

    public void tearDown() {
        mu  = Double.NaN;
        ae  = Double.NaN;
        c20 = Double.NaN;
        c30 = Double.NaN;
        c40 = Double.NaN;
        c50 = Double.NaN;
        c60 = Double.NaN;
    }

    private double mu;
    private double ae;
    private double c20;
    private double c30;
    private double c40;
    private double c50;
    private double c60;

}