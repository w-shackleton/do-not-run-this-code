#ifndef O_SPACEITEM_H
#define O_SPACEITEM_H

#include "../editorCallbacks.hpp"

//#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <tinyxml.h>

#include <wx/menu.h>
#include <wx/event.h>

#define BORDER_CLICK_SIZE 8
#define ROTATION_MULTIPLIER 0.01 /* Arbitary value, purely for user interactivity */

namespace Objects
{
	class SpaceItem
	{
		protected:
			virtual void saveXMLChild(TiXmlElement* item);

			virtual std::string getName() = 0;

			EditorCallbacks &callbacks;

			wxMenu *contextMenu;
			int contextMenuNextAvailableSlot;
		public:
			SpaceItem(EditorCallbacks &callbacks, double sx, double sy);
			SpaceItem(EditorCallbacks &callbacks, TiXmlElement &item);
			~SpaceItem();

			double x, y;

			virtual void draw(Cairo::RefPtr<Cairo::Context> &cr) = 0;
			virtual bool isClicked(int cx, int cy) = 0;
			virtual bool isBorderClicked(int cx, int cy) = 0;
			void move(double dx, double dy);
			virtual void moveBorder(int dx, int dy) = 0;

			virtual void scale(int r) = 0;
			virtual void rotate(double r) = 0; // In RADIANS

			virtual void saveXML(TiXmlElement& parent);

			inline wxMenu *getContextMenu()
			{
				return contextMenu;
			}
			bool recycle;

			virtual void onCMenuItemClick(int id);
	};
	enum
	{
		ID_CMenu_1 = wxID_HIGHEST + 1,
		ID_CMenu_2,
		ID_CMenu_3,
		ID_CMenu_4,
		ID_CMenu_5,
		ID_CMenu_6,
		ID_CMenu_7,
		ID_CMenu_8,
		ID_CMenu_9,
		ID_CMenu_10,
	};

};

#endif
