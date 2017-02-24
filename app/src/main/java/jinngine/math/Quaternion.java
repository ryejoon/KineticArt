/**
 * Copyright (c) 2008-2010  Morten Silcowitz.
 *
 * This file is part of the Jinngine physics library
 *
 * Jinngine is published under the GPL license, available 
 * at http://www.gnu.org/copyleft/gpl.html. 
 */
package jinngine.math;

import java.io.Serializable;


public final class Quaternion implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 
	 * Vector part
	 */
	public final Vector3 v = new Vector3(1,0,0);
	/**
	 * Scalar part
	 */
	public double s = 0.0;

	/**
	 *  Default constructor
	 */
	public Quaternion() {}
	
	/**
	 * Construct a new quaternion using the given scalar and vector parts
	 * @param s
	 * @param v
	 */
	public Quaternion( double s, Vector3 v ) {
		this.s = s;
		this.v.assign(v);
	}

	/**
	 * Return a quaternion representing a rotation of theta radians about the given n axis
	 * @param theta
	 * @param n
	 * @return
	 */
	public static Quaternion rotation( double theta, Vector3 n ) {
		return new Quaternion ( (double)Math.cos(theta/2.0f), n.multiply( (double)Math.sin(theta/2.0f) ) ); 
	}

	public Quaternion multiply( Quaternion q ) {
		// q*q' = [ ss'- v*v', sv' + s'v + v x v' ] see 
		return new Quaternion( s*q.s-v.dot(q.v), 
				q.v.multiply(s).add( v.multiply(q.s) ).add(  v.cross(q.v) ));  
	}

	public final void set( Quaternion qmark ) {
		//TODO, find some way of cleaning up the bad access to the Vector class, all over the code
		this.s = qmark.s;
		this.v.assign(qmark.v);
	}

	//q1 *= q2
	public static Quaternion sMultiply( Quaternion q1, Quaternion q2 ) {
		double new_s = q1.s*q2.s-q1.v.dot(q2.v);
		Vector3 new_v = q2.v.multiply(q1.s).add(q1.v.multiply(q2.s)).add(q1.v.cross(q2.v));  

		q1.s = new_s;
		q1.v.assign(new_v);

		return q1;
	}

	//Same as constructor
	public final void assign( double s, Vector3 v) {
		this.s = s;
		this.v.assign(v);
	}

	public final void assign( Quaternion q1) {
		this.s = q1.s;
		this.v.assign(q1.v);
	}


	public final void assign(Matrix3 m) {
		//TODO needs testing
		this.s = Math.sqrt(1.0 + m.a11 + m.a22 + m.a33) / 2.0;
		double w4 = (4.0 * this.s);
		this.v.x = (m.a32 - m.a23) / w4 ;
		this.v.y = (m.a13 - m.a31) / w4 ;
		this.v.z = (m.a21 - m.a12) / w4 ;
	}


	/** 
	 * Apply this quaternion as a rotation to the vector v
	 */
	public Vector3 rotate( Vector3 v ) {
		// PBA Theorem 18.42  p'= qpq* 
		// p is quaternion [0,(x,y,z)]
		// p' is the rotatet result
		// q  is the unit quaternion representing a rotation
		// q* is the conjugated q
		Quaternion vq = new Quaternion(0.0f, v);
		Quaternion rotatet = this.multiply(vq).multiply( this.conjugate() );

		return new Vector3(rotatet.v);
	}

	/** 
	 * Apply the quaternion q as a rotation to the vector v
	 */
	public static final void applyRotation( Quaternion q, Vector3 v ) {
		

		double s = -q.v.dot(v);  //scalar value of quaternion q*qv
		final Vector3 vtemp = v.multiply(q.s);         //vector part of q*qv, stored in v
		Vector3.crossProduct(q.v, v, v );        
		Vector3.add(v,vtemp);

		//reset vtemp
		vtemp.assign(new Vector3());

		//conjugate q
		//Quaternion.conjugate(q);
		Vector3.multiply(q.v, -1);

		//calculate the vector part of (q*qv)*q'
		Vector3.multiplyAndAdd( q.v, s, vtemp);
		Vector3.multiplyAndAdd( v, q.s, vtemp);
		// v = v x q.v
		Vector3.crossProduct(v,q.v, v);

		Vector3.add( v, vtemp );  //v is now rotated    

		//conjugate again so q is restored
		//Quaternion.conjugate(q);
		Vector3.multiply(q.v, -1);
	}

	/** 
	 * Add the quaternion q to this quaternion
	 */
	public Quaternion add( Quaternion q ) {
		return new Quaternion( s+q.s, v.add(q.v));
	}

	/**
	 * Add the quaternion a to the quaternion q and place the result in q, such that
	 * q = q + a
	 */
	public static void add( Quaternion q, Quaternion a ) {
		q.s += a.s;
		Vector3.add( q.v, a.v );
	}

	/**
	 * Multiply this quaternion by the given scalar a
	 */
	public Quaternion multiply( double a ) {
		return new Quaternion( s*a, v.multiply(a) );
	}


	/**
	 * Return the 2-norm of this quaternion
	 */
	public double norm() {
		return (double)Math.sqrt( s*s + this.v.squaredNorm() );
	}

	/**
	 * Conjugate this quaternion, so q=(s,v) becomes (s,-v)
	 */
	public Quaternion conjugate() {
		return new Quaternion( s, v.multiply(-1) );
	}

	/** 
	 * Conjugate the given quaternion q, such that q=(s,v) becomes (s,-v)
	 */
	public static final void conjugate( Quaternion q ) {
		Vector3.multiply(q.v, -1);
	}

	/**
	 * Convert the given quaternion q into the rotation matrix R. The result is placed 
	 * in the given Matrix3 R, and the reference for R is returned
	 */
	public static Matrix3 toRotationMatrix3( Quaternion q, Matrix3 R ) {
		Vector3 v = q.v;
		double s = q.s;

		R.assign(
				1-2*(v.y*v.y+v.z*v.z), 2*v.x*v.y-2*s*v.z,       2*s*v.y+2*v.x*v.z, 
				2*v.x*v.y+2*s*v.z,      1-2*(v.x*v.x+v.z*v.z), -2*s*v.x+2*v.y*v.z,
				-2*s*v.y+2*v.x*v.z,      2*s*v.x+2*v.y*v.z,       1-2*(v.x*v.x+v.y*v.y));

		return R;
	}

	/**
	 * Convert this quaternion into a new rotation matrix
	 * @return A new rotation matrix
	 */
	public Matrix3 toRotationMatrix3() {
		return Quaternion.toRotationMatrix3(this, new Matrix3() );
	}

	/**
	 * Convert this quaternion into a new Matrix4 transformation matrix, consisting of the 
	 * usual Matrix3 rotation part, and an identity translation part.
	 */
	public Matrix4 rotationMatrix4() {
		Matrix4 M = new Matrix4();
		Vector3 v = this.v;
		double s = this.s;
		M.a11 = 1-2*(v.y*v.y+v.z*v.z); M.a12 =  2*v.x*v.y-2*s*v.z;      M.a13 = 2*s*v.y+2*v.x*v.z;  
		M.a21 = 2*v.x*v.y+2*s*v.z;      M.a22 =  1-2*(v.x*v.x+v.z*v.z); M.a23 = -2*s*v.x+2*v.y*v.z;
		M.a31 = -2*s*v.y+2*v.x*v.z;     M.a32 =  2*s*v.x+2*v.y*v.z;      M.a33 =  1-2*(v.x*v.x+v.y*v.y);	  
		M.a44 = 1;	  
		return M;
	}

	/**
	 * Normalise this quaternion
	 */
	public void assignNormalized() {
		double l = (double)Math.sqrt( s*s + v.x*v.x + v.y*v.y + v.z*v.z );
		s = s/l;
		v.x = v.x/l;
		v.y = v.y/l;
		v.z = v.z/l;
	}

	/**
	 * Return a copy of this quaternion
	 */
	public Quaternion copy() {
		return new Quaternion(this.s,this.v);
	}

	/**
	 * The inner product of this quaternion and the given quaternion q
	 */
	public final double dot(Quaternion q) {
		return this.v.dot(q.v) + this.s * q.s;
	}

	/**
	 * Return an interpolated quaternion, based on this quaternion, the given quaternion q2, and the
	 * parameter t. 
	 * @param q2 quaternion to interpolate against
	 * @param t interpolation parameter in [0,1]
	 * @return
	 */
	public final Quaternion interpolate( Quaternion q2, double t) {
		//seems to be slerp interpolation of quaterions [02 03 2008]
		                                                 Quaternion qa = this;
		                                                 Quaternion qb = q2;
		                                                 //      qa sin((1-t) theta) + qb sin( t theta )
		                                                 //qm = ---------------------------------------  0<t<1 
		                                                 //                    sin theta
		                                                 //  	  
		                                                 //  theta = arccos( qa dot qb )
		                                                 double theta = Math.acos(qa.dot(qb));

		                                                 if (Math.abs(theta) < 1e-7 ) {
		                                                	 return this;
		                                                 }

		                                                 return qa.multiply(Math.sin((1-t)*theta))
		                                                 .add( qb.multiply( Math.sin( t*theta )))
		                                                 .multiply( 1/Math.sin(theta));
	}

	public static final Vector3 anguarVelocity(Quaternion q0, Quaternion q1) {
		Quaternion q0inv = new Quaternion(q0.s,q0.v.multiply(-1)).multiply(1/q0.dot(q0));
		Quaternion r = q0inv.multiply(q1);

		double sinomeganormhalf = r.v.norm();

		//zero angular velocity
		if (sinomeganormhalf < 1e-7 ) {
			return new Vector3();
		}

		Vector3 n = r.v.multiply(1/sinomeganormhalf);

		double omegaNorm = Math.asin(sinomeganormhalf)*2;

		return n.multiply(omegaNorm);
	}

	public static final Quaternion orientation( Vector3 unit) {
		Vector3 i = Vector3.i();

		double theta = Math.acos(i.dot(unit));
		Vector3 v = i.cross(unit);

		System.out.println("rotation axis is");
		System.out.println(v);
		System.out.println("and angle is " + theta );


		return Quaternion.rotation(theta, v);
	}


	public void Print() {
		System.out.println( "[ "+ s );
		System.out.println(v);
		System.out.println("]");
	}

	public float getAxisAngle(Vector3 axis){
		   if (s > 1) s = norm(); // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
		   float angle = (float) (2 * Math.acos(s));
		   float scale = (float) Math.sqrt(1-s*s); // assuming quaternion normalised then w is less than 1, so term always positive.
		   if (scale < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
		     // if s close to zero then direction of axis not important
		     axis.x = v.x; // if it is important that axis is normalised then replace with x=1; y=z=0;
		     axis.y = v.y;
		     axis.z = v.z;
		   } else {
			 axis.x = v.x / scale; // normalise axis
			 axis.y = v.y / scale;
			 axis.z = v.z / scale;
		   }
		   
		   return angle;

//		float scale = (float)Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
//		if(scale==0){
//			axis.x = 0;
//			axis.y = 0;
//			axis.z = 0;
//		}else{
//			axis.x = v.x / scale;
//			axis.y = v.y / scale;
//			axis.z = v.z / scale;
//		}
//		float angle = (float) (Math.acos(2*s));
//		return angle;
	}
}
