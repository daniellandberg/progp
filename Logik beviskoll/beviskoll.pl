
% swipl
% ['beviskoll.pl'].
% trace.
% verify('testet.txt').


verify(InputFileName) :-
	see(InputFileName),
	read(Prems), read(Goal), read(Proof),
	seen,
	validate(Prems, Goal, Proof). %Validera beviset

validate(Prems, Goal , Proof) :-
  check_goal(Proof, Goal),
	proof(Prems, Proof, []).

check_goal(Proof, Goal) :-
	last(Proof, [_, Goal, _]).%kolla att sista objektet på listan Proof matchar formen för argument 2

proof(_, [], _).

proof(Prems, [H|T], Prooved) :-
	check_proof(Prems, H, Prooved),
	append(Prooved, [H], Prooved2),
	proof(Prems, T, Prooved2).


 %Basfallet för beviskontrollering. Om vi får in en tom lista för Proof terminerar programmet

%Kolla att premisserna är giltiga. ok
check_proof(Prems, [_, Prem, premise], _) :-
	member(Prem, Prems).

	%Anropa check_proof rekursivt,  med Tail:n för bevislistan och lägg till Head:n i listan med Prooved

%AND introduction
check_proof(_, [R, and(P, Q), andint(L1,L2)],  Prooved) :-
	R>L1, R>L2,	%Beviset i H ligger på rad med index R. L1 och L2 äor de raderna vi använder och-introduktion på. Dessa måste vara mindre än R för att beviset ska vara gitligt
	member([L1, P, _], Prooved),%Kolla att vi har bevisat att vi har P på rad L1
	member([L2, Q, _], Prooved).%Kolla att vi har bevisat att vi har Q på rad L2%När vi är klara med kontroll av AndInt lägger vi till beviset H i listan med alla kontrollerade bevis. Notera att positionen för H i Prooved inte spelar någon roll


%AND Elimination 1 kollat
check_proof(_, [R, P , andel1(L)], Prooved) :-
	R>L,
	member([L, and(P, _), _], Prooved). %Kolla att [L, P och _, _] finns med i bevisade isf lägger vi till H till Prooved.

%AND Elimination 2 kollat
check_proof(_, [R, P , andel2(L)], Prooved) :- %exakt som ovan fast med and(_,P)
	R>L,
	member([L, and(_, P), _], Prooved).

%OR introduction 1
check_proof(_, [R, or(P,_), orint1(L)], Prooved) :-
	R>L,
	member([L, P, _], Prooved).

%OR introduction 2
check_proof(_, [R, or(_,P), orint2(L)], Prooved) :-
	R>L,
	member([L, P, _], Prooved).


%OR elemination %test19
check_proof(_, [R, X, orel(L1,L2,L3,L4,L5)], Prooved) :-
	R>L1,R>L2,R>L3,R>L4,R>L5,
	member([L1, or(P,Q),_], Prooved), %kollar att premisen ligger i Prooved
	member([[L2, P, assumption]|Z], Prooved), %kollar att vårt första antagande ligger i Prooved
	if_empty(Z, Last_Line, L3).
	member([[L4, Q, assumption]|Z1], Prooved), %kollar att vårt andra antagande ligger i Prooved
	if_empty(Z, Last_Line, L5).

%IIMPLICATION introduction
check_proof(_, [R, imp(P,Q), impint(L1,L2)], Prooved) :-
	R>L1, R>L2,
	member([[L1,P,assumption]|Z], Prooved),
	if_empty(Z, Last_Line, L2).




%IIMPLICATION elimination
check_proof(_, [R, Q, impel(L1, L2)], Prooved) :-
	R>L1, R>L2,
	member([L1,Y,_], Prooved),
	member([L2,imp(Y,X),_], Prooved).

%NEGATION introduction
check_proof(_, [R, neg(P), negint(L1, L2)], Prooved) :-
	R>L1, R>L2,
	member([[L1, P , assumption]|Z], Prooved), %kollar ås första raden är assumtion.
	last(Z, Last_Line),
  if_empty(Z, Last_Line, L2).


%NEGATION elemination
check_proof(_, [R, cont, negel(L1,L2)], Prooved) :-
	R>L1, R>L2,
	member([L1, P, _], Prooved),
	member([L2, neg(P),_], Prooved).

%DOUBLE_NEGATION introduction
check_proof(_, [R, neg(neg(P)), negnegint(L)], Prooved) :-
	R>L,
	member([L, P,_], Prooved).

%DOUBLE_NEGATION elemination
check_proof(_, [R, P, negnegel(L)], Prooved) :-
	R>L,
	member([L, neg(neg(P)),_], Prooved).

%CONTRADICTION elimination
check_proof(_, [R, _, contel(L)], Prooved) :-
	R>L,
	member([L,cont,_], Prooved).

%ASSUMPTION
check_proof(Prems , [[X, Y, assumption]|T], Prooved) :-
	proof(Prems, T, [[X, Y, assumption]|Prooved]).

	%Check that all the proofs in the box are valid within

	%COPY
	check_proof(_, [R, P, copy(L)], Prooved) :-
		R>L,
		member([L, P, _], Prooved).
	%MODUS_TOLLENS
	check_proof(_, [R, neg(P), mt(L1,L2)], Prooved) :-
		R>L1,
		R>L2,
		member([L1, imp(P, Q), _], Prooved),
		member([L2, neg(Q), _], Prooved).

	%PROOF_BY_CONTRADICTION
	check_proof(_, [R, P, pbc(L1,L2)], Prooved) :-
		R>L1,
		R>L2,
		member([[L1, neg(P), assumption]|Z], Prooved),
		if_empty(Z, Last_Line, L2).

	%LAW_OF_EXCLUDED_MIDDLE
	check_proof(_, [_, or(P, neg(P)), lem], _).



	if_empty(Z, Last_Line, L2) :-
		length(Z, L),
		L == 0 ; last_element(Z, Last_Line, L2).

	last_element(Z, Last_Line, L2) :-
		last(Z, Last_Line),
		Last_Line = [L2,Q,_].
