﻿import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
#path_to_chromedriver = "D:\\Documentos\\1. Cuoka\\Scraping\\chromedriver"
#path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"
#path_to_chromedriver = "C:\\Users\\Dani\\Documents\\chromedriver"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Bershka_true\\false\\"
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Bershka_false\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Bershka_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.bershka.com/es/hombre/ropa/cazadoras-y-abrigos-c1010193236.html"),
        ("Jeans", "https://www.bershka.com/es/hombre/ropa/jeans-c1010193238.html"),
        ("Camisetas", "https://www.bershka.com/es/hombre/ropa/camisetas-c1010193239.html"),
        ("Sudaderas", "https://www.bershka.com/es/hombre/ropa/sudaderas-c1010193244.html"),
        ("Punto", "https://www.bershka.com/es/hombre/ropa/punto-c1010193243.html"),
        ("Camisas", "https://www.bershka.com/es/hombre/ropa/camisas-c1010193240.html"),
        ("Pantalones", "https://www.bershka.com/es/hombre/ropa/pantalones-c1010193241.html"),
        ("Bermudas", "https://www.bershka.com/es/hombre/ropa/bermudas-c1010193242.html"),
        ("Zapatos", "https://www.bershka.com/es/hombre/zapatos/novedades-c1010193201.html"),
        ("Relojes", "https://www.bershka.com/es/hombre/accesorios/relojes-c1010193177.html")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k, v in urls:    
    try:
        dr.get(v)
        # Se espera a que aparezcan los productos un maximo de 10 segundos.
        WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "image"))
        )

        # Se saca el tamano del html.
        lastHeight = dr.execute_script("return document.body.scrollHeight")

        # Se hace scroll hasta abajo hasta que el tamano del html no cambie.
        while True:
            dr.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(1)
            newHeight = dr.execute_script("return document.body.scrollHeight")
            if newHeight == lastHeight:
                break
            lastHeight = newHeight

        links = []
        products = dr.find_elements_by_class_name("image")

        # Si no se encuentra ningun producto se lanza una excepcion.
        if (len(products) == 0):
            raise Exception("Ningun elemento encontrado")
        
        for product in products:
            links.append(product.find_element_by_xpath(".//a").get_attribute("href"))

        # Se escriben los links de cada producto en fichero.
        file = open(path + "Seccion_" + k + ".txt", 'w')

        for link in links:
            file.write(link + "\n")

        file.close()
        
    except Exception as e:
        with open(path + "Seccion_Error_" + k + ".txt", 'w') as file_error:
            # Se escribe la sección que ha fallado
            file_error.write(k + " (" + str(e) + ")")

# Se crea un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Se cierra el navegador.
dr.quit()
