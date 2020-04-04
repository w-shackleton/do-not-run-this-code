#ifndef EDITORCALLBACKS_H
#define EDITORCALLBACKS_H

/*
   Callbacks from the main interface to various things in the program
   */
class EditorCallbacks
{
	public:
		EditorCallbacks();

		virtual void onRefresh() = 0;
};

#endif
