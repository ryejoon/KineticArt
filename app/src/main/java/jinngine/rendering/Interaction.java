package jinngine.rendering;

import java.util.Iterator;

import jinngine.collision.RayCast;
import jinngine.geometry.Geometry;
import jinngine.geometry.SupportMap3;
import jinngine.math.InertiaMatrix;
import jinngine.math.Matrix3;
import jinngine.math.Vector3;
import jinngine.physics.Body;
import jinngine.physics.DefaultScene;
import jinngine.physics.Scene;
import jinngine.physics.constraint.joint.BallInSocketJoint;


public class Interaction implements Rendering.EventCallback {
	private final Scene scene;
	private boolean interacting = false;
	private Body target = null;
	private final Body controller = new Body("InteractionController");
	private final Vector3 pickpoint = new Vector3();
	private BallInSocketJoint force;
	private Matrix3 inertia;
	private Matrix3 inverse;
	private final Vector3 planeNormal = new Vector3(0,1,0);
	
	private static Object monitor = new Object();
	
	public Interaction( Scene scene) {
		this.scene = scene;
		this.controller.state.anisotropicmass.assignScale(1); // hope to prevent bugs
		this.controller.setFixed(true);
	}
	
	public void mouseScrolled(int scrolled){
		
		
	}
	
	@Override
	public void mouseDragged(double x, double y, Vector3 point, Vector3 direction) {
		// update body	
		// intersect the pointer ray with the interaction plane to get a target point
		double u = planeNormal.dot(pickpoint.sub(point)) / planeNormal.dot(direction);

		// move controller body to target position
		synchronized(DefaultScene.monitor){
			controller.setPosition(point.add(direction.multiply(u)));
		}
	}

	@Override
	public void mousePressed(double x, double y, Vector3 point, Vector3 direction) {

		
		target = null;
		interacting = false;
		double parameter = Double.POSITIVE_INFINITY;
		
		synchronized(DefaultScene.monitor){
			
		Iterator<Body> bodies = scene.getBodies();
		// go thru each body
		while (bodies.hasNext()) {
			Body bi = bodies.next();

			// only shoot at non-fixed bodies
			if ( !bi.isFixed()) {
				Iterator<Geometry> geometries = bi.getGeometries();
				Geometry gi = geometries.next();

				// if geometry is usable
				if ( gi instanceof SupportMap3) {

					RayCast raycast = new RayCast();
					Vector3 pb = new Vector3(), pc = new Vector3();
					double t = raycast.run((SupportMap3)gi, null, point, direction, pb, pc, 0, 0.05, 1e-7, true);

//					write out t for debugging
//					System.out.println("t="+t);
					
					if (t<parameter) {
						parameter = t;
						target = bi;
						pickpoint.assign(point.add(direction.multiply(t)));
//						pickpoint.print();
					}
				} // if support map
			} // body is unfixed
		}
		
		// clicked something?
		if (target != null) {
			interacting = true;
			target.changeColor();
//			System.out.println("found " + target);
			
			// place controller body into the world at the centre of mass
			// of target body
			controller.setVelocity(new Vector3());
			controller.setPosition(pickpoint);
			controller.updateTransformations();
			target.updateTransformations();

			this.force = new BallInSocketJoint(target, controller, controller.getPosition(), new Vector3(0,1,0));
			this.force.setForceLimit(1.5*target.getMass());
			this.force.setCorrectionVelocityLimit(7);
			
			// copy angular mass properties
			inertia = new InertiaMatrix(target.state.inertia);
			inverse = new InertiaMatrix(target.state.inverseinertia);

			// mute angular movement
			target.state.inverseinertia.assignZero();
			
			// remove current movement
			target.setVelocity(0,0,0);
			target.setAngularVelocity(0,0,0);

			// insert the acting stuff into the physics world
			scene.addBody(this.controller);
			scene.addConstraint(this.force);
			scene.addLiveConstraint(this.force);
		}
		
		}
	}

	@Override
	public void mouseReleased() {
		synchronized(DefaultScene.monitor){
			
		if (interacting) {						
			// remove controller body and force
			scene.removeConstraint(this.force);
			scene.removeBody(this.controller);	
			scene.removeLiveConstraint(this.force);
					
			// restore angular inertia properties
			target.state.inertia.assign(inertia);
			target.state.inverseinertia.assign(inverse);

			// reset state
			target = null;
			interacting = false;			
		}
		
		}
	}

	@Override
	public void spacePressed() {
		// set new plane normal and re-start pickpoint
		planeNormal.assign(0,0,1);
		pickpoint.assign(controller.getPosition());

	}

	@Override
	public void spaceReleased() {
		// set new plane normal and re-start pickpoint
		planeNormal.assign(0,1,0);	
		pickpoint.assign(controller.getPosition());

	}

}
