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
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Uterque\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.uterque.com/es/colecci%C3%B3n/abrigos/ver-todo-c1751032.html"),
        ("Pantalones", "https://www.uterque.com/es/colecci%C3%B3n/pantalones/ver-todo-c1753572.html"),
        ("Vestidos y faldas", "https://www.uterque.com/es/colecci%C3%B3n/vestidos-y-faldas-c1748462.html"),
        ("Monos", "https://www.uterque.com/es/colecci%C3%B3n/monos-c1748463.html"),
        ("Camisas y blusas", "https://www.uterque.com/es/colecci%C3%B3n/camisas-y-blusas-c1748458.html"),
        ("Camisetas", "https://www.uterque.com/es/colecci%C3%B3n/tops-y-camisetas-c1748464.html"),
        ("Jerseis", "https://www.uterque.com/es/colecci%C3%B3n/punto/ver-todo-c1748492.html"),
        ("Piel", "https://www.uterque.com/es/colecci%C3%B3n/piel-c1748460.html"),
        ("Chaquetas", "https://www.uterque.com/es/colecci%C3%B3n/chaquetas-c1748461.html"),
        ("Denim", "https://www.uterque.com/es/colecci%C3%B3n/denim-c1748466.html"),
        ("Bolsos", "https://www.uterque.com/es/bolsos/ver-todo-c1748474.html"),
        ("Bisuteria", "https://www.uterque.com/es/bisuter%C3%ADa/ver-todo-c1748479.html")
        ("Complementos", "https://www.uterque.com/es/complementos/ver-todo-c1748489.html"),
        ("Calzado", "https://www.uterque.com/es/calzado/ver-todo-c1751009.html")]
    
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
            EC.presence_of_element_located((By.CLASS_NAME, "image"))
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
        products = dr.find_elements_by_css_selector("div.listCategory > div")
        for product in products:
            links.append(product.find_element_by_css_selector("a").get_attribute("href"))

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
