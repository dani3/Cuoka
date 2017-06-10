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
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Oysho\\false\\"

# Lista de secciones con sus URL's
urls = [("Sujetadores", "https://www.oysho.com/es/sujetadores/ver-todo-c1010155026.html"),
        ("Bodies","https://www.oysho.com/es/lencer%C3%ADa/bodies-c1469092.html"),
        ("Braguitas", "https://www.oysho.com/es/lencer%C3%ADa/braguitas/ver-todo-c1470541.html"),
        ("Pijamas", "https://www.oysho.com/es/pijamas/ver-todo-c1010166027.html"),
        ("Homewear", "https://www.oysho.com/es/homewear/ver-todo-c1010200547.html"),
        ("Baño", "https://www.oysho.com/es/ba%C3%B1o/ver-todo-c1010209050.html"),
        ("Vestidos", "https://www.oysho.com/es/beachwear/vestidos-c1469150.html"),
        ("Pantalones", "https://www.oysho.com/es/beachwear/pantalones/ver-todo-c1010216015.html"),
        ("Camisetas","https://www.oysho.com/es/beachwear/camisetas-c1469151.html"),
        ("Monos", "https://www.oysho.com/es/beachwear/monos-c1511519.html"),
        ("Sport", "https://www.oysho.com/es/deporte/ver-todo-c1010200548.html"),
        ("Zapatos", "https://www.oysho.com/es/calzado/ver-todo-c1197505.html")]
    
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
            EC.presence_of_element_located((By.CLASS_NAME, "grid_element"))
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
        products = dr.find_elements_by_class_name("grid_elem_container")
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
