module Powermon.Misc(groupBy', interpolate) where

import Data.List(unfoldr)

-- A groupBy which works for non-equality grouping
groupBy' :: (a -> a -> Bool) -> [a] -> [[a]]
groupBy' _   []                        = []
groupBy' _   [x]                       = [[x]]
groupBy' cmp (x:xs@(x':_)) | cmp x x'  = (x:y):ys
                           | otherwise = [x]:r
  where r@(y:ys) = groupBy' cmp xs

-- Interpolates between 2 values
interpolate :: (Fractional a) => a -> a -> Integer -> [a]
interpolate start end number = let step = (((end - start)/(fromInteger number)))
			       in unfoldr (\num -> if num == (fromInteger number) -- Unfold sequence into a list
			       		then Nothing else
					Just (start+(num*step),num+1)) 1
