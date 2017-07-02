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
        ("Cazadoras", "https://www.stradivarius.com/es/hombre/ropa/cazadoras-c1020084558.html"),
        ("Blazers", "https://www.stradivarius.com/es/hombre/ropa/blazers-c1020084560.html"),
        ("Parkas", "https://www.stradivarius.com/es/hombre/ropa/parkas-c1020084561.html"),
        ("Camisetas", "https://www.stradivarius.com/es/hombre/ropa/camisetas-c1020084563.html"),
        ("Polos", "https://www.stradivarius.com/es/hombre/ropa/polos-c1020084564.html"),
        ("Sudaderas", "https://www.stradivarius.com/es/hombre/ropa/sudaderas-c1020084565.html"),
        ("Pantalones", "https://www.stradivarius.com/es/hombre/ropa/pantalones-c1020084566.html"),
        ("Bermudas", "https://www.stradivarius.com/es/hombre/ropa/bermudas-c1020084567.html"),
        ("Punto", "https://www.stradivarius.com/es/hombre/ropa/punto-c1020084568.html"),
        ("Vaqueros", "https://www.stradivarius.com/es/hombre/ropa/jeans-c1020084569.html")]
    
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
