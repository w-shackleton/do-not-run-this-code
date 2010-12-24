#ifndef PLANET_H
#define PLANET_H

#include "spherical.hpp"

#include "../misc/geometry.hpp"

namespace Objects
{
	class Planet : public Spherical
	{
		protected:
			inline std::string getName() { return "planet"; }
			void saveXMLChild(TiXmlElement* item);

//			static std::vector<Type> types = {Type(1, .5, "planet1.jpg")};
			
			class PlanetTypes;
			class PlanetType
			{
				protected:
					friend class PlanetTypes;
					friend class Planet;

					PlanetType(int id, std::string filename, double bounciness, double density, int minSize, int maxSize, Misc::Colour bgCol);

					int id;

					std::string filename;
					double bounciness;
					double density;
					int minSize, maxSize;

					Misc::Colour bgCol;
			};

			class PlanetTypes : std::vector<PlanetType> // Just a way of initialising at load
			{
				protected:
					PlanetTypes();
					friend class Planet;
			};

			PlanetTypes planetTypes;

		public:
			Planet(double sx, double sy, double sradius);
			Planet(TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

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
};
#endif
