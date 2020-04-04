package uk.digitalsquid.physicsland;


public abstract class PhysicsObject
{
	public static enum ObjectType
	{
		CIRCLE,
		RECTANGLE,
		PATH
	}
	public int color, objId;
	public ObjectType type;
	public boolean fixed;
	
	public PhysicsObject(boolean fixed, int color, ObjectType type, int objId)
	{
		this.fixed = fixed;
		this.color = color;
		this.type = type;
		this.objId = objId;
	}
}
