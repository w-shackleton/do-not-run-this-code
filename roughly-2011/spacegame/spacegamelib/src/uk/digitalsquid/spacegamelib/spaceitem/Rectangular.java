package uk.digitalsquid.spacegamelib.spaceitem;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.IsClickable;

public abstract class Rectangular extends SpaceItem implements IsClickable
{
	/**
	 * The size of this rectangle
	 */
	protected Vec2 size;
	
	protected Fixture fixture;
	private PolygonShape poly;
	
	/**
	 * @param coord		Center position of the rectangle
	 * @param size		Size of the rectangle
	 * @param rotation	Rotation of rectangle, in DEGREES
	 */
	public Rectangular(SimulationContext context, Vec2 coord, Vec2 size, float density, float rotation, float restitution, BodyType type)
	{
		super(context, coord, rotation, type);
		
		this.size = size;
		
		FixtureDef fixtureDef = new FixtureDef();
		poly = new PolygonShape();
		poly.setAsBox((float)size.x / 2, (float)size.y / 2);
		fixtureDef.shape = poly;
		fixtureDef.density = density;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = restitution;
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	@Override
	public boolean isClicked(float x, float y)
	{
		return CompuFuncs.pointInPolygon(getRectPos(), x, y);
	}
	
	private Vec2[] tmpRectPos;
	/**
	 * Get coordinates of the four corners of this rectangle
	 * @return An array of four coordinates
	 */
	protected Vec2[] getRectPos()
	{
		if(tmpRectPos == null) {
			tmpRectPos = new Vec2[4];
			tmpRectPos[0] = new Vec2();
			tmpRectPos[1] = new Vec2();
			tmpRectPos[2] = new Vec2();
			tmpRectPos[3] = new Vec2();
		}
		tmpRectPos[0].x = getPosX() + CompuFuncs.rotateX(-size.x / 2, -size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[0].y = getPosY() + CompuFuncs.rotateY(-size.x / 2, -size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[1].x = getPosX() + CompuFuncs.rotateX(+size.x / 2, -size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[1].y = getPosY() + CompuFuncs.rotateY(+size.x / 2, -size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[2].x = getPosX() + CompuFuncs.rotateX(+size.x / 2, +size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[2].y = getPosY() + CompuFuncs.rotateY(+size.x / 2, +size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[3].x = getPosX() + CompuFuncs.rotateX(-size.x / 2, +size.y / 2, getRotation() * DEG_TO_RAD);
		tmpRectPos[3].y = getPosY() + CompuFuncs.rotateY(-size.x / 2, +size.y / 2, getRotation() * DEG_TO_RAD);
		/*Log.v(TAG+ ret[0]);
		Log.v(TAG, "1:TAG);
		Log.v(TAG, "2: " + retTAG.v(TAG, "3: " + ret[3]);*/
		return tmpRectPos;
	}
	
}
