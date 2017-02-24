/**
 * Copyright (c) 2008-2010  Morten Silcowitz.
 *
 * This file is part of the Jinngine physics library
 *
 * Jinngine is published under the GPL license, available 
 * at http://www.gnu.org/copyleft/gpl.html. 
 */
package jinngine.math;

public class Matrix4 {

    	  public double a11, a12, a13, a14;
	  public double a21, a22, a23, a24;
	  public double a31, a32, a33, a34;
	  public double a41, a42, a43, a44;
	  	  
	  public Matrix4() {
            a11=0; a12=0; a13=0; a14=0;
            a21=0; a22=0; a23=0; a24=0;
            a31=0; a32=0; a33=0; a34=0;
            a41=0; a42=0; a43=0; a44=0;
          }

          public final Matrix4 assignZero() {
                a11=0; a12=0; a13=0; a14=0;
                a21=0; a22=0; a23=0; a24=0;
                a31=0; a32=0; a33=0; a34=0;
                a41=0; a42=0; a43=0; a44=0;
                return this;
          }

	  public Matrix4(double a11, double a12, double a13, double a14,
			         double a21, double a22, double a23, double a24,
			         double a31, double a32, double a33, double a34,
			         double a41, double a42, double a43, double a44	  
	  ) {		  
		  this.a11=a11; this.a12=a12; this.a13=a13; this.a14=a14;
		  this.a21=a21; this.a22=a22; this.a23=a23; this.a24=a24;
		  this.a31=a31; this.a32=a32; this.a33=a33; this.a34=a34;
		  this.a41=a41; this.a42=a42; this.a43=a43; this.a44=a44;
	  }
	  public Matrix4 assign(double a11, double a12, double a13, double a14,
			         double a21, double a22, double a23, double a24,
			         double a31, double a32, double a33, double a34,
			         double a41, double a42, double a43, double a44
	  ) {
		  this.a11=a11; this.a12=a12; this.a13=a13; this.a14=a14;
		  this.a21=a21; this.a22=a22; this.a23=a23; this.a24=a24;
		  this.a31=a31; this.a32=a32; this.a33=a33; this.a34=a34;
		  this.a41=a41; this.a42=a42; this.a43=a43; this.a44=a44;
                  return this;
	  }

	  
	  public Matrix4( Matrix4 M) {
                assign(M);
	  }

          public final Matrix4 assign(Matrix4 M) {
                a11 = M.a11; a12 = M.a12; a13 = M.a13; a14 = M.a14;
                a21 = M.a21; a22 = M.a22; a23 = M.a23; a24 = M.a24;
                a31 = M.a31; a32 = M.a32; a33 = M.a33; a34 = M.a34;
                a41 = M.a41; a42 = M.a42; a43 = M.a43; a44 = M.a44;
                return this;
          }

          public Matrix4(double[] m) {
              assign(m);
          }
          


          public Matrix4(float[] m) {
              assign(m);
          }

          public Matrix4 assign(float[] array) { 
		  a11 = array[0];
		  a21 = array[1];
		  a31 = array[2];
		  a41 = array[3];

		  a12 = array[4];
		  a22 = array[5];
		  a32 = array[6];
		  a42 = array[7];

		  a13 = array[8];
		  a23 = array[9];
		  a33 = array[10];
		  a43 = array[11];

		  a14 = array[12];
		  a24 = array[13];
		  a34 = array[14];
		  a44 = array[15];
                  return this;
          }

		/**
	   *
	   * @param M
	   * @param array
	   */
	  public final Matrix4 assign( double[] array) {
		  a11 = array[0];
		  a21 = array[1];
		  a31 = array[2];
		  a41 = array[3];

		  a12 = array[4];
		  a22 = array[5];
		  a32 = array[6];
		  a42 = array[7];

		  a13 = array[8];
		  a23 = array[9];
		  a33 = array[10];
		  a43 = array[11];

		  a14 = array[12];
		  a24 = array[13];
		  a34 = array[14];
		  a44 = array[15];
                  return this;
	  }
	  
	  public final Matrix4 assign(int[] array) {
		  a11 = array[0];
		  a21 = array[1];
		  a31 = array[2];
		  a41 = array[3];

		  a12 = array[4];
		  a22 = array[5];
		  a32 = array[6];
		  a42 = array[7];

		  a13 = array[8];
		  a23 = array[9];
		  a33 = array[10];
		  a43 = array[11];

		  a14 = array[12];
		  a24 = array[13];
		  a34 = array[14];
		  a44 = array[15];
                  return this;
	  }

          public static Matrix4 identity() {
              return new Matrix4().assignIdentity();
	  }

