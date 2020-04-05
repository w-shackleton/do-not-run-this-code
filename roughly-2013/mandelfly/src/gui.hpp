/**
 * This module defines the basic GUI for the app
 */
#ifndef MANDEL_GUI_H
#define MANDEL_GUI_H

#include<vector>
#include<string>
#include<functional>
#include<glibmm/ustring.h>
#include<glibmm/i18n.h>
#include<glibmm/threads.h>
#include<glibmm/dispatcher.h>
#include<gtkmm/window.h>
#include<gtkmm/actiongroup.h>
#include<gtkmm/statusbar.h>
#include<gtkmm/dialog.h>
#include<gtkmm/label.h>
#include<gtkmm/comboboxtext.h>
#include<gtkmm/box.h>
#include<glibmm/stringutils.h>

#include "fractal2d.hpp"
#include "cl/mandel.hpp"

#include "clhead.hpp"
#include "cl.hpp"
#include "cl/test.hpp"

namespace mandel {
	namespace gui {
		template <class T>
			T* askUser(Glib::ustring title, Glib::ustring message, Gtk::Window &window, std::vector<T>& choices, std::function<Glib::ustring (T&)> getString) {
				Gtk::Dialog dialog(title, window, true);
				Gtk::Label label(message);
				Gtk::ComboBoxText selection;

				for(T &choice : choices) {
					selection.append(getString(choice));
				}

				Gtk::VBox vbox;
				dialog.get_content_area()->pack_start(vbox);
				vbox.pack_start(label);
				vbox.pack_start(selection);
				dialog.show_all_children();
				dialog.add_button("OK", Gtk::RESPONSE_OK);
				dialog.run();

				int result = selection.get_active_row_number();
				if(result == -1) return NULL;
				return &choices[result];
			}

		int getNumber(Glib::ustring title, Glib::ustring message, Gtk::Window &window, int initial);

		class MainScreen : public Gtk::Window {
			public:
				MainScreen(cl::Platform &platform, std::vector<cl::Device> &devices);
				virtual ~MainScreen();
			protected:
				void bgLoad();
				void loadEnd();

				void runTests();

				void changeIterations();
				void changeNumTiles();
				void increasePrecision();
				void decreasePrecision();
			private:
				mandel::comp::Computations *worker;
				mandel::comp::test::NumberTest *numberTest;
				mandel::comp::test::BignumTest *bignumTest;
				mandel::comp::test::FractalTest *fractalTest;
				cl::Platform platform;
				std::vector<cl::Device> devices;

				// Threads
				Glib::Threads::Thread *loadThread;

				// Dispatchers
				Glib::Dispatcher onLoadEnd;

				// GUI
				Glib::RefPtr<Gtk::ActionGroup> actionGroup;
				Gtk::Statusbar statusBar;
				Gtk::VBox vbox;

				Fractal2D *imageArea;
		};
	}
}

#endif
