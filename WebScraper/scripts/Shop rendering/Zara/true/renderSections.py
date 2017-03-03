import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
#path_to_chromedriver = sys.argv[1]
path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"

# Path donde se encuentra el script -> "C:\\..\\false\\"
#path = sys.argv[2]
path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Zara_true\\false\\"

urls = [("Abrigos", "http://www.zara.com/es/es/mujer/abrigos/ver-todo-c733882.html"),
        ("Monos", "http://www.zara.com/es/es/mujer/monos-c663016.html")]
		
chrome_options = Options()
chrome_options.add_argument("--lang=es")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorre la lista de secciones
for k,v in urls:
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 20 segundos.
    element = WebDriverWait(dr, 30).until(
        EC.presence_of_element_located((By.CLASS_NAME, "product-info"))
    )

    # Sacamos el tamano del html.
    lastHeight = dr.execute_script("return document.body.scrollHeight")

    # Hacemos scroll hasta abajo hasta que el tamano del html no cambie.
    while True:
        dr.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(3)
        newHeight = dr.execute_script("return document.body.scrollHeight")
        if newHeight == lastHeight:
            break
        lastHeight = newHeight

    links = []
    products = dr.find_elements_by_class_name("product-info")
    for product in products:
        links.append(product.find_element_by_xpath(".//a").get_attribute("href"))

    # Escribimos los links de cada producto en fichero.                 
    file = open(path + k + ".txt", 'w')

    for link in links:
        file.write(link + "\n")

    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()

