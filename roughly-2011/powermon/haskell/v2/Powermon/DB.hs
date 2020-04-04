module Powermon.DB (createConnection, closeConnection, getPowerData, normalisePower, dailyAvg, days) where

import System.IO
import Database.HDBC.ODBC
import Database.HDBC
-- import Data.Complex
import Data.Time.Clock
import Data.Time.Calendar
import GHC.Float
import Data.List
import Data.Fixed

import Powermon.Misc

connectionString = "DSN=owlro"

createConnection :: IO Connection
createConnection = do
	conn <- connectODBC connectionString
	return conn

closeConnection :: Connection -> IO ()
closeConnection = disconnect

getPowerData :: (IConnection conn) => conn -> IO [(UTCTime, Float)]
getPowerData conn = do
	sqlQuery <- quickQuery conn "SELECT MIN(time), AVG(power) FROM Power GROUP BY DATE(time), HOUR(time), MINUTE(time);" []
	return $ go sqlQuery
        where go :: [[SqlValue]] -> [(UTCTime, Float)]
	      go vals = do
	      		value <- vals
			return $ (\[sTime, sPower] -> (rollbackMin (fromSql sTime)::UTCTime, double2Float ((fromSql sPower)::Double))) value
			where rollbackMin :: UTCTime -> UTCTime
			      rollbackMin time = let timeSec :: Rational -- round time to first second in minute
			      			     timeSec = toRational $ utctDayTime time
			      			     timeRounded = timeSec - (timeSec `mod'` 60)
			      			 in UTCTime (utctDay time) (secondsToDiffTime $ floor timeRounded)

avg :: (Fractional a) => [a] -> a
avg xs = (sum xs) / (fromIntegral (length xs))

normalisePower :: [(UTCTime, Float)] -> [(UTCTime, Float)]
normalisePower orig =
			let grouped = groupBy' findGaps orig
			in foldr1 interpolateGaps grouped
			where findGaps (t1, _) (t2, _)
				| time2 > time1 = (time2 - time1) == 60
				 --			on same day	start of next day	end of previous
				| otherwise 	= diffDays day2 day1 == 1 && (time2 == 0) && (time1 >= 86400 - 60)
				where time1 = utctDayTime t1
				      time2 = utctDayTime t2
				      day1  = utctDay t1
				      day2  = utctDay t2
			      interpolateGaps :: [(UTCTime, Float)] -> [(UTCTime, Float)] -> [(UTCTime, Float)]
			      interpolateGaps section acc = -- Folds the list of lists together, fills in middle with guestimated values
			      		let (st, sp) = last section -- Start time & power
			      		    (ft, fp) = head acc -- End time & power
					    minsDifference = round $ (toRational $ diffUTCTime ft st) / 60
					    ps = interpolate sp fp minsDifference
					    ts = interpolate 0 (diffUTCTime ft st) minsDifference
					in section ++ (zipWith (\t p -> (addUTCTime t st, p)) ts ps) ++ acc

dailyAvg :: [(UTCTime,  Float)] -> [(UTCTime, Float)]
dailyAvg xs = let grouped = groupBy (\a b -> (utctDayTime $ fst a) == (utctDayTime $ fst b)) $
			sortBy (\a b -> compare (utctDayTime $ fst a) (utctDayTime $ fst b)) xs
	      in map average grouped

-- Gets the average for each day. Assumes data is already in time order
days :: [(UTCTime,  Float)] -> [(UTCTime, Float)]
days = map average . groupBy (\a b -> (utctDay $ fst a) == (utctDay $ fst b))

-- Averages the power usage list, also returning the first time in the list
average :: [(UTCTime, Float)] -> (UTCTime, Float)
average group = (fst $ head group, sum (map snd group) / (fromRational . toRational $ length group))
