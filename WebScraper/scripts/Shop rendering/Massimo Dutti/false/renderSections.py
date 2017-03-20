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
#path = "C:\\Users\\Dani\\Documents\\shops\\Massimo Dutti_false\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.massimodutti.com/es/mujer/abrigos-c680658.html"),
        ("Chaquetas", "https://www.massimodutti.com/es/mujer/chaquetas/ver-todo-c911123.html"),
        ("Piel", "https://www.massimodutti.com/es/mujer/piel-c675010.html"),
        ("Americanas", "https://www.massimodutti.com/es/mujer/americanas/ver-todo-c911146.html"),
        ("Trajes", "https://www.massimodutti.com/es/mujer/trajes-c1383007.html"),
        ("Vestidos", "https://www.massimodutti.com/es/mujer/vestidos-y-monos-c1472056.html"),
        ("Faldas", "https://www.massimodutti.com/es/mujer/faldas-c675013.html"),
        ("Jerseis", "https://www.massimodutti.com/es/mujer/jers%C3%A9is-y-c%C3%A1rdigans/ver-todo-c911188.html"),
        ("Camisas", "https://www.massimodutti.com/es/mujer/camisas-y-blusas/ver-todo-c911173.html"),
        ("Camisetas", "https://www.massimodutti.com/es/mujer/camisetas/ver-todo-c911194.html"),
        ("Pantalones", "https://www.massimodutti.com/es/mujer/pantalones/ver-todo-c911198.html"),
        ("Jeans", "https://www.massimodutti.com/es/mujer/denim-c675012.html"),
        ("Bolsos", "https://www.massimodutti.com/es/mujer/bolsos-y-monederos/ver-todo-c914508.html"),
        ("Complementos", "https://www.massimodutti.com/es/mujer/accesorios/ver-todo-c1522503.html"),
        ("Zapatos", "https://www.massimodutti.com/es/mujer/zapatos/ver-todo-c1475029.html")]
    
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
        WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "product-view"))
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
        products = dr.find_elements_by_class_name("product-view-image-wrapper")
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
