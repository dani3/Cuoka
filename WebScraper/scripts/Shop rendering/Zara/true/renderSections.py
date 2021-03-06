﻿import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
#path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"
#path_to_chromedriver = "C:\\Users\\Dani\\Documents\\chromedriver"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Zara_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Cazadoras", "http://www.zara.com/es/es/hombre/cazadoras-c586542.html"),
        ("Bombers", "http://www.zara.com/es/es/hombre/bombers-c813531.html"),
        ("Blazers","http://www.zara.com/es/es/hombre/blazers-c392502.html"),
        ("Trajes","https://www.zara.com/es/es/hombre/tailoring/ver-todo-c597508.html"),
        ("Pantalones","http://www.zara.com/es/es/hombre/pantalones/ver-todo-c719514.html"),
        ("Bermudas","http://www.zara.com/es/es/hombre/bermudas-c381001.html"),
        ("Jeans","http://www.zara.com/es/es/hombre/jeans-c368001.html"),
        ("Camisas","http://www.zara.com/es/es/hombre/camisas/ver-todo-c719520.html"),
        ("Camisetas","http://www.zara.com/es/es/hombre/camisetas/ver-todo-c719523.html"),
        ("Polos","http://www.zara.com/es/es/hombre/polos-c715535.html"),
        ("Sudaderas","http://www.zara.com/es/es/hombre/sudaderas-c799012.html"),
        ("Jerseys","http://www.zara.com/es/es/hombre/jerseys-y-chaquetas/ver-todo-c719526.html"),
        ("Zapatos","http://www.zara.com/es/es/hombre/zapatos/ver-todo-c719027.html"),
        ("Accesorios","https://www.zara.com/es/es/hombre/accesorios/ver-todo-c719529.html"),
        ("Bañadores","https://www.zara.com/es/es/hombre/ba%C3%B1adores-c822511.html"),
        ("Sport","http://www.zara.com/es/es/hombre/jogging-c726529.html")]

chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k, v in urls:    
    try:
        dr.get(v)
        # Esperamos a que aparezcan los productos un maximo de 10 segundos.
        WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "_product-grid-xmedia"))
        )

        # Sacamos el tamano del html.
        lastHeight = dr.execute_script("return document.body.scrollHeight")

        # Hacemos scroll hasta abajo hasta que el tamano del html no cambie.
        while True:
            dr.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(1)
            newHeight = dr.execute_script("return document.body.scrollHeight")
            if newHeight == lastHeight:
                break
            lastHeight = newHeight

        links = []
        products = dr.find_elements_by_class_name("product")

        # Si no se encuentra ningun producto lanzamos una excepcion
        if (len(products) == 0):
            raise Exception("Ningun elemento encontrado")
        
        for product in products:
            links.append(product.find_element_by_xpath(".//a").get_attribute("href"))

        # Escribimos los links de cada producto en fichero.
        file = open(path + "Seccion_" + k + ".txt", 'w')

        for link in links:
            file.write(link + "\n")

        file.close()

    except Exception as e:
        with open(path + "Seccion_Error_" + k + ".txt", 'w') as file_error:
            # Escribimos la sección que ha fallado
            file_error.write(k + " (" + str(e) + ")")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
