import Powermon.DB
import Powermon.Graph
import Graphics.Rendering.Plot.Render
import Graphics.Rendering.Cairo
import Data.Packed.Vector
import Data.Time.Clock
import GHC.Float

main = renderTestGraph 1920 1080

renderTestGraph :: Int -> Int -> IO ()
renderTestGraph w h = do
	surface <- createImageSurface FormatRGB24 w h

	putStrLn "Created surface"

	conn <- createConnection
	power <- getPowerData conn

	putStrLn "Got from DB"

	let norm = normalisePower $ dailyAvg power
	-- let norm = days $ normalisePower power
	let firstTime = fst . head $ norm
	-- let xs = fromList $ map (fromRational . toRational . flip diffUTCTime firstTime . fst) norm :: Vector Double
	let ys = fromList $ map (float2Double . snd) norm :: Vector Double


	putStrLn "Rendering"

	renderWith surface $ do
			let rend = makeGraph ys
			render rend (w,h)
	putStrLn "Saving"

	surfaceWriteToPNG surface "image.png"

	closeConnection conn

powerGet = do
	conn <- createConnection
	getPowerData conn
