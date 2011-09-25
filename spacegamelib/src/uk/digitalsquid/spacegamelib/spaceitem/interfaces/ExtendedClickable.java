package uk.digitalsquid.spacegamelib.spaceitem.interfaces;

/**
 * More advanced click actions
 * @author william
 *
 */
public interface ExtendedClickable extends Clickable {
	public void mouseDown(float x, float y);
	public void mouseMove(float x, float y);
	public void mouseUp(float x, float y);
}
