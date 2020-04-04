package uk.digitalsquid.physicsland;


public class PhysicsObjectSquare extends PhysicsObject
{
	public PhysicsObjectSquare(boolean fixed, float width, float height, int color, int objId)
	{
		super(fixed, color, ObjectType.RECTANGLE, objId);
		this.width = width;
		this.height = height;
	}
	public float width;
	public float height;
}
