-- Författare: Fredrik Diffner & Daniel Landberg
-- 2019-02-13

module F2 where
  import Data.List

  --skapar datatyperna Type och MolSeq
  data Type = DNA|Protein deriving(Show, Eq)
  data MolSeq = MolSeq {
    typ1 :: Type,
    sekvensnamn :: String,
    sekvens :: String
    } deriving (Show, Eq)

  --2.2
  -- Funktionen string2seq
  -- Undersöker om den givna sekvensen är ett DNA eller protein, och skapar
  -- datatypen MolSeq
  string2seq :: String -> String -> MolSeq
  string2seq sekvensnamn sekvens
    |dnaOrProtein sekvens = MolSeq DNA sekvensnamn sekvens
    |otherwise = MolSeq Protein sekvensnamn sekvens

  -- Hjälpfunktion till string2Seq, går igenom sekvensen och returnerar true om den bara
  -- innehåller bokstäverna ACTG, dvs om den är ett DNA
  dnaOrProtein :: String -> Bool
  dnaOrProtein [] = True
  dnaOrProtein (x:xs)
    |elem x "ACTG" = dnaOrProtein xs
    |otherwise = False

  --2.3
  -- Funktionen seqName. Tar datatypen MolSeq som parameter, returnerar dess namn
  seqName :: MolSeq -> String
  seqName x = sekvensnamn x

  -- Funktionen seqSequence. Tar datatypen MolSeq som parameter, returnerar dess sequence
  seqSequence :: MolSeq -> String
  seqSequence x = sekvens x

  -- Funktionen seqLength. Tar datatypen MolSeq som parameter, returnerar dess sekvens-längd
  seqLength :: MolSeq -> Int
  seqLength x = length (sekvens x)

  seqType :: MolSeq -> Type
  seqType x = typ1 x

  --2.3
  -- Funktionen seqDistance. Tar två st DNA- eller Protein sekvenser, returnerar dess evolutionära avstånd
  -- Returnerar error om parametrarna
  seqDistance :: MolSeq -> MolSeq -> Double
  seqDistance x1 x2
    |seqType x1 == seqType x2 && seqType x1 == DNA &&  a > 0.74 = 3.3
    |seqType x1 == seqType x2 && seqType x1 == Protein && a > 0.94 = 3.7
    |seqType x1 == seqType x2 && seqType x1 == DNA = (-3/4)*log(1-((4*a)/3))
    |seqType x1 == seqType x2 && seqType x1 == Protein = (-19/20)*log(1-((20*a)/19))
    |otherwise = error "Different types"
    where a = (seqDiff (seqSequence x1) (seqSequence x2))/(fromIntegral (seqLength x1))

  -- Hjälpfunktion till seqDistance. Räknar ut skillnaden mellan två sekvenser.
  seqDiff :: String -> String -> Double
  seqDiff [] [] = 0
  seqDiff (x1:xs1) (x2:xs2)
    |x1 == x2 = 0 + seqDiff xs1 xs2
    |otherwise = 1 + seqDiff
     xs1 xs2

  --3
  --3.1
  -- Skapar datatypen Profile
  data Profile = Profile {
    matris :: [[(Char, Int)]],
    typ2 :: Type,               -- typ2 för att inte ha samma namn som i datatypen Molseq
    antalSekv :: Int,
    namn :: String
    } deriving (Show)

  -- 3.2
  -- Funktionen molseqs2profile. Skapar en profil från givna sekvenser samt namn för profilen
  molseqs2profile :: String -> [MolSeq] -> Profile
  molseqs2profile namn molseqs -- Behövs felhantering? Om t.ex. vi får sekvenser av olika typ?. Behövs felhantering om olika långa sekvenser?
    |seqType (head molseqs) == DNA = Profile (makeProfileMatrix molseqs) DNA (length molseqs) namn
    |otherwise = Profile (makeProfileMatrix molseqs) Protein (length molseqs) namn

  nucleotides = "ACGT"
  aminoacids = sort "ARNDCEQGHILKMFPSTWYVX"

  makeProfileMatrix :: [MolSeq] -> [[(Char, Int)]]
  makeProfileMatrix [] = error "Empty sequence list"
  makeProfileMatrix sl = res
    where
      t = seqType (head sl)
      defaults =
        if (t == DNA) then                                    -- Om sekvenserna är ett DNA
          zip nucleotides (replicate (length nucleotides) 0)  -- Replicate skapar en lista av 0:or lika lång som längden på "nucleotides"
                                                              -- Zip "zippar ihop" de två listorna till en lista med tuplel-par. Varje par är
                                                              -- en nukloid och siffran noll. [(A, 0), (C, 0), (G, 0), (T, 0)]
        else
          zip aminoacids (replicate (length aminoacids) 0)    -- Samma som ovan fast med proteiner.
      strs = map seqSequence sl                               -- Map utför funktionen seqSequence på hela listan sl. Dvs skapar en lista med
                                                              -- alla listor av sekvenser.
      tmp1 = map (map (\x -> ((head x), (length x))) . group . sort) (transpose strs) -- transpose tar flera listor och lägger ihop elementen på respektive index i en lista. [[1,2,3],[4,5,6]] == [[1,4],[2,5],[3,6]]
                                                              -- group grupperar. group "Mississippi" = ["M","i","ss","i","ss","i","pp","i"]
                                                              -- sort bara för bokstavsordning.
                                                              -- (\x -> ((head x), (length x))) Gör en tupel av headen av x och x:s längd. x är i detta fall de förekomster av en viss bokstav på ett visst index.
                                                              -- Dvs; raden returnerar en lista med tupler som visar hur många gånger en bokstav förekommer på ett visst index.
                                                              -- map (map (\x -> ((head x), (length x))) . group . sort) (transpose ["ABCDP", "PDCBA", "AABBC"]) = [[('A',2),('P',1)],[('A',1),('B',1),('D',1)],[('B',1),('C',2)],[('B',2),('D',1)],[('A',1),('C',1),('P',1)]]
      equalFst a b = (fst a) == (fst b)                       -- Kollar om första elementet i tupeln a är lika med första elementet i tupeln b
      res = map sort (map (\l -> unionBy equalFst l defaults) tmp1) -- unionBy tar bort de element i andra listan där equalFst returnerar true. Dvs, innersta map-funktionen lägger till t.ex. (c, 0) om den redan inte finns i tmp1.
                                                              -- Den yttersta map sorterar bara matrisen.

  -- 3.3
  -- Funktionen profileName. Returnerar en profils sekvensnamn
  profileName :: Profile -> String
  profileName profile = namn profile

  -- Funktionen profileFrequency. Returnerar relativa frekvensen för en given bokstav på en given plats
  -- i en given profil
  profileFrequency :: Profile -> Int -> Char -> Double
  profileFrequency profil position tecken = relFrek                    -- Behövs felhantering om skickar in felaktiga sekvenser? Eller sekvenser av olika typer?
    where
      matrisen = matris profil                                         -- matrisen
      index = matrisen!!position                                       -- Listan med aktuellt index
      frekvens = fromIntegral (profileFrequencyMatch index tecken)     -- Tar ut frekvensen med hjälp av hjälpfunktionen profileFrequencyMatch. Gör om till double
      antalSekvIProfil = fromIntegral (antalSekv profil)               -- Antalet frekvenser i profilen, gör om till double
      relFrek = frekvens/antalSekvIProfil

  -- Hjälpfunktion till profileFrequency. Returnerar förekomsten av ett givet tecken på en given plats
  profileFrequencyMatch :: [(Char, Int)] -> Char -> Int
  profileFrequencyMatch [] tecken = 0 -- Detta borde inte behövas.
  profileFrequencyMatch (x:xs) tecken
    |fst x == tecken = snd x                                            -- Om tupeln innehåller det tecken vi vill ha, returnerar vi andra elementet i tupeln, dvs antalet förekomster av tecknet
    |otherwise = profileFrequencyMatch xs tecken                        -- Annars, anropar funktionen rekursivt.

  -- 3.4
  -- Funktionen profileDistance
  -- Räknar ut skillnaden mellan två givna profiler
  profileDistance :: Profile -> Profile -> Double
  profileDistance profil1 profil2                                       -- "foldl (\acc x -> acc f(x)) 0 xs" kör f(x) på hela listan xs (x är element i listan) och adderar resultatet.
                                                                        -- För varje index y (från längden på sekvenserna) räknar vi ut och adderar tecknen x på den positionen.
      |typ2 profil1 == DNA = foldl (\ acc1 y -> acc1 + (foldl (\acc2 x -> acc2 + abs((profileFrequency profil1 y x) - (profileFrequency profil2 y x))) 0 nucleotides)) 0  [0..(length (matris profil1) -1)]
      |otherwise = foldl (\ acc1 y -> acc1 + (foldl (\acc2 x -> acc2 + abs((profileFrequency profil1 y x) - (profileFrequency profil2 y x))) 0 aminoacids)) 0  [0..(length (matris profil1) -1)]

  -- 4
  -- Skapar typklassen Evol.
  -- Metoden name returnerar namnet på given Profile eller Molseq. Anropar funktionerna seqName eller profileName för respektive typ
  -- Metoden distance returnerar avståndet. Anropar seqDistance alternativt profileDistance beroende på typ
  -- distanceMatrix tar in en lista av Evols, returnerar avstånden som en matris. Tar hjälp av metoderna pairs, name och distance.
  -- pairs tar emot en lista, och returnerar permutationen av alla par i listan.
  class Evol a where
    name :: a -> String
    distance :: a -> a -> Double
    distanceMatrix :: [a] -> [(String, String, Double)]
    distanceMatrix evols = result
      where
         evolsPairs = pairs evols
         result = [(name (fst x), name (snd x), distance (fst x) (snd x)) | x <- evolsPairs ] -- x är en del av listan evolPairs, dvs x blir en tupel bestående av två evols.

    pairs :: [a] -> [(a,a)]
    pairs evols = [(x,y) | (x:ys) <- tails evols, y <- (x:ys)]  -- Returnerar en lista med tupler av alla par-kombinationer. Tails är en inbyggd funktion som returnerar en lista
                                                                -- med listor där huvudena är borttagna. tails [1,2,3] = [[1,2,3], [2,3], [3], []]
  -- Instansen Molseq
  instance Evol MolSeq where
    name molseq = seqName molseq
    distance molseq1 molseq2 = seqDistance molseq1 molseq2

  --Instansen Profile
  instance Evol Profile where
    name profile = profileName profile
    distance profile1 profile2 = profileDistance profile1 profile2
