#ifndef PLANET_H
#define PLANET_H

#include "spherical.hpp"

#include "../misc/geometry.hpp"

namespace Objects
{
	namespace Helpers
	{
		class PlanetEditor;
	};
	class PlanetTypes;
	class Planet;
	class PlanetType
	{
		protected:
			friend class PlanetTypes;
			friend class Planet;
			friend class Objects::Helpers::PlanetEditor;

			PlanetType(int id, std::string filename, double bounciness, double density, int minSize, int maxSize, Misc::Colour bgCol);

			int id;

			std::string filename;
			double bounciness;
			double density;
			int minSize, maxSize;

			Misc::Colour bgCol;
		public:
			PlanetType();
	};

	class PlanetTypes : public std::vector<PlanetType> // Just a way of initialising at load
	{
		public:
			friend class Planet;
			PlanetTypes();

			enum // Existing numbers must never change; only add new
			{
				PLANET_n1, // Normal planets
				PLANET_n2,
				PLANET_n3,
				PLANET_sticky1, // Can't escape once hit
				PLANET_nobounce1, // Not very bouncy
				PLANET_bounce1, // VERY bouncy, elastic band planet
				PLANET_bounce2, // Bounciness of 1
			};
	};

	static PlanetTypes planetTypes; // TODO: Make this static

	class Planet : public Spherical
	{
		protected:
			inline std::string getName() { return "planet"; }
			void saveXMLChild(TiXmlElement* item);

//			static std::vector<Type> types = {Type(1, .5, "planet1.jpg")};
			

			int type;
			PlanetType planetType;
		public:
			Planet(int type, double sx, double sy, double sradius);
			Planet(TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
	};
};

#endif
