package uk.digitalsquid.physicsland;

public class Box2DInterface
{
	static
	{
		System.loadLibrary("physicsland");
	}
	public static final int maxBodySize=300;
	
	public native void createWorld(float minX, float minY, float maxX, float maxY, float gravityX, float gravityY);
	public native int createBox(float x, float y, float width, float height);
	public native int createCircle(float x, float y, float radius);
	public native int createCircle2(float x, float y, float radius, float weight, float restitution);
	public native BodyInfo getBodyInfo(BodyInfo info,int index);
	public native void step(float stepTime, int velocityIterations, int positionIterations) ;
	public native void setGravity(float gravityX, float gravityY) ;
	public native void destroyBody(int id) ;
	public native void getCollisions(collisionIdKeeper keeper,int index);
	public native int createBox2(float x,float y,float width,float height,float density,float restitution,float friction);
	public native void setBodyXForm(int id,float x,float y,float radiant);
	public native void setBodyAngularVelocity(int id,float radiant);
	public native void setBodyLinearVelocity(int id,float x,float y);
	public native BodyInfo getStatus(BodyInfo info,int index);
	public native BodyInfo getLinerVelocity(BodyInfo info,int index);
}
