-- Författare: Fredrik Diffner & Daniel Landberg
-- 2019-01-26

module F1 where
  import Data.Char

  -- 1. Fibonacci-talen
  fib :: Integer -> Integer
  fib 0 = 0
  fib 1 = 1
  fib n = fib (n-1) + fib (n-2)

  --------------------------------------------------------------
  -- 2. Rövarspråket
  -- Rekursiv funktion som översätter en sträng till rövarspåket
  -- Använder hjälpfunktionen vokalEllerKonsonant
  rovarsprak :: String -> String
  rovarsprak [] = []
  rovarsprak (x:xs) = vokalEllerKonsonant x ++ rovarsprak xs

  -- Hjälpfunktion till rovarsprak. Om parametern är en
  -- vokal returneras densamma som en sträng.
  -- Är parametern x en konsonant returneras strängen "xox"
  vokalEllerKonsonant :: Char -> String
  vokalEllerKonsonant x
    |elem x "aeiouy" = [x]
    |otherwise = [x]++"o"++[x]       -- Varför kan vi inte ha x:"o":x ??

  -- Rekursiv funktion som översätter från rövarspråket till "vanligt" språk
  karpsravor :: String -> String
  karpsravor [] = []
  karpsravor [x] = [x]
  karpsravor [x,y] = [x,y]
  karpsravor (x:y:z:xs)
    |[y] == "o" && x == z && not (elem x "aeiouy") = [x] ++ karpsravor xs
    |otherwise = [x] ++ karpsravor (y:z:xs)

  --------------------------------------------------------------
  -- 3. Medellängd
  -- Returnerar ordens medellängden. Använder hjälpfunktionerna antalBokstaver
  -- och antalOrd
  medellangd :: String -> Double
  medellangd xs =
    (antalBokstaver xs)/(antalOrd xs)

  -- Hjälpfunktion. Returnerar antalet ord. Går igenom strängen och adderar 1
  -- när vi kommer till slutet av ett ord. isAlpha returnerar True om parametern
  -- är en del av alfabetet
  antalOrd :: String -> Double
  antalOrd [x]
    |isAlpha x = 1
    |otherwise = 0
  antalOrd (x:y:xs)
    |isAlpha x && isAlpha y = 0 + antalOrd (y:xs)
    |isAlpha x = 1 + antalOrd (y:xs)
    |otherwise = 0 + antalOrd (y:xs)

  -- Hjälpfunktion. Returnerar antalet bokstäver. isAlpha returnerar True om parametern
  -- är en del av alfabetet
  antalBokstaver :: String -> Double
  antalBokstaver [] = 0
  antalBokstaver (x:xs)
    |isAlpha x = 1 + antalBokstaver xs
    |otherwise = 0 + antalBokstaver xs

  ------------------------------------------------------------------
  -- 4. Listskyffling
  -- Skyfflar om en lista. Använder hjälpfunktionen varannan.
  skyffla :: [a] -> [a]
  skyffla [] = []
  skyffla [x] = [x]
  skyffla (x:y:xs) =
    varannan (x:y:xs) ++ skyffla (varannan (y:xs))

  -- Hjälpfunktion. Tar ut vartannat element i en lista
  varannan :: [a] -> [a]
  varannan [] = []
  varannan [x] = [x]
  varannan (x:y:xs) =
    x:varannan xs
