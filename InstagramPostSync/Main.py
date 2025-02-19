a = """
a-bronnikova	
alekskisel	
alex-non	
alienatedems	
angelopov	
bakaushin-na	
danyayert	
darya31	
descalier	
dmitryakramow	
endviyou	
ermpolina53	
eyandulov	
ily4koposov	
irashirokova	
katyha19	
koriukova-v	
l1kaa	
loginpro	
ltv1997	
maribasova	
neliubinpaw	
sakeeva01	
sergeykrasnov	
sidang	
spectring	
taawrk	
tedeshi	
versden	
yakomissarov
""".strip()
# a = """
# mnikishaev
# """
# 'a-bronnikova', 'alekskisel', 'alex-non', 'alienatedems', 'angelopov', 'bakaushin-na', 'danyayert', 'darya31', 'descalier', 'dmitryakramow', 'endviyou', 'ermpolina53', 'eyandulov', 'ily4koposov', 'irashirokova', 'katyha19', 'koriukova-v', 'l1kaa', 'loginpro', 'ltv1997', 'maribasova', 'neliubinpaw', 'sakeeva01', 'sergeykrasnov', 'sidang', 'spectring', 'taawrk', 'tedeshi', 'versden', 'yakomissarov'
a = """
alenaklyukh	
ann-cyberv	
antrubnikova	
aribelousova	
ari-garkusha	
arminedoc	
asiyaappasova	
a-skowron	
catmax	
cherkasova-a2	
chertigr	
crimewave	
daniil3435	
daryaisk	
dasha-burbela	
dolzhikovama	
eadozorina	
egogunskaia	
erlased	
evgfdr	
filipvasilev	
glagoleva98	
harumiya	
igorshapkin	
kravchen-ko-k	
kris2d2	
lemonlemonov	
lukianovasofi	
manannikova27	
mkorolk0	
mukelich	
muravievvn	
ochigvintseva	
omega25	
paninaa3	
p-malenkova	
radchenko-kd	
sidanastasia	
silvante	
soluninaks	
suvorovayul	
svirin-pav	
syaparkh	
temakakdela	
valentin-e-a	
vovamem2	
woalsg	
yuna-f	
zm-marina
""".strip()

c = 0
requests = []
d = []
for i in a.split("\n"):
    if i.strip():
        d.append(i.strip())
        c += 1
        requests.append(i.strip())
print(d)
print(len(d))
for login in requests:
    print(f"insert into operator_properties values ((select operator_id from operator_settings where login = '{login}'), 'IN_TEST_GROUP', 'true', 0);")

# c = 0
# requests = []
# for i in a.split("\n"):  # Split by newline character "\n"
#     if i.strip():  # Check if the line is not empty after removing leading/trailing whitespace
#         print(i)
#         c += 1
#         requests.append(i.strip())  # Store the stripped login in the list
#
# for login in requests:
#     print("""insert into operator_properties values ((select operator_id from operator_settings where login in ('{login}')), 'IN_TEST_GROUP', 'true', 0);""")
#
# # Join the logins with ', ' and format them with single quotes
# formatted_logins = "', '".join(requests)
#
# print(c)  # Print the count of non-empty lines
# print(f"'{formatted_logins}'")  # Print the formatted logins

# k = ['anna-morel', 'antolavr', 'arina-kn15', 'assolter', 'elp1k', 'etidyhard', 'flyagain', 'grebenkovav',
#      'irahramzova', 'irinaya91', 'karputovaira', 'katsy32', 'katyagiette', 'kaznachey', 'maaaaaaaaaash',
#      'master51261', 'medovajulia', 'molandrey', 'mraque', 'nikitasakh', 'o-iakushenko', 'olga27-m',
#      'onyanovald', 'osipqa', 'prikota247', 'pyakovleva02', 'rogovcev', 'sadertdinov-a', 'skorokhodo-p',
#      'stasia-poz', 'svikusha', 'vvetlu', 'yuliachuprina', 'yuliyabikmet', 'yulyasalk']
#
#


rr = False
if rr:
    import requests
    headers = {
        "Authorization": "OAuth y1_AQAD-qJSNTqJAAATgAAAAAAATLtKAABG_aM8yUJG-aHdQToFQ_CKyf_k4Q",  # Replace with your actual bearer token
        "Content-Type": "application/json"
    }
    e = ['mnikishaev']

    data = {
        "logins": d
    }
    url = "https://task-distributor.samsara.yandex-team.ru/api/v0/operator/force/sync"

    response = requests.post(url, headers=headers, json=data)

    # Check if the request was successful and print the response
    if response.status_code == 200:
        print("Request was successful")
        print("Response JSON:", response.json())
    else:
        print("Request failed with status code:", response.status_code)
        print("Response text:", response.text)

print(len(['a-bronnikova', 'alekskisel', 'alex-non', 'alienatedems', 'angelopov', 'bakaushin-na',
           'danyayert', 'darya31', 'descalier', 'dmitryakramow', 'endviyou', 'ermpolina53', 'eyandulov',
           'ily4koposov', 'irashirokova', 'katyha19', 'koriukova-v', 'l1kaa', 'loginpro', 'ltv1997',
           'maribasova', 'neliubinpaw', 'sakeeva01', 'sergeykrasnov', 'sidang', 'spectring', 'taawrk',
           'tedeshi', 'versden', 'yakomissarov',
           'alenaklyukh', 'ann-cyberv', 'antrubnikova', 'aribelousova', 'ari-garkusha', 'arminedoc',
           'asiyaappasova', 'a-skowron', 'catmax', 'cherkasova-a2', 'chertigr', 'crimewave',
           'daniil3435', 'daryaisk', 'dasha-burbela', 'dolzhikovama', 'eadozorina', 'egogunskaia',
           'erlased', 'evgfdr', 'filipvasilev', 'glagoleva98', 'harumiya', 'igorshapkin',
           'kravchen-ko-k', 'kris2d2', 'lemonlemonov', 'lukianovasofi', 'manannikova27', 'mkorolk0',
           'mukelich', 'muravievvn', 'ochigvintseva', 'omega25', 'paninaa3', 'p-malenkova',
           'radchenko-kd', 'sidanastasia', 'silvante', 'soluninaks', 'suvorovayul', 'svirin-pav',
           'syaparkh', 'temakakdela', 'valentin-e-a', 'vovamem2', 'woalsg', 'yuna-f', 'zm-marina']))