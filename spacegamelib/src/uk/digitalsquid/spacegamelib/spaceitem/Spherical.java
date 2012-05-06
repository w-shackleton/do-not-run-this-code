package uk.digitalsquid.spacegamelib.spaceitem;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.IsClickable;

public abstract class Spherical extends SpaceItem implements IsClickable
{
	protected Fixture fixture;
	
	public Spherical(SimulationContext context, Vec2 coord, float density, float radius, BodyType type)
	{
		super(context, coord, 0, type);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new CircleShape();
		fixtureDef.density = density;
		fixtureDef.shape.m_radius = radius;
		fixtureDef.friction = 0.6f;
		fixtureDef.restitution = 0.7f;
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	@Override
	public boolean isClicked(float x, float y) {
		return Math.hypot(getPos().x - x, getPos().y - y) < getRadius();
	}
	
	public float getRadius() {
		return ((CircleShape)fixture.getShape()).m_radius;
	}
	
	public void setRadius(float radius) {
		((CircleShape)fixture.getShape()).m_radius = radius;
	}
}
