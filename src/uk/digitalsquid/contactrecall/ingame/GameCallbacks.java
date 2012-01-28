package uk.digitalsquid.contactrecall.ingame;

/**
 * Callback functions from the game itself to the activity that is hosting it.
 * @author william
 *
 */
interface GameCallbacks {
	void onGamePaused();
	void onGameResumed();
	void onGameCancelled();
	
	static final GameCallbacks EMPTY_CALLBACKS = new GameCallbacks() {
		@Override
		public void onGameResumed() {
		}
		
		@Override
		public void onGamePaused() {
		}
		
		@Override
		public void onGameCancelled() {
		}
	};
}
