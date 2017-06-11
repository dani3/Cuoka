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
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\MANGO_true\\false\\"
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Mango_false\\true\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Pedro Del Hierro_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Vestidos", "http://shop.mango.com/ES/m/mujer/prendas/vestidos?m=familia&v=32"),
        ("Abrigos", "http://shop.mango.com/ES/m/mujer/prendas/abrigos?m=familia&v=2"),
        ("Monos", "http://shop.mango.com/ES/m/mujer/prendas/monos?m=familia&v=34"),
        ("Camisas", "http://shop.mango.com/ES/m/mujer/prendas/camisas?m=familia&v=14"),
        ("Camisetas", "http://shop.mango.com/ES/m/mujer/prendas/camisetas?m=familia&v=18%2C318"),
        ("Jerseis", "http://shop.mango.com/ES/m/mujer/prendas/cardigans-y-jerseis?m=familia&v=55%2C355"),
        ("Sudaderas", "http://shop.mango.com/ES/m/mujer/prendas/sudaderas?m=familia&v=610%2C810"),
        ("Chaquetas", "http://shop.mango.com/ES/m/mujer/prendas/chaquetas?m=familia&v=4%2C304"),
        ("Pantalones", "http://shop.mango.com/ES/m/mujer/prendas/pantalones?m=familia&v=26%2C326"),
        ("Jeans", "http://shop.mango.com/ES/m/mujer/prendas/jeans?m=familia&v=28"),
        ("Shorts", "http://shop.mango.com/ES/m/mujer/prendas/shorts?m=familia&v=22%2C322"),
        ("Faldas", "http://shop.mango.com/ES/m/mujer/prendas/vestidos?m=familia&v=32"),
        ("Bikinis", "http://shop.mango.com/ES/m/mujer/prendas/bano?m=familia&v=36"),
        ("Sport","http://shop.mango.com/ES/m/mujer/prendas/deporte?m=familia&v=46")]
    
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Se recorren la lista de secciones
for k, v in urls:    
    try:
        dr.get(v)
        
        # Esperamos a que aparezcan los productos un maximo de 60 segundos.
        WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "product-list"))
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
        products = dr.find_elements_by_class_name("product-list-item")

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
