import sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

path_to_chromedriver = 'C:\\Users\\Dani\\Documents\\chromedriver'

section = "Blazers"
path = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\"
links = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\Blazers_LINKS.txt"

listOfLinks = []
file = open(links, 'r')
for link in file:
    listOfLinks.append(link)

dr = webdriver.Chrome(executable_path = path_to_chromedriver)

print(str(len(listOfLinks)))

cont = 0
for link in listOfLinks:
    print(str(cont) + ": " + link)
    
    try:
        dr.get(link)

        # Esperamos a que aparezca la imagen un maximo de 20 segundos.
        element = WebDriverWait(dr, 20).until(
            EC.presence_of_element_located((By.CLASS_NAME, "_img-zoom"))
        )

        time.sleep(1)

        htmlPath = path + section + "_" + str(cont) + ".html"

        # Escribimos el HTML.
        file = open(htmlPath, 'w')
        file.write(dr.page_source)
        file.close()

        cont = cont + 1
    except:
        print("Exception")

        htmlPath = path + section + "_" + str(cont) + ".html"

        # Escribimos el HTML.
        file = open(htmlPath, 'w')
        file.close()

        cont = cont + 1

dr.quit()
