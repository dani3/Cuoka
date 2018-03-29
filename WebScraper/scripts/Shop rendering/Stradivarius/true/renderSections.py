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
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_false\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Stradivarius_true\\false\\"
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Stradivarius\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.stradivarius.com/es/hombre/ropa/abrigos-c1390578.html"),
        ("Chaquetas", "https://www.stradivarius.com/es/hombre/ropa/chaquetas/ver-todo-c1710526.html"),
        ("Piel", "https://www.stradivarius.com/es/hombre/ropa/piel-c1020081590.html"),
        ("Parkas", "https://www.stradivarius.com/es/hombre/ropa/parkas-c1020081592.html"),
        ("Bombers", "https://www.stradivarius.com/es/hombre/ropa/bombers-c1020081593.html"),
        ("Punto", "https://www.stradivarius.com/es/hombre/ropa/punto-c1020081601.html"),
        ("Camisas", "https://www.stradivarius.com/es/hombre/ropa/camisas-c1020081594.html"),
        ("Camisetas", "https://www.stradivarius.com/es/hombre/ropa/camisetas-c1020081596.html"),
        ("Sudaderas", "https://www.stradivarius.com/es/hombre/ropa/sudaderas-c1020081598.html"),
        ("Pantalones", "https://www.stradivarius.com/es/hombre/ropa/pantalones/ver-todo-c1718537.html"),
        ("Zapatos", "https://www.stradivarius.com/es/hombre/zapatos/todos-c1020081613.html"),
        ("Jeans", "https://www.stradivarius.com/es/hombre/ropa/jeans/ver-todo-c1020081576.html")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k, v in urls:
    
    try:
        dr.get(v)
        # Esperamos a que aparezcan los productos un maximo de 20 segundos.
        WebDriverWait(dr, 20).until(
            EC.presence_of_element_located((By.CLASS_NAME, "parent-div-product-grid"))
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
        products = dr.find_elements_by_css_selector("div.parent-div-product-grid > a")

        # Si no se encuentra ningun producto lanzamos una excepcion
        if (len(products) == 0):
            raise Exception("Ningun elemento encontrado") 

        for product in products:
            links.append(product.get_attribute("href"))

        # Escribimos los links de cada producto en fichero.
        file = open(path + "Seccion_" + k + ".txt", 'w')

        for link in links:
            if "javascript:void(0)" not in link:
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
