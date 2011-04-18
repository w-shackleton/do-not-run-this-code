package uk.digitalsquid.spacegame.spaceitem.interfaces;

public interface Warpable
{
	/**
	 * Allows objects within the game to return data mainly involving effects back to the main game loop.<br>
	 * @return Either {@code null} or a new {@link WarpData} with data to return.
	 */
	public WarpData sendWarpData();
	
	/**
	 * Stores data about the view warp, ie {@link #zoom}, {@link #rotation} and {@link #fade}.<br>
	 * This class also returns whether the level should end, in the variable {@link #endGame}, ie the player just died.
	 */
	public static final class WarpData
	{
		/**
		 * The current level zoom, with {@code 1} being no change in zoom level
		 */
		public float zoom = 1;
		/**
		 * A rotation in DEGREES by which to rotate the level around the centre of the screen.
		 */
		public float rotation = 0;
		/**
		 * An 8-bit unsigned {@link Integer} (ie {@code 0-255}) by the amount that the screen should be faded.<br>
		 * Default is 0, and complete black is {@code 255}
		 */
		public float fade = 0;
		/**
		 * If this {@link Boolean} is set to true, then the game will end most likely immediately, so make sure to perform any ending effects before setting this variable to true.
		 */
		public boolean endGame = false;
		
		public static final int END_NONE = 0;
		public static final int END_QUIT = 1;
		public static final int END_FAIL = 2;
		public static final int END_SUCCESS = 3;
		
		public int endReason = END_FAIL;
		
		public boolean stopTimer = false;
		
		public WarpData(float zoom, float rotation, float fade, boolean endGame)
		{
			this.zoom = zoom;
			this.rotation = rotation;
			this.fade = fade;
			this.endGame = endGame;
		}
		
		public WarpData(float zoom, float rotation, float fade, boolean endGame, int endReason)
		{
			this.zoom = zoom;
			this.rotation = rotation;
			this.fade = fade;
			this.endGame = endGame;
			this.endReason = endReason;
		}
		
		public WarpData(boolean endGame, int endReason)
		{
			this.endGame = endGame;
			this.endReason = endReason;
		}

		public WarpData()
		{
		}
		
		public WarpData(boolean stopTimer)
		{
			this.stopTimer = stopTimer;
		}
		
		/**
		 * Applies the data from oldData into this {@link WarpData}.
		 * @param oldData The data to apply into this object.
		 */
		public void apply(WarpData oldData)
		{
			fade += oldData.fade;
			rotation += oldData.rotation;
			zoom *= oldData.zoom;
			if(oldData.endGame)
				endGame = true;
			if(oldData.endReason != END_NONE)
				endReason = oldData.endReason;
			if(oldData.stopTimer)
				stopTimer = true;
		}
		
		public void reset() {
			fade = 0;
			rotation = 0;
			zoom = 1;
			endGame = false;
			endReason = 0;
			stopTimer = false;
		}
	}
}
