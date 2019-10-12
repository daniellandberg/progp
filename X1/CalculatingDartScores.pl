% Författare: Fredrik Diffner, Daniel Landberg
[kattio].
main :-
	repeat,
	read_int(X),
	(X == end_of_file ;
	 calculatingDartScores(X),
	 fail
	).

  % Testar kombinationer på de olika intervallen 1-20 och 0-3.
  % member(C, List2) kommer unifiera C med första elementet i listan List2.
  % Funkar inte det i Number is X*A + Y*B + Z*C, kommer prolog backtracka tillbaka och testa C som andra elementet i List2. Osv
calculatingDartScores(Number):-
  List1 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16, 17, 18, 19, 20],
  List2 = [0, 1, 2, 3],
  member(X, List1),
  member(Y, List1),
  member(Z, List1),
  member(A, List2),
  member(B, List2),
  member(C, List2),
  Number is X*A + Y*B + Z*C,
  print(X,A),!, nl, % nl = new line.
  print(Y,B),!, nl,
  print(Z,C),!.

calculatingDartScores(_):-
  write(impossible).

print(X,A):-
  A is 1,
  write("single "), write(X).

print(X,A):-
  A is 2,
  write("double "), write(X).

print(X,A):-
  A is 3,
  write("triple "), write(X).

print(_,A):-
  A is 0.
