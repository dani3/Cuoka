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
#path = "C:\\Users\\Dani\\Documents\\shops\\HyM_false\\false\\"
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_false\\false\\"

# Lista de secciones con sus URL's
urls = [("Camisas", "http://www2.hm.com/es_es/mujer/compra-por-producto/camisas-y-blusas.html"),
        ("Vestidos", "http://www2.hm.com/es_es/mujer/compra-por-producto/vestidos.html"),
        ("Camisetas", "http://www2.hm.com/es_es/mujer/compra-por-producto/tops.html"),
        ("Punto", "http://www2.hm.com/es_es/mujer/compra-por-producto/prendas-de-punto.html"),
        ("Jerseys", "http://www2.hm.com/es_es/mujer/compra-por-producto/cardigans-y-jerseis.html"),
        ("Chaquetas", "http://www2.hm.com/es_es/mujer/compra-por-producto/chaquetas-y-abrigos.html"),
        ("Americanas", "http://www2.hm.com/es_es/mujer/compra-por-producto/americanas-y-chalecos.html"),
        ("Pantalones", "http://www2.hm.com/es_es/mujer/compra-por-producto/pantalones.html"),
        ("Vaqueros", "http://www2.hm.com/es_es/mujer/compra-por-producto/vaqueros.html"),
        ("Faldas", "http://www2.hm.com/es_es/mujer/compra-por-producto/faldas.html"),
        ("Monos", "http://www2.hm.com/es_es/mujer/compra-por-producto/monos.html"),
        ("Shorts", "http://www2.hm.com/es_es/mujer/compra-por-producto/pantalones-cortos.html"),
        ("Sport", "http://www2.hm.com/es_es/mujer/compra-por-producto/h-m-sport.html"),
        ("Bikinis", "http://www2.hm.com/es_es/mujer/compra-por-producto/moda-de-bano.html"),
        ("Lenceria", "http://www2.hm.com/es_es/mujer/compra-por-producto/lenceria.html"),
        ("Complementos", "http://www2.hm.com/es_es/mujer/compra-por-producto/accesorios.html"),
        ("Zapatos", "http://www2.hm.com/es_es/mujer/compra-por-producto/calzado.html")]

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
            EC.presence_of_element_located((By.CLASS_NAME, "product-item-image"))
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
        products = dr.find_elements_by_class_name("product-item")

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