          /**
	   * Assign the identity matrix to this matrix4
	   */
	  public final Matrix4 assignIdentity() {
		  a11=1; a12=0; a13=0; a14=0;
		  a21=0; a22=1; a23=0; a24=0;
		  a31=0; a32=0; a33=1; a34=0;
		  a41=0; a42=0; a43=0; a44=1;
                  return this;
	  }

          
	  //C = AxB 
	  public static Matrix4 multiply( final Matrix4 A, final Matrix4 B, final Matrix4 C ) {
	    //                   B | b11 b12 b13 b14
	    //                     | b21 b22 b23 b24
	    //                     | b31 b32 b33 b34
		//                     | b41 b42 b43 b44
	    //     ----------------------------------
	    //  A  a11 a12 a13 a14 | c11 c12 c13 c14
	    //     a21 a22 a23 a24 | c21 c22 c23 c24
	    //     a31 a32 a33 a34 | c31 c32 c33 c34
	    //     a41 a42 a43 a44 | c41 c42 c43 c44
		  
	    final double t11 = A.a11*B.a11 + A.a12*B.a21 + A.a13*B.a31 + A.a14*B.a41;
	    final double t12 = A.a11*B.a12 + A.a12*B.a22 + A.a13*B.a32 + A.a14*B.a42;
	    final double t13 = A.a11*B.a13 + A.a12*B.a23 + A.a13*B.a33 + A.a14*B.a43;
	    final double t14 = A.a11*B.a14 + A.a12*B.a24 + A.a13*B.a34 + A.a14*B.a44;
	    
	    final double t21 = A.a21*B.a11 + A.a22*B.a21 + A.a23*B.a31 + A.a24*B.a41;
	    final double t22 = A.a21*B.a12 + A.a22*B.a22 + A.a23*B.a32 + A.a24*B.a42;
	    final double t23 = A.a21*B.a13 + A.a22*B.a23 + A.a23*B.a33 + A.a24*B.a43;
	    final double t24 = A.a21*B.a14 + A.a22*B.a24 + A.a23*B.a34 + A.a24*B.a44;
	    
	    final double t31 = A.a31*B.a11 + A.a32*B.a21 + A.a33*B.a31 + A.a34*B.a41;
	    final double t32 = A.a31*B.a12 + A.a32*B.a22 + A.a33*B.a32 + A.a34*B.a42;
	    final double t33 = A.a31*B.a13 + A.a32*B.a23 + A.a33*B.a33 + A.a34*B.a43;
	    final double t34 = A.a31*B.a14 + A.a32*B.a24 + A.a33*B.a34 + A.a34*B.a44;
       
	    final double t41 = A.a41*B.a11 + A.a42*B.a21 + A.a43*B.a31 + A.a44*B.a41;
	    final double t42 = A.a41*B.a12 + A.a42*B.a22 + A.a43*B.a32 + A.a44*B.a42;
	    final double t43 = A.a41*B.a13 + A.a42*B.a23 + A.a43*B.a33 + A.a44*B.a43;
	    final double t44 = A.a41*B.a14 + A.a42*B.a24 + A.a43*B.a34 + A.a44*B.a44;


	    //copy to C
	    C.a11 = t11;
	    C.a12 = t12;
	    C.a13 = t13;
	    C.a14 = t14;

	    C.a21 = t21;
	    C.a22 = t22;
	    C.a23 = t23;
	    C.a24 = t24;

	    C.a31 = t31;
	    C.a32 = t32;
	    C.a33 = t33;
	    C.a34 = t34;

	    C.a41 = t41;
	    C.a42 = t42;
	    C.a43 = t43;
	    C.a44 = t44;

            return C;
	  }
	  
	  /**
	   * Multiply this matrix by A and return the result
	   * @param A
	   * @return
	   */
	  public Matrix4 multiply( final Matrix4 A ) {		 
		  return Matrix4.multiply(this, A, new Matrix4());		  
	  }
	  
	  /**
	   * 
	   * @param v
	   * @return
	   */
	  public Vector3 multiply( Vector3 v) {		
		  return Matrix4.multiply(this, v, new Vector3());
	  }
	  
	  //Transform a vector in R3 to a homogeneous vector in R4, perform matrix mult,
	  //and transform back into an R3 vector  
	  //r = Av
	  public static Vector3 multiply( final Matrix4 A, final Vector3 v, final Vector3 r ) {
		  
	    //                   V | v1
	    //                     | v2
	    //                     | v3
		//                     | 1
	    //     -----------------------
	    //  A  a11 a12 a13 a14 | c1
	    //     a21 a22 a23 a24 | c2
	    //     a31 a32 a33 a34 | c3   
	    //     a41 a42 a43 a44 | c4
		  
		  final double t1 = v.x*A.a11+v.y*A.a12+v.z*A.a13+ 1*A.a14;
		  final double t2 = v.x*A.a21+v.y*A.a22+v.z*A.a23+ 1*A.a24;
		  final double t3 = v.x*A.a31+v.y*A.a32+v.z*A.a33+ 1*A.a34;
		  final double t4 = v.x*A.a41+v.y*A.a42+v.z*A.a43+ 1*A.a44;

	    r.x = t1/t4;
	    r.y = t2/t4;
	    r.z = t3/t4;
	    
	    return r;
	  }  

	  public double[] toArray() {
		  return new double[]{
		  a11,a21,a31,a41,
                  a12,a22,a32,a42,
                  a13,a23,a33,a43,
                  a14,a24,a34,a44};
	  }
	  
