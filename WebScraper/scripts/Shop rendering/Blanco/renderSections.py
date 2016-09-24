import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
#path_to_chromedriver = sys.argv[1]
#path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"
path_to_chromedriver = "C:\\Users\\Dani\\Documents\\chromedriver"

# Path donde se encuentra el script -> "C:\\..\\false\\"
#path = sys.argv[2]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Blanco_true\\false\\"
path = "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Camisetas", "http://www.blanco.com/es/es_es/partes-de-arriba/camisetas.html"),
        ("Vestidos", "http://www.blanco.com/es/es_es/vestidos.html")]

chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k,v in urls:
    try:        
        dr.get(v)

        # Esperamos a que aparezcan los productos un maximo de 60 segundos.
        element = WebDriverWait(dr, 60).until(
            EC.presence_of_element_located((By.CLASS_NAME, "item"))
        )

        # Sacamos el tamano del html.
        lastHeight = dr.execute_script("return document.body.scrollHeight")

        # Hacemos scroll hasta abajo hasta que el tamano del html no cambie.
        while True:
            dr.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)
            newHeight = dr.execute_script("return document.body.scrollHeight")
            if newHeight == lastHeight:
                break
            lastHeight = newHeight

        links = []
        products = dr.find_elements_by_class_name("product-image")
        for product in products:
            links.append(product.get_attribute("href"))

        # Escribimos los links de cada producto en fichero.
        file = open(path + k + ".txt", 'w')

        for link in links:
            file.write(link + "\n")

        file.close()

    except:
        pass

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
