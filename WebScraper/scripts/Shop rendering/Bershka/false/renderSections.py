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
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Bershka_true\\false\\"
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_false\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Pedro Del Hierro_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "http://www.bershka.com/es/mujer/ropa/abrigos-y-chaquetas-c1010193212.html"),
        ("Vestidos", "https://www.bershka.com/es/mujer/ropa/vestidos-c1010193213.html"),
        ("Monos", "https://www.bershka.com/es/mujer/ropa/monos-c1010193214.html"),
        ("Jeans", "https://www.bershka.com/es/mujer/ropa/jeans-c1010193215.html"),
        ("Pantalones", "https://www.bershka.com/es/mujer/ropa/pantalones-c1010193216.html"),
        ("Camisetas", "https://www.bershka.com/es/mujer/ropa/camisetas-c1010193217.html"),
        ("Bodies", "https://www.bershka.com/es/mujer/ropa/bodies-c1010193219.html"),
        ("Camisas", "https://www.bershka.com/es/mujer/ropa/camisas-c1010193221.html"),
        ("Sudaderas", "https://www.bershka.com/es/mujer/ropa/sudaderas-c1010193222.html"),
        ("Jerseis", "https://www.bershka.com/es/mujer/ropa/jerseis-c1010193532.html"),
        ("Faldas", "https://www.bershka.com/es/mujer/ropa/faldas-c1010193224.html"),
        ("Shorts", "https://www.bershka.com/es/mujer/ropa/shorts-c1010194517.html")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k,v in urls:
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
            time.sleep(2)
            newHeight = dr.execute_script("return document.body.scrollHeight")
            if newHeight == lastHeight:
                break
            lastHeight = newHeight

        links = []
        products = dr.find_elements_by_class_name("image")
        for product in products:
            links.append(product.find_element_by_xpath(".//a").get_attribute("href"))

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
