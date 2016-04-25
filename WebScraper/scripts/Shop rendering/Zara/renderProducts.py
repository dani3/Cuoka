import sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Nombre de la seccion -> "Camisas"
section = sys.argv[2]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
# Path donde se encuentra el fichero de links -> "C:\\..\\false\\Camisas_LINKS.txt"
links = sys.argv[4]

# Se recorre el fichero de links y se guardan en una lista
listOfLinks = []
file = open(links, 'r')
for link in file:
    listOfLinks.append(link)

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

cont = 0
# Se recorre la lista de links
for link in listOfLinks:
    try:
        dr.get(link)

        # Esperamos a que aparezca la imagen un maximo de 20 segundos.
        element = WebDriverWait(dr, 20).until(
            EC.presence_of_element_located((By.CLASS_NAME, "_img-zoom"))
        )

        # Se espera un segundo
        time.sleep(1)

        # Se forma el nombre del fichero .html con un contador
        htmlPath = path + section + "_" + str(cont) + ".html"

        # Escribimos el HTML.
        file = open(htmlPath, 'w')
        file.write(dr.page_source)
        file.close()

        cont = cont + 1
    except:
        #Aunque se cree una excepcion, creamos el fichero vacio.
        htmlPath = path + section + "_" + str(cont) + ".html"

        # Escribimos el HTML.
        file = open(htmlPath, 'w')
        file.close()

        cont = cont + 1

# Cerramos el navegador
dr.quit()