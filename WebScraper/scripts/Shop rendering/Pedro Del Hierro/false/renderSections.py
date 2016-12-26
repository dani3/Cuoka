import os, time, sys
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
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_false\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Pedro Del Hierro_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Blusas", "http://pedrodelhierro.com/es/es/mujer/blusas"),
        ("Abrigos","http://pedrodelhierro.com/es/es/mujer/abrigos"),
        ("Piel", "http://pedrodelhierro.com/es/es/mujer/piel"),
        ("Vestidos", "http://pedrodelhierro.com/es/es/mujer/vestidos"),
        ("Pantalones", "http://pedrodelhierro.com/es/es/mujer/pantalones"),
        ("Faldas", "http://pedrodelhierro.com/es/es/mujer/faldas"),
        ("Punto", "http://pedrodelhierro.com/es/es/mujer/punto"),
        ("Camisetas", "http://pedrodelhierro.com/es/es/mujer/camisetas"),
        ("Jeans", "http://pedrodelhierro.com/es/es/mujer/jeans"),
        ("Sport", "http://pedrodelhierro.com/es/es/mujer/activewear"),
        ("Zapatos", "http://pedrodelhierro.com/es/es/mujer/zapatos")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k,v in urls:
    file_error = open(path + k + "_links_error.txt", 'w')
    
    try:
        dr.get(v)

        # Esperamos a que aparezcan los productos un maximo de 60 segundos.
        element = WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "c05__thumb-link"))
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
        products = dr.find_elements_by_class_name("c05__thumb-link")
        for product in products:
            links.append(product.get_attribute("href"))

        # Escribimos los links de cada producto en fichero.
        file = open(path + k + ".txt", 'w')

        for link in links:
            file.write(link + "\n")

        file.close()
        
    except:
        #Escribimos el link de la seccion que falla
        file_error.write(v)
        
    finally:
        file_error.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()