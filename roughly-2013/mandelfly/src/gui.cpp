#include "gui.hpp"

#include <gtkmm/uimanager.h>
#include <iostream>
#include "cl.hpp"

using namespace mandel::gui;
using namespace mandel::comp;
using namespace mandel::comp::test;
using namespace Glib;
using namespace Glib::Threads;
using namespace Gtk;
using namespace sigc;
using namespace std;


MainScreen::MainScreen(cl::Platform &platform, std::vector<cl::Device> &devices) :
	Window(),
	platform(platform), devices(devices)
{
	set_title("Mandelfly");
	set_size_request(300, 300);

	actionGroup = ActionGroup::create();
	actionGroup->add(Action::create("MenuTests", "_Test"));
	actionGroup->add(Action::create("VecPerf", "Run Vector performance tests"),
			mem_fun(*this, &MainScreen::runTests));
	actionGroup->add(Action::create("MenuSettings", "_Settings"));
	actionGroup->add(Action::create("Iterations", "_Iterations"),
			mem_fun(*this, &MainScreen::changeIterations));
	actionGroup->add(Action::create("Tiles", "_Tiles"),
			mem_fun(*this, &MainScreen::changeNumTiles));
	actionGroup->add(Action::create("PrecInc", "Increase Precision"),
			mem_fun(*this, &MainScreen::increasePrecision));
	actionGroup->add(Action::create("PrecDec", "Decrease Precision"),
			mem_fun(*this, &MainScreen::decreasePrecision));
	RefPtr<UIManager> uiManager = UIManager::create();
	uiManager->insert_action_group(actionGroup);
	add_accel_group(uiManager->get_accel_group());
	uiManager->add_ui_from_string(
			"<ui>"
			"  <menubar name='MenuBar'>"
			"    <menu action='MenuTests'>"
			"      <menuitem action='VecPerf'/>"
			"    </menu>"
			"    <menu action='MenuSettings'>"
			"      <menuitem action='Iterations'/>"
			"      <menuitem action='Tiles'/>"
			"      <menuitem action='PrecInc'/>"
			"      <menuitem action='PrecDec'/>"
			"    </menu>"
			"  </menubar>"
			"</ui>"
			);

	add(vbox);
	vbox.pack_start(*uiManager->get_widget("/MenuBar"), Gtk::PACK_SHRINK);

	imageArea = new Fractal2D();
	vbox.pack_start(*imageArea);

	vbox.pack_start(statusBar, Gtk::PACK_SHRINK);
	statusBar.push("Loading");

	onLoadEnd.connect(mem_fun(*this, &MainScreen::loadEnd));
	loadThread = Thread::create(mem_fun(*this, &MainScreen::bgLoad));
	show_all_children();
}

MainScreen::~MainScreen() {
	delete imageArea;
	delete worker;
}

void MainScreen::bgLoad() {
	worker = new Computations(platform, devices);
	imageArea->setWorker(worker);
	numberTest = new NumberTest(*worker);
	// bignumTest = new BignumTest(*worker);
	fractalTest = new FractalTest(*worker);
	onLoadEnd();
}

void MainScreen::loadEnd() {
	loadThread->join();
	statusBar.push("Loaded");
	loadThread = NULL;

	/*
	complex<cl_float> centre(-0.5, 0);
	complex<cl_float> size(2, 2);

//	complex<cl_float> centre(-0.415, -0.683);
//	complex<cl_float> size(0.02, 0.02);
//	auto surface = fractal->generateFractal(centre, size);
//	auto surface = fractal->generateFractalOnHost(centre, size);
	auto surface = bn2Fractal->generateFractalOnHost(centre, size);
	imageArea->setSurface(surface); */
}

void MainScreen::runTests() {
	if(numberTest) numberTest->testFloats();
	if(numberTest) numberTest->testFloatVectors();
	if(bignumTest) bignumTest->testBignumMultiply();
	if(fractalTest) fractalTest->testGenerateFractal();
}

void MainScreen::changeIterations() {
	try {
		int iters = getNumber("Iterations", "Please enter the number of iterations to compute",
				*this, imageArea->getIterations());
		imageArea->setIterations(iters);
	} catch(std::exception e) { }
}

void MainScreen::changeNumTiles() {
	try {
		int tiles = getNumber("Tiles", "Please enter the number of tiles to split into",
				*this, imageArea->getTiles());
		imageArea->setTiles(tiles);
	} catch(std::exception e) { }
}

void MainScreen::increasePrecision() {
	imageArea->increasePrecision();
}

void MainScreen::decreasePrecision() {
	imageArea->decreasePrecision();
}
int mandel::gui::getNumber(Glib::ustring title, Glib::ustring message, Gtk::Window &window, int initial) {
	Gtk::Dialog dialog(title, window, true);
	Gtk::Label label(message);
	Gtk::Entry value;
	value.set_text(Glib::Ascii::dtostr(initial));

	Gtk::VBox vbox;
	dialog.get_content_area()->pack_start(vbox);
	vbox.pack_start(label);
	vbox.pack_start(value);
	dialog.show_all_children();
	dialog.add_button("OK", Gtk::RESPONSE_OK);
	dialog.run();

	int result = Glib::Ascii::strtod(value.get_text());
	return result;
}
