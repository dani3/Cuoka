import sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

path_to_chromedriver = 'C:\\Users\\Dani\\Documents\\chromedriver'

section = sys.argv[1]
path = sys.argv[2]
links = sys.argv[3]

listOfLinks = []
file = open(links, 'r')
for link in file:
    listOfLinks.append(link)

dr = webdriver.Chrome(executable_path = path_to_chromedriver)

cont = 0
for link in listOfLinks:
    dr.get(link)

    # Esperamos a que aparezca la imagen un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "image"))
    )

    time.sleep(1)

    htmlPath = path + section + "_" + str(cont) + ".html"

    # Escribimos el HTML, teniendo en cuenta el cambio de codificacion.
    file = open(htmlPath, 'w')
    html = dr.page_source.replace('€', '').encode(sys.stdout.encoding, errors='replace')
    html = html.decode('cp1252').encode('utf-8').decode('utf-8')
    file.write(html)
    file.close()

    cont = cont + 1

dr.quit()
