import requests

url: str = 'http://localhost:42069'

Tokens: list = ['Ania','Bartek', 'Czarek', 'Daria', 'Ewa', 'Filip', 'Gosia']
Actions: list = ['Fold', 'Check', 'Call', 'Raise']

gameID = input("GAME ID: ")
print("----"+gameID+"----")

for token in Tokens:
    print(url+ '/joinGame/?playerToken=' + token + '&nickname=' + token + '&gameId=' + gameID)
    r = requests.get(url + '/joinGame/?playerToken=' + token + '&nickname=' + token + '&gameId=' + gameID)
    if(r.status_code != 200 and r.status_code != 201):
        print("Error: " + str(r.status_code))
        exit(1)

while(True):
    print("Players: ")
    for i in range(len(Tokens)):
        print(str(i) + ". " + Tokens[i])
    val = input("Choose player: ")
    if (not (int(val) < len(Tokens) and int(val) >= 0)):
        print("Wrong player number")
        continue
    print("Actions: ")
    for i in range(len(Actions)):
        print(str(i) + ". " + Actions[i])
    val2 = input("Choose action: ")
    if (not (int(val2) < len(Actions) and int(val2) >= 0)):
        print("Wrong action number")
        continue
    if(int(val2) == 3):
        val3 = input("Choose amount: ")
        r = requests.get(url + '/action' + Actions[int(val2)] + '/?playerToken=' + Tokens[int(val)] + '&amount=' + val3 + '&gameId=' + gameID)
    else:
        r = requests.get(url + '/action' + Actions[int(val2)] + '/?playerToken=' + Tokens[int(val)] + '&gameId=' + gameID)
    print("Status: " + str(r.status_code))