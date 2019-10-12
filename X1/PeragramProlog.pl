%Författare: Fredrik Diffner, Daniel Landberg

[kattio].

main :-
  repeat,
  read_string(String),
	(String == end_of_file;
	 peragram(String),
	 fail
	).

peragram(String):-
  string_to_list(String, List), % Gör om strängen till en lista
  count(List, 0).

/*Count skriver ut antalet gånger bokstäver uppträder ett ojämnt antal gånger i strängen -1. Om inga bosktäver förekommer ett
ojämnt antal gånger skrivs 0 ut.
Räknar ut detta rekursivt med hjälp av funktionen Occurs.
Kollar hur många gånger första elementet förekommer i hela listan. Beroende på om det är ett jämnt eller ojämnt antal adderas 1 till Count,
och listan rensas på det kollade elementet*/
count([], Count):-
  Result is Count -1,
  (Count is 0 -> write(Count); write(Result)),!. %-> = if. If boolean -> gör detta; annars detta. ! föhindrar backtracking.

count([H|T], Count):-
  occurs(H, [H|T], 0, Occ),
  delete([H|T], H, Result), %delete(List1, Element, List2) rensar listan List1 på elementet Element.
  NewCount is Count + 1,
  (0 is Occ mod 2 -> count(Result, Count); count(Result, NewCount)),!.

%Kontrollerar rekursivt hur många gånger ett element förekommer i en lista = Occ.
occurs(_, [], Res, Res).

occurs(Elem, [H|T], Res, Occ):-
  NewRes is Res + 1,
  (Elem is H -> occurs(Elem, T, NewRes, Occ); occurs(Elem, T, Res, Occ)),!.
