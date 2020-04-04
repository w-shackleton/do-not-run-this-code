module DB (createConnection, getPowerData, TimeData, disconnect) where

import System.IO
import Database.HDBC.ODBC
import Database.HDBC
-- import Data.Complex
import Data.Time.Clock
import Data.Time.Calendar
import GHC.Float

type TimeData = [(UTCTime, Float)]


-- main = do
-- 	conn <- createConnection
-- 	power <- getPowerData conn
-- 
-- 	putStrLn "Loaded DB info, writing file"
-- 
-- 	outFile <- openFile "fft.csv" WriteMode
-- 
-- 	let complexPower = map (\(_, power) -> (float2Double power) :+ 0) power
-- 	let fftList = fft complexPower
-- 
-- 	writeOutput outFile fftList
-- 
-- 	hClose outFile
-- 
-- writeOutput :: RealFloat c => Handle -> [Complex c] -> IO ()
-- writeOutput _ [] = do return ()
-- writeOutput handle (x:xs) = do
-- 	let real = show . realPart $ x
-- 	let imag = show . imagPart $ x
-- 	hPutStrLn handle $ real ++ "," ++ imag
-- 	writeOutput handle xs

connectionString = "DSN=owlro"

createConnection :: IO Connection
createConnection = do
	conn <- connectODBC connectionString
	return conn

getPowerData :: (IConnection conn) => conn -> IO [(UTCTime, Float)]
getPowerData conn = do
	--sqlQuery <- quickQuery conn "SELECT time, power from Power ORDER BY time ASC;" []
	sqlQuery <- quickQuery conn "SELECT MIN(time), AVG(power) FROM Power GROUP BY DATE(time), HOUR(time), MINUTE(time);" []
	return $ go sqlQuery
	where transform :: [SqlValue] -> (UTCTime, Float)
	      transform [sTime, sPower] = ((fromSql sTime)::UTCTime, double2Float ((fromSql sPower)::Double))
	      go :: [[SqlValue]] -> [(UTCTime, Float)]
	      go vals = do
	      		value <- vals
			return $ transform value

avg :: (Fractional a) => [a] -> a
avg xs = (sum xs) / (fromIntegral (length xs))
