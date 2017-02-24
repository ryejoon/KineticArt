/**
 * Copyright (c) 2008-2010  Morten Silcowitz.
 *
 * This file is part of the Jinngine physics library
 *
 * Jinngine is published under the GPL license, available 
 * at http://www.gnu.org/copyleft/gpl.html. 
 */
package jinngine.examples;

import jinngine.collision.SAP2;


import jinngine.geometry.Box;
import jinngine.geometry.Geometry;
import jinngine.math.Vector3;
import jinngine.physics.*;
import jinngine.physics.constraint.Constraint;
import jinngine.physics.constraint.joint.UniversalJoint;
import jinngine.physics.force.GravityForce;
import jinngine.physics.solver.NonsmoothNonlinearConjugateGradient;
import jinngine.rendering.Interaction;
import jinngine.rendering.Rendering;
import com.ms.ryejoon.kineticart.CubeRenderer;
import com.ms.ryejoon.kineticart.TouchSurfaceView;

import java.util.*;

public class MobileExample implements Rendering.Callback {	
	private final Scene scene;
	private Vector<Geometry> geometries = new Vector();
	private Vector<Body> bodies = new Vector();
	private Vector<Constraint> constraints = new Vector();
	private static int itemNumber = 0;
	private CubeRenderer renderer;
	
	public MobileExample(CubeRenderer r) {
		renderer = r;		
		// start jinngine 
		scene = new DefaultScene(new SAP2(), new NonsmoothNonlinearConjugateGradient(75), new DefaultDeactivationPolicy());
		scene.setTimestep(0.03);
		/*
		// add boxes to bound the world
		Body floor = new Body("floor", new Box(1500,20,1500));
		floor.setPosition(new Vector3(0,-30,0));
		floor.setFixed(true);
		
		Body back = new Body( "back", new Box(200,200,20));		
		back.setPosition(new Vector3(0,0,-30));
		back.setFixed(true);

		Body front = new Body( "front", new Box(200,200,20));		
		front.setPosition(new Vector3(0,0,30));
		front.setFixed(true);

		Body left = new Body( "left", new Box(20,200,200));		
		left.setPosition(new Vector3(-25,0,0));
		left.setFixed(true);

		Body right = new Body( "right", new Box(20,200,200));		
		right.setPosition(new Vector3(25,0,0));
		right.setFixed(true);
		*/
		Geometry ceilingGeometry = new Box(1,1,1);
		Body ceiling = new Body("ceiling", ceilingGeometry);
		ceiling.setPosition(new Vector3(0, 10, 0));
		ceiling.setFixed(true);
		
		
		
		// head
		Geometry headgeometry = new Box(1,1,1);
		Body head = new MobileBody( "head", headgeometry );
		head.setPosition(new Vector3(0,0,0));
		geometries.add(headgeometry);
		bodies.add(head);
		
		//Constraing�� �ι�° ���ڴ�, ���� �� Body�� �߰� ��ǥ�ε�.
		Constraint link = new UniversalJoint(ceiling,head,new Vector3(0,5,0), new Vector3(0,1,0), new Vector3(1,0,0));
		scene.addConstraint(link);
	

		// torso1
		Geometry stickLv1geometry = new Box(8,0.5,0.5);
		MobileBody stickLv1 = new MobileBody( "stickLv1", stickLv1geometry );
		stickLv1.setPosition(new Vector3(0,-1.5,0));
		geometries.add(stickLv1geometry);
		bodies.add(stickLv1);
		
		hangBox(stickLv1, 0.5f);
		hangBox(stickLv1, 0.5f);
		hangBox(stickLv1, 0.5f);
		hangBox(stickLv1, 1.0f);
		hangBox(stickLv1, 1.0f);
		hangBox(stickLv1, 1.0f);
		hangBox(stickLv1, 0.8f);
		hangBox(stickLv1, 0.8f);


		Constraint neck = new UniversalJoint(head,stickLv1,new Vector3(0,-0.75,-0), new Vector3(0,1,0), new Vector3(1,0,0));
		scene.addConstraint(neck);
		

		// add all to scene
		/*
		scene.addBody(floor);
		scene.addBody(back);
		scene.addBody(front);		
		scene.addBody(left);
		scene.addBody(right);
		*/
		scene.addBody(ceiling);		
		
		/*
		scene.addBody(lv1LeftObject);
		scene.addBody(lv1RightObject);	
		scene.addBody(stickLv2L);
		scene.addBody(stickLv2R);	
		*/
//		 put gravity on limbs
		
		scene.addForce( new GravityForce(head));		
		scene.addForce( new GravityForce(stickLv1));		
		//scene.addForce( new GravityForce(lv1LeftObject));		
		//scene.addForce( new GravityForce(lv1RightObject));	
		
		//scene.addForce( new GravityForce(ceiling));		
		
		//scene.addForce( new GravityForce(stickLv2L));	
		//scene.addForce( new GravityForce(stickLv2R));	

		Iterator<Body> bi = bodies.iterator();
		while(bi.hasNext()){
			Body tempBody = (Body)bi.next();
			scene.addBody(tempBody);
			scene.addForce( new GravityForce(tempBody));
		}


		Iterator<Geometry> gi = geometries.iterator();
		while(gi.hasNext()){
			Geometry tempGeometry = (Geometry)gi.next();
			renderer.addToDraw(tempGeometry);	
		}
		
		Iterator<Constraint> ci = constraints.iterator();
		while(ci.hasNext()){
			Constraint tempConstraint = (Constraint)ci.next();
			scene.addConstraint(tempConstraint);
		}
		
		TouchSurfaceView.setCallback(new Interaction(scene));
		
	}

	@Override
	public void tick() {
		// each frame, to a time step on the Scene
		scene.tick();
	}
	
	
	public void hangBox(MobileBody root, float boxSize){
		MobileBody tempBody = root;
		//�ش� root�� �� ������ child���� ��������. �� ������ child�� tempBody�� �����
		while(tempBody.hasChild()){
			tempBody = tempBody.getChild();
		}
		
		Vector3 parentPosition = tempBody.getPosition();
		Geometry childGeometry = new Box(boxSize,boxSize,boxSize);
		MobileBody child = new MobileBody( Integer.toString(itemNumber), childGeometry );
		child.setPosition(new Vector3(parentPosition.x,parentPosition.y-1,parentPosition.z));
		geometries.add(childGeometry);
		bodies.add(child);
		Constraint link = new UniversalJoint(tempBody,child,new Vector3(parentPosition.x,parentPosition.y-0.5,parentPosition.z), new Vector3(0,1,0), new Vector3(1,0,0));
		constraints.add(link);
		
		tempBody.setChild(child);		
	}
	
	

}
