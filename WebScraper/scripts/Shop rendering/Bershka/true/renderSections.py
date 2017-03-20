import os, time, sys
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
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Bershka_true\\true\\"
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_false\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Bershka_true\\true\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.bershka.com/es/hombre/ropa/abrigos-y-cazadoras-c1010193236.html"),
        ("Bombers", "https://www.bershka.com/es/hombre/ropa/bombers-c1010193237.html"),
        ("Jeans", "https://www.bershka.com/es/hombre/ropa/jeans-c1010193238.html"),
        ("Camisetas", "https://www.bershka.com/es/hombre/ropa/camisetas-c1010193239.html"),
        ("Camisas", "https://www.bershka.com/es/hombre/ropa/camisas-c1010193240.html"),
        ("Pantalones", "https://www.bershka.com/es/hombre/ropa/pantalones-c1010193241.html"),
        ("Bermudas", "https://www.bershka.com/es/hombre/ropa/bermudas-c1010193242.html"),
        ("Punto", "https://www.bershka.com/es/hombre/ropa/punto-c1010193243.html"),
        ("Sudaderas", "https://www.bershka.com/es/hombre/ropa/sudaderas-c1010193244.html"),
        ("Sport", "https://www.bershka.com/es/hombre/ropa/gymwear-c1010193247.html"),
        ("Relojes", "https://www.bershka.com/es/hombre/accesorios/relojes-c1010193177.html")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k, v in urls:
    file_error = open(path + k + "_links_error.txt", 'w')
    
    try:
        dr.get(v)
        # Esperamos a que aparezcan los productos un maximo de 10 segundos.
        element = WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "principalImg"))
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
        products = dr.find_elements_by_class_name("image")

        # Si no se encuentra ningun producto lanzamos una excepcion
        if (len(products) == 0):
            raise Exception("Ningun elemento encontrado")
        
        for product in products:
            links.append(product.find_element_by_xpath(".//a").get_attribute("href"))

        # Escribimos los links de cada producto en fichero.
        file = open(path + k + ".txt", 'w')

        for link in links:
            file.write(link + "\n")

        file.close()
        
    except Exception as e:
        # Escribimos la sección que ha fallado
        file_error.write(k + " (" + str(e) + ")")
        
    finally:
        file_error.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
