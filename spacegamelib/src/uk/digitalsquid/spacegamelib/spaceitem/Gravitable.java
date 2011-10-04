package uk.digitalsquid.spacegamelib.spaceitem;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;

public abstract class Gravitable extends Spherical implements Forceful
{
	protected final float internalResistanceCoefficient;
	
	public Gravitable(SimulationContext context, Vec2 coord, float internalResistanceCoefficient, float density, float radius, BodyType type)
	{
		super(context, coord, density, radius, type);
		this.internalResistanceCoefficient = internalResistanceCoefficient;
		fixture.m_density = density;
	}
	
	private final Vec2 tmpRF = new Vec2();
	
	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV)
	{
		// float size = body.getFixtureList().m_shape.m_radius * body.getFixtureList().m_shape.m_radius;
		float size = body.getFixtureList().m_shape.m_radius * 4; // Relative pulls are less - large planets exert a less extreme force.
		CompuFuncs.computeForce(tmpRF, body.getPosition(), size * body.getFixtureList().getDensity(), getRadius(), itemC);
		if(VecHelper.dist(getPos(), itemC) < getRadius() + BALL_RADIUS) { // If inside
			tmpRF.x -= itemV.x * internalResistanceCoefficient;
			tmpRF.y -= itemV.y * internalResistanceCoefficient; // Apply damping
		}
		return tmpRF;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius) { }

	public float getDensity() {
		return fixture.m_density;
	}

	public void setDensity(float density) {
		fixture.m_density = density;
	}
}
