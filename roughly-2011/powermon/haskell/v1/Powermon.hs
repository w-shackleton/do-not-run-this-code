import DB
import Powergraph

import System.Console.GetOpt
import System.Environment

data Options = Options {
	outputFile :: String
}

startOptions = Options {
	outputFile = "chart.png"
}

options :: [OptDescr (Options -> IO Options)]
options = [
	Option "o" ["out"]
		(ReqArg (\arg opt -> return opt { outputFile = arg })
		"FILE")
	"File to save chart to"
	]

main = do
	-- Get options
	(actions, nonOptions, errors) <- getArgs >>= return . getOpt Permute options
	opts <- foldl (>>=) (return startOptions) actions

	putStrLn ("File is " ++ outputFile opts)

	conn <- createConnection
	putStrLn "Connected"

	power <- getPowerData conn
	putStrLn "Got data"

	disconnect conn
	return ()

