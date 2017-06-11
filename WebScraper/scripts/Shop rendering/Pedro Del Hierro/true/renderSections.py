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
urls = [("Bermudas", "http://pedrodelhierro.com/es/es/hombre/bermudas"),
        ("Cazadoras","http://pedrodelhierro.com/es/es/hombre/cazadoras"),
        ("Americanas","http://pedrodelhierro.com/es/es/hombre/americanas"),
        ("Punto", "http://pedrodelhierro.com/es/es/hombre/punto"),
        ("Camisas", "http://pedrodelhierro.com/es/es/hombre/camisas"),
        ("Camisetas", "http://pedrodelhierro.com/es/es/hombre/polos-y-camisetas"),
        ("Pantalones", "http://pedrodelhierro.com/es/es/hombre/pantalones"),
        ("Jeans", "http://pedrodelhierro.com/es/es/hombre/jeans"),
        ("Trajes", "http://pedrodelhierro.com/es/es/hombre/trajes"),
        ("Complementos", "http://pedrodelhierro.com/es/es/hombre/tailoring/complementos"),
        ("Corbatas", "http://pedrodelhierro.com/es/es/hombre/tailoring/corbatas"),
        ("Bañadores", "http://pedrodelhierro.com/es/es/hombre/bano"),
        ("Zapatos", "http://pedrodelhierro.com/es/es/hombre/zapatos")]
    
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

        # Si no se encuentra ningun producto lanzamos una excepcion
        if (len(products) == 0):
            raise Exception("Ningun elemento encontrado")
        
        for product in products:
            links.append(product.get_attribute("href"))

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
