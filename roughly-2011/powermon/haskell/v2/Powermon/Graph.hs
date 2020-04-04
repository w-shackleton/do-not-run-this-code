module Powermon.Graph where

import Graphics.Rendering.Plot
import Graphics.Rendering.Plot.Render
import Graphics.Rendering.Cairo (Render)
import Data.Packed.Vector

two :: Double -> Double
two = (2*)

dat :: Series
dat = fromList $ [1..10] ++ [2, 1, 0, -1, 1]

-- makeGraph :: Vector Double -> Vector Double -> Figure ()
makeGraph :: Vector Double -> Figure ()
makeGraph ys = do
	withTextDefaults $ setFontFamily "OpenSymbol"
	withTitle $ setText "Power usage"
	withSubTitle $ do
		setText "Daily power usage average"
		setFontSize 10
	setPlots 1 1
	withPlot (1,1) $ do
		-- setDataset [(Line, xs, ys)]
		setDataset (Line, [ys])
		addAxis XAxis (Side Lower) $ withAxisLabel $ setText "Time"
		addAxis YAxis (Side Lower) $ withAxisLabel $ setText "Power (W)"
		addAxis XAxis (Value 0) $ return ()
		setRangeFromData XAxis Lower Linear
		setRangeFromData YAxis Lower Linear
