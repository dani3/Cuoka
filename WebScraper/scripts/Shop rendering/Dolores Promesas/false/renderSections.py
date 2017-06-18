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
#path_to_chromedriver = "D:\\Documentos\\1. Cuoka\\Scraping\\chromedriver"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Uterque\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Massimo Dutti_false\\false\\"
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Dolores Promesas\\false\\"

# Lista de secciones con sus URL's
urls = [("Baño", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/BANO"),
        ("Bisuteria", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/BISUTERIA"),
        ("Bolsos", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/BOLSOS"),
        ("Camisas", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/CAMISAS"),
        ("Camisetas", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/CAMISETAS"),
        ("Chaquetas", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/CHAQUETAS"),
        ("Faldas", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/FALDAS%20"),
        ("Monos", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/MONOS"),
        ("Pantalones", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/PANTALONES"),
        ("Shorts", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/SHORTS"),
        ("Sudaderas", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/SUDADERAS"),
        ("Tops", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/TOP"),
        ("Vestidos", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/VESTIDOS"),
        ("Zapatos", "http://tienda.dolorespromesas.com/catalogo/seccion/MI%20COLECCION/ZAPATOS"),
        ("Falda", "http://tienda.dolorespromesas.com/catalogo/seccion/RESORT%20PV17/FALDAS%20"),
        ("Vestido", "http://tienda.dolorespromesas.com/catalogo/seccion/RESORT%20PV17/VESTIDOS"),
        ("Outlet", "http://tienda.dolorespromesas.com/catalogo/seccion/OUTLET")]
    
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
            EC.presence_of_element_located((By.CLASS_NAME, "p_img"))
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
        products = dr.find_elements_by_class_name("p_thumb")
        for product in products:
            links.append(product.find_element_by_css_selector("a").get_attribute("href"))

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