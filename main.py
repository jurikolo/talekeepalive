import argparse
import requests


parser = argparse.ArgumentParser(description="The program checks hero's status and helps if necessary")
parser.add_argument("--email", help="Users email")
parser.add_argument("--password", help="User's password")
args = parser.parse_args()

api_client = "talekeepalive-0.0.1"
urls = {
    "login": "https://the-tale.org/accounts/auth/api/login",
    "logout": "https://the-tale.org/accounts/auth/api/logout?api_version=1.0&api_client=" + api_client,
    "auth": "https://the-tale.org/accounts/auth/api/login?api_version=1.0&api_client=" + api_client,
    "account_info": "https://the-tale.org/accounts/10745/api/show?api_version=1.0&api_client=" + api_client,
    "game_info": "https://the-tale.org/game/api/info?api_version=1.10&api_client=" + api_client,
    "card_list": "https://the-tale.org/game/cards/api/get-cards?api_version=2.0&api_client=" + api_client,
    "use_card": "https://the-tale.org/game/cards/api/use?api_version=2.0&api_client=" + api_client + "&card="
}
cards = {
    "resurrect": 160,
    "quest": 156
}

client = requests.session()
client.get(urls.get("login"))

headers = {'referer': 'https://the-tale.org/'}
csrf_token = ''
cookies = client.cookies
for cookie in client.cookies:
    if cookie.name == 'csrftoken':
        csrf_token = cookie.value

session = requests.Session()
data = {
    'email': args.email,
    'password': args.password,
    'csrfmiddlewaretoken': csrf_token
}

auth = session.post(urls.get("auth"), data=data, headers=headers, cookies=cookies)
data.pop("email")
data.pop("password")

print(f"Login result: {auth.text}\n\n")

game_info = session.get(urls.get("game_info"))

try:
    if game_info.json()["data"]["account"]["hero"]["base"]["alive"]:
        print("Hero is alive, nothing to do")
    else:
        print("Hero is dead, going to resurrect")
        card_list = session.get(urls.get("card_list"))
        user_cards = card_list.json()["data"]["cards"]
        for card in user_cards:
            if card.get("type") == cards.get("resurrect"):
                print(f"Following card will be used to resurrect the hero: {card}")
                break
except Exception as e:
    print(f"Something went wrong, exception: {e}")

is_lazy = False
try:
    quests = game_info.json()["data"]["account"]["hero"]["quests"]["quests"]  # quests 2 times, that's correct
    for quest in quests:
        if quest["line"][0]["type"] == "no-quest":
            is_lazy = True
except Exception as e:
    print(f"Something went wrong, exception: {e}")

if is_lazy:
    card_found = False
    print("Hero does nothing, going to stop that!")
    card_list = session.get(urls.get("card_list"))
    for card in card_list.json()["data"]["cards"]:
        if card.get("type") == cards.get("quest"):
            card_found = True
            print(f"Following card will be used to start questing: {card}")
            break

    if card_found:
        start_questing = session.post(urls.get("use_card") + card.get("uid"), data=data, headers=headers, cookies=cookies, allow_redirects=False)
        print(start_questing.status_code)
    else:
        print("There is no matching card to help the hero")
else:
    print("Hero is questing, nothing to do")

logout = session.get(urls.get("logout"))
