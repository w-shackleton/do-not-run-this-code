package uk.digitalsquid.physicsland;


public class PhysicsObjectCircle extends PhysicsObject
{
	public float rad;
	
	public PhysicsObjectCircle(boolean fixed, float rad, int color, int objId)
	{
		super(fixed, color, ObjectType.CIRCLE, objId);
		this.rad = rad;
	}
}
