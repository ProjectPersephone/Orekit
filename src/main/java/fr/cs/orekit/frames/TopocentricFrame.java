package fr.cs.orekit.frames;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import fr.cs.orekit.bodies.BodyShape;
import fr.cs.orekit.bodies.GeodeticPoint;
import fr.cs.orekit.errors.OrekitException;
import fr.cs.orekit.time.AbsoluteDate;
import fr.cs.orekit.utils.PVCoordinates;

/** Topocentric frame.
 * <p> Frame associated to a position at the surface of a body shape.</p>
 * @author Véronique Pommier-Maurussane
 */
public class TopocentricFrame extends Frame {

    /** Serializable UID. */
    private static final long serialVersionUID = 720487682019109221L;

    /** Point where the topocentric frame is defined */
    private final GeodeticPoint point;

    /** Constructor for the singleton.
     * @param parentShape body shape on which the local point is defined
     * @param point local surface point where topocentric frame is defined
     * @param name the string representation
     */
    public TopocentricFrame(final BodyShape parentShape, final GeodeticPoint point,
                            final String name) {
        
        super(parentShape.getBodyFrame(), null, name);
        this.point = point;
        
        // Build transformation from body centered frame to topocentric frame :        
        // 1. Translation from body center to geodetic point
        final Transform translation = new Transform(parentShape.transform(point).negate());
        
        // 2. Rotate axes
        final Vector3D Xtopo = getEast();
        final Vector3D Ztopo = getZenith();
        final Transform rotation = new Transform(new Rotation(Xtopo, Ztopo, Vector3D.plusI, Vector3D.plusK),
                                                 Vector3D.zero);
        
        // Compose both transformations
        setTransform(new Transform(translation, rotation));

    }

    /** Get the zenith direction of topocentric frame, expressed in parent shape frame.
     * <p>The zenith direction is defined as the normal to local horizontal plane.</p>
     */
    public Vector3D getZenith() {
        
        final double xZenith = Math.cos(point.getLongitude()) * Math.cos(point.getLatitude());
        final double yZenith = Math.sin(point.getLongitude()) * Math.cos(point.getLatitude());
        final double zZenith = Math.sin(point.getLatitude());
       
        return  new Vector3D(xZenith, yZenith, zZenith);
    }
    
    /** Get the nadir direction of topocentric frame, expressed in parent shape frame.
     * <p>The nadir direction is the opposite of zenith direction.</p>
     */
    public Vector3D getNadir() {
        return getZenith().negate();      
    }
    
   /** Get the north direction of topocentric frame, expressed in parent shape frame.
     * <p>The north direction is defined in the horizontal plane 
     * (normal to zenith direction) and following the local meridian.</p>
     */
    public Vector3D getNorth() {
        
        final double xNorth = - Math.cos(point.getLongitude()) * Math.sin(point.getLatitude());
        final double yNorth = - Math.sin(point.getLongitude()) * Math.sin(point.getLatitude());
        final double zNorth = Math.cos(point.getLatitude());
       
        return new Vector3D(xNorth, yNorth, zNorth);
    }
    
    /** Get the south direction of topocentric frame, expressed in parent shape frame.
     * <p>The south direction is the opposite of north direction.</p>
     */
    public Vector3D getSouth() {
        return getNorth().negate();      
    }
    
    /** Get the east direction of topocentric frame, expressed in parent shape frame.
     * <p>The east direction is defined in the horizontal plane 
     * in order to complete direct triangle (east, north, zenith).</p>
     */
    public Vector3D getEast() {
        return new Vector3D(-Math.sin(point.getLongitude()), 
                            Math.cos(point.getLongitude()),
                            0.);
    }
    
    /** Get the west direction of topocentric frame, expressed in parent shape frame.
     * <p>The west direction is the opposite of east direction.</p>
     */
    public Vector3D getWest() {
        return getEast().negate();            
    }
    
    
    
    /** Get the elevation of a point with regards to the local point.
     * <p>The elevation is the angle between the local horizontal and 
     * the direction from local point to given point.</p>
     * @param extPoint point for which elevation shall be computed
     * @param frame frame in which the point is defined
     * @param date computation date
     */
    public double getElevation(Vector3D extPoint, Frame frame, AbsoluteDate date) 
        throws OrekitException {
        
        // Transform given point from given frame to topocentric frame
        final Transform t = frame.getTransformTo(this, date);
        final Vector3D extPointTopo = t.transformPosition(extPoint);
        
        // Elevation angle is PI/2 - angle between zenith and given point direction
        return Math.asin(extPointTopo.normalize().getZ());
    }

    /** Get the azimuth of a point with regards to the topocentric frame center point.
     * <p>The azimuth is the angle between the North direction at local point and 
     * the projection in local horizontal plane of the direction from local point 
     * to given point. Azimuth angles are counted clockwise, i.e positive towards the East.</p>
     * @param extPoint point for which elevation shall be computed
     * @param frame frame in which the point is defined
     * @param date computation date
     */
    public double getAzimuth(Vector3D extPoint, Frame frame, AbsoluteDate date)
        throws OrekitException {
        
        // Transform given point from given frame to topocentric frame
        final Transform t = getTransformTo(frame, date).getInverse();
        final Vector3D extPointTopo = t.transformPosition(extPoint);
        
        // Compute azimuth
        double azimuth = Math.atan2(extPointTopo.getX(), extPointTopo.getY());
        if (azimuth < 0.) {
            azimuth += 2. * Math.PI;
        }
        return azimuth;
        
    }
    
    /** Get the range of a point with regards to the topocentric frame center point.
     * @param extPoint point for which range shall be computed
     * @param frame frame in which the point is defined
     * @param date computation date
     */
    public double getRange(Vector3D extPoint, Frame frame, AbsoluteDate date) 
        throws OrekitException {
        
        // Transform given point from given frame to topocentric frame
        final Transform t = getTransformTo(frame, date).getInverse();
        final Vector3D extPointTopo = t.transformPosition(extPoint);
        
        // Compute range
        return extPointTopo.getNorm();
        
    }
    
    /** Get the range rate of a point with regards to the topocentric frame center point.
     * @param extPV point/velocity for which range rate shall be computed
     * @param frame frame in which the point is defined
     * @param date computation date
     */
    public double getRangeRate(PVCoordinates extPV, Frame frame, AbsoluteDate date) 
        throws OrekitException {

        // Transform given point from given frame to topocentric frame
        final Transform t = frame.getTransformTo(this, date);
        final PVCoordinates extPVTopo = t.transformPVCoordinates(extPV);
        
        // Compute range rate (doppler) : relative rate along the line of sight
        return Vector3D.dotProduct(extPVTopo.getPosition(), extPVTopo.getVelocity()) 
               / extPVTopo.getPosition().getNorm();
        
    }
}