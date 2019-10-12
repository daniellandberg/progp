#Författare: Fredrik Diffner, Daniel Landberg

def dna():          # uppgift 1
    # A eller C eller G eller T, + = en eller flera gånger
    return "^(A|C|G|T)+$"

def sorted():       # uppgift 2
    #(?=) = "positive lookahead", kontrollerar så det finns minst en siffra. t(?=s) matchar andra t:et i streets, men inte s:et. * = ingen eller flera.
    return "^(?=\d)9*8*7*6*5*4*3*2*1*0*$"

def hidden1(x):     # uppgift 3
    #. = "vad som helst". Returnerar {vad som helst}{sökt sträng}{vad som helst}
    return ('.*'+x+'.*')

def hidden2(x):     # uppgift 4
    # indata x är strängen som vi vill konstruera ett regex för att söka efter
    reg = ".*"
    return ".*"+reg.join(x)+ ".*"

def equation():     # uppgift 5
    #\+ för att matcha specifika tecknet "+".
    #först ett valfritt + eller -, sedan minst en siffra, följt av valfritt (räknesätt följt av en eller flera siffror). Detta följt av ett valfritt = följt av exakt samma utryck som innan.
    return "^(\+|-)?[0-9]+((\+|-|\*|\/)[0-9]+)*(=(\+|-)?[0-9]+((\+|-|\*|\/)[0-9]+)*)?$"


def parentheses():  # uppgift 6
    return "^(\((\((\((\((\(\))*\))*\))*\))*\))+$"


def sorted3():      # uppgift 7
#Kan börja med vilka siffror som helst, följt av olika fall där en siffra mellan 0 och 9 föregås och följs av lämpliga siffror i stigande ordning. Avslutas med valfria siffror.
    return "^[0-9]*((01[2-9])|((0|1)2[3-9])|([0-2]3[4-9])|([0-3]4[5-9])|([0-4]5[6-9])|([0-5]6[7-9])|([0-6]7[8-9])|([0-7]89))[0-9]*$"



from sys import stdin
import re

def main():
    def hidden1_test(): return hidden1('test')
    def hidden2_test(): return hidden2('test')
    tasks = [dna, sorted, hidden1_test, hidden2_test, equation, parentheses, sorted3]
    print('Skriv in teststrängar:')
    while True:
        line = stdin.readline().rstrip('\r\n')
        if line == '': break
        for task in tasks:
            result = '' if re.search(task(), line) else 'INTE '
            print('%s(): "%s" matchar %suttrycket "%s"' % (task.__name__, line, result, task()))


if __name__ == '__main__': main()
