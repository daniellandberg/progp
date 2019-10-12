-- Författare : Fredrik Diffner & Daniel Landberg

import Data.List (sort)

main = do
  busNumber <- getLine --getLine tar input från I/O
  buses <- getLine --Skiter i första inputen, dvs antalet bussar
  --putStr skriver ut till I/O
  putStr(bussNummer (sort (makeIntArray buses))) -- Gör först om det till en lista med Integers genom funktionen makeIntArray, sorterar den sedan med sort

-- Gör om en sträng till en lista med integers
makeIntArray :: String -> [Integer]
makeIntArray [] = []
makeIntArray str = map read (words str) --Words tar en sträng "hej på dig" och gör om det till en lista av ord ["hej", "på" , "dig"]
-- read gör om en sträng till en Integer

-- Rekursiv funktion som sorterar bussnummer
-- -1 symboliserar ett "-"
-- Om vi har bussnummer som kan slås ihop, anropa då rekursivt men med första index som -1, för att hålla koll på att vi håller på att slå ihop
bussNummer :: [Integer] -> String
bussNummer [] = []
bussNummer [buss] = show buss
bussNummer [buss1, buss2]
  |buss1 == -1 = show buss2 -- Om näst sista är minus ett, har vi anropat ner till -<bussnummer>. Returnera då bara <bussnummer>
  |otherwise = show buss1 ++ " " ++ show buss2
bussNummer (buss1:buss2:buss3:rest)
  |buss1 + 1 == buss2 && buss2 + 1 == buss3 = show buss1 ++ "-" ++ bussNummer (-1:buss3:rest)
  |buss1 == -1 && buss2 + 1 == buss3 = bussNummer (-1:buss3:rest)
  |buss1 == -1 = show buss2 ++ " " ++ bussNummer (buss3:rest)
  |otherwise = show buss1 ++ " " ++ bussNummer (buss2:buss3:rest)
