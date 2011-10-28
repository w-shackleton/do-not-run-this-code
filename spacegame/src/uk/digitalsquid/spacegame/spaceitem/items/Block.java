package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.IsClickable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

public class Block extends SpaceItem implements Moveable, Forceful, IsClickable {

	public Block(SimulationContext context, Vec2 pos, float angle, BodyType type) {
		super(context, pos, angle, type);
	}

	@Override
	public boolean isClicked(float x, float y) {
		return false;
	}

	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
	}

	@Override
	public void move(float millistep, float speedScale) {
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
	}
}