	  public final Matrix4 inverse() {
                  Matrix4 m=new Matrix4();
		  m.a11 =      a22*a33*a44 - a22*a34*a43 - a32*a23*a44 + a32*a24*a43 + a42*a23*a34 - a42*a24*a33;
		  m.a12 =    - a12*a33*a44 + a12*a34*a43 + a32*a13*a44 - a32*a14*a43 - a42*a13*a34 + a42*a14*a33;
		  m.a13 =      a12*a23*a44 - a12*a24*a43 - a22*a13*a44 + a22*a14*a43 + a42*a13*a24 - a42*a14*a23;
		  m.a14 =    - a12*a23*a34 + a12*a24*a33 + a22*a13*a34 - a22*a14*a33 - a32*a13*a24 + a32*a14*a23;
		  m.a21 =    - a21*a33*a44 + a21*a34*a43 + a31*a23*a44 - a31*a24*a43 - a41*a23*a34 + a41*a24*a33;
		  m.a22 =      a11*a33*a44 - a11*a34*a43 - a31*a13*a44 + a31*a14*a43 + a41*a13*a34 - a41*a14*a33;
		  m.a23 =    - a11*a23*a44 + a11*a24*a43 + a21*a13*a44 - a21*a14*a43 - a41*a13*a24 + a41*a14*a23;
		  m.a24 =      a11*a23*a34 - a11*a24*a33 - a21*a13*a34 + a21*a14*a33 + a31*a13*a24 - a31*a14*a23;
		  m.a31 =      a21*a32*a44 - a21*a34*a42 - a31*a22*a44 + a31*a24*a42 + a41*a22*a34 - a41*a24*a32;
		  m.a32 =    - a11*a32*a44 + a11*a34*a42 + a31*a12*a44 - a31*a14*a42 - a41*a12*a34 + a41*a14*a32;
		  m.a33 =      a11*a22*a44 - a11*a24*a42 - a21*a12*a44 + a21*a14*a42 + a41*a12*a24 - a41*a14*a22;
		  m.a34 =    - a11*a22*a34 + a11*a24*a32 + a21*a12*a34 - a21*a14*a32 - a31*a12*a24 + a31*a14*a22;
		  m.a41 =    - a21*a32*a43 + a21*a33*a42 + a31*a22*a43 - a31*a23*a42 - a41*a22*a33 + a41*a23*a32;
		  m.a42 =      a11*a32*a43 - a11*a33*a42 - a31*a12*a43 + a31*a13*a42 + a41*a12*a33 - a41*a13*a32;
		  m.a43 =    - a11*a22*a43 + a11*a23*a42 + a21*a12*a43 - a21*a13*a42 - a41*a12*a23 + a41*a13*a22;
		  m.a44 =      a11*a22*a33 - a11*a23*a32 - a21*a12*a33 + a21*a13*a32 + a31*a12*a23 - a31*a13*a22;
		 
		  double D = a11*m.a11 + a21*m.a12 +  a31*m.a13 + a41*m.a14;
		  if(D != 0)
		  {
			  m.a11 /=D; m.a12 /=D; m.a13 /=D; m.a14 /=D;
			  m.a21 /=D; m.a22 /=D; m.a23 /=D; m.a24 /=D;
			  m.a31 /=D; m.a32 /=D; m.a33 /=D; m.a34 /=D;
			  m.a41 /=D; m.a42 /=D; m.a43 /=D; m.a44 /=D;
		  }
		  return m;
	  }
    public static Matrix4 scaleMatrix(double d) {
        return new Matrix4().assignScale(d,d,d);
    }
    public static Matrix4 scaleMatrix(double x,double y, double z) {
        return new Matrix4().assignScale(x,y,z);
    }
    public Matrix4 assignScale(double d) {
        return assignScale(d, d, d);
    }

    public Matrix4 assignScale(double x,double y, double z) {
        a11=x; a12=0; a13=0; a14=0;
	a21=0; a22=y; a23=0; a24=0;
	a31=0; a32=0; a33=z; a34=0;
	a41=0; a42=0; a43=0; a44=1;
        return this;
    }

    public Matrix4 assignMultiply(Matrix4 m) {
        return multiply(this, m, this);
    }
    public boolean isNaN() {
        return Double.isNaN(a11)
                || Double.isNaN(a12)
                || Double.isNaN(a13)
                || Double.isNaN(a14)
                || Double.isNaN(a21)
                || Double.isNaN(a22)
                || Double.isNaN(a23)
                || Double.isNaN(a24)
                || Double.isNaN(a31)
                || Double.isNaN(a32)
                || Double.isNaN(a33)
                || Double.isNaN(a34)
                || Double.isNaN(a41)
                || Double.isNaN(a42)
                || Double.isNaN(a43)
                || Double.isNaN(a44);
    }

    @Override
    public String toString() {
        return "[" + a11 + ", " + a12 + ", " + a13 + ", " + a14 + "]\n"
                + "[" + a21 + ", " + a22 + ", " + a23 + ", " + a24 + "]\n"
                + "[" + a31 + ", " + a32 + ", " + a33 + ", " + a34 + "]\n"
                + "[" + a41 + ", " + a42 + ", " + a43 + ", " + a44 + "]";
    }

    
}
