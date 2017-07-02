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
urls = [("Camisas", "https://www.stradivarius.com/es/mujer/ropa/camisas/ver-todo-c1020047030.html"),
        ("Camisetas", "https://www.stradivarius.com/es/mujer/ropa/camisetas/ver-todo-c1020040560.html"),
        ("Tops", "https://www.stradivarius.com/es/mujer/ropa/tops-c1390554.html"),
        ("Vestidos", "https://www.stradivarius.com/es/mujer/ropa/vestidos-c1390555.html"),
        ("Monos", "https://www.stradivarius.com/es/mujer/new-collection/ropa/monos-c1390583.html"),
        ("Faldas", "https://www.stradivarius.com/es/mujer/ropa/faldas/ver-todo-c1020040596.html"),
        ("Shorts", "https://www.stradivarius.com/es/mujer/ropa/shorts-c1390552.html"),
        ("Pantalones", "https://www.stradivarius.com/es/mujer/ropa/pantalones/ver-todo-c1020040585.html"),
        ("Vaqueros", "https://www.stradivarius.com/es/mujer/ropa/jeans/ver-todo-c1020040584.html"),
        ("Sudaderas", "https://www.stradivarius.com/es/mujer/ropa/sudaderas-c1390553.html"),
        ("Jerseis", "https://www.stradivarius.com/es/mujer/ropa/jers%C3%A9is-y-c%C3%A1rdigan/ver-todo-c1020040574.html"),
        ("Chaquetas", "https://www.stradivarius.com/es/mujer/ropa/chaquetas/ver-todo-c1390544.html"),
        ("Abrigos", "https://www.stradivarius.com/es/mujer/ropa/abrigos/todos-c1020040566.html")]
    
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
