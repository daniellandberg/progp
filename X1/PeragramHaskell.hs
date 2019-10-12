-- Författare Fredrik Diffner, Daniel Landberg

import Data.List (sort)
import Data.List (group)

-- Ett Peragram kan innehålla max en grupp bokstäver med udda längd.
main = do
  string <- getLine
  let
    sortString = group (sort string) -- Sorterar och grupperar. Dvs "abacdc" blir ["aa", "b", "cc", "d"]
  putStr(show (howManyToTakeAway sortString))

-- Tar en lista med grupperade bosktäver. Kollar hur många av de grupperade bokstäverna som är av udda antal.
-- Tar svaret av det och subraherar 1, dvs hur många bokstäver behöver vi minst ta bort för att få max en gruppering av udda antal.
-- Om det blir -1 returneras 0
howManyToTakeAway :: [String] -> Int
howManyToTakeAway list
  |ans < 0 = 0
  |otherwise = ans
  where
    ans = (sum (map (`mod` 2) (map length list)) -1) --Tar mod 2 på längden av alla element, dvs kollar hur många element som är av ojämn längd. Summerar sedan och tar -1
