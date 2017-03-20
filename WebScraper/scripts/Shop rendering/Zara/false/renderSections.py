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
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Zara_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Camisetas", "http://www.zara.com/es/es/mujer/camisetas/ver-todo-c719014.html"),
        ("Abrigos", "http://www.zara.com/es/es/mujer/abrigos/ver-todo-c719012.html"),
        ("Chaquetas","http://www.zara.com/es/es/mujer/chaquetas-c358002.html"),
        ("Blazers","http://www.zara.com/es/es/mujer/blazers-c797504.html"),
        ("Vestidos","http://www.zara.com/es/es/mujer/vestidos-c358003.html"),
        ("Monos","http://www.zara.com/es/es/mujer/monos-c399001.html"),
        ("Camisas","http://www.zara.com/es/es/mujer/camisas/ver-todo-c719021.html"),
        ("Body","http://www.zara.com/es/es/mujer/body-c602501.html"),
        ("Pantalones","http://www.zara.com/es/es/mujer/pantalones/ver-todo-c719022.html"),
        ("Jeans","http://www.zara.com/es/es/mujer/jeans/ver-todo-c719019.html"),
        ("Faldas","http://www.zara.com/es/es/mujer/faldas/ver-todo-c719016.html"),
        ("Punto","http://www.zara.com/es/es/mujer/punto/ver-todo-c719015.html"),
        ("Camisetas","http://www.zara.com/es/es/mujer/camisetas/ver-todo-c719014.html"),
        ("Sudaderas","http://www.zara.com/es/es/mujer/sudaderas-c364001.html"),
        ("Bolsos","https://www.zara.com/es/es/mujer/bolsos/ver-todo-c819022.html"),
        ("Accesorios","https://www.zara.com/es/es/mujer/accesorios/ver-todo-c719013.html"),
        ("Zapatos","http://www.zara.com/es/es/mujer/zapatos/ver-todo-c719531.html")]

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
            EC.presence_of_element_located((By.CLASS_NAME, "_product-grid-xmedia"))
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
        products = dr.find_elements_by_class_name("product")

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
