import requests
import random
import os


def gen_csrftoken():
    size = 64
    allowed_chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    return "".join(random.choice(allowed_chars) for x in range(size))


def handler(event=None, context=None):
    api_client = "talekeepalive-0.0.1"
    urls = {
        "account_info": "https://the-tale.org/accounts/10745/api/show?api_version=1.0&api_client=" + api_client,
        "game_info": "https://the-tale.org/game/api/info?api_version=1.10&api_client=" + api_client,
        "card_list": "https://the-tale.org/game/cards/api/get-cards?api_version=2.0&api_client=" + api_client,
        "use_card": "https://the-tale.org/game/cards/api/use?api_version=2.0&api_client=" + api_client + "&card="
    }
    cards = {
        "resurrect": 160,
        "quest": 156
    }

    csrftoken = gen_csrftoken()
    session = requests.Session()
    cookies = dict({'sessionid': os.environ.get("SESSION_ID"), 'csrftoken': csrftoken})
    headers = dict({'referer': 'https://the-tale.org/', 'X-CSRFToken': csrftoken})
    session.cookies.update(cookies)

    game_info = session.get(urls.get("game_info"))

    try:
        if game_info.json()["data"]["account"]["hero"]["base"]["alive"]:
            print("[INFO] Hero is alive, nothing to do")
        else:
            print("[WARN] Hero is dead, going to resurrect")
            card_found = False
            card_list = session.get(urls.get("card_list"))
            user_cards = card_list.json()["data"]["cards"]
            for card in user_cards:
                if card.get("type") == cards.get("resurrect"):
                    card_found = True
                    print(f"Following card will be used to start questing: {card}")
                    break
            if card_found:
                return session.post(urls.get("use_card") + card.get("uid"),
                                    data={},
                                    headers=headers,
                                    cookies=cookies,
                                    allow_redirects=False).json()
            else:
                print("[WARN] There is no matching card to help the hero")
    except Exception as e:
        print(f"[ERROR] Something went wrong, exception: {e}")

    is_lazy = False
    try:
        quests = game_info.json()["data"]["account"]["hero"]["quests"]["quests"]  # quests 2 times, that's correct
        for quest in quests:
            if quest["line"][0]["type"] == "no-quest":
                is_lazy = True
    except Exception as e:
        print(f"[ERROR] Something went wrong, exception: {e}")

    if is_lazy:
        card_found = False
        print("[WARN] Hero does nothing, going to stop that!")
        card_list = session.get(urls.get("card_list"))
        for card in card_list.json()["data"]["cards"]:
            if card.get("type") == cards.get("quest"):
                card_found = True
                print(f"[INFO] Following card will be used to start questing: {card}")
                break

        if card_found:
            return session.post(urls.get("use_card") + card.get("uid"),
                                          data={},
                                          headers=headers,
                                          cookies=cookies,
                                          allow_redirects=False).json()
        else:
            print("[WARN] There is no matching card to help the hero")
    else:
        print("[INFO] Hero is questing, nothing to do")
