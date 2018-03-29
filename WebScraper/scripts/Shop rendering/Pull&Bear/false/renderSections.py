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
#path = "C:\\Users\\Dani\\Documents\\shops\\Pull&Bear_true\\false\\"

# Lista de secciones con sus URL's
urls = [("Abrigos", "https://www.pullandbear.com/es/mujer/ropa/abrigos-y-cazadoras-c1030009518.html"),
        ("Vestidos", "https://www.pullandbear.com/es/mujer/ropa/vestidos-c29016.html"),
        ("Monos", "https://www.pullandbear.com/es/mujer/ropa/petos-y-monos-c1073503.html"),
        ("Jerseis", "https://www.pullandbear.com/es/mujer/ropa/punto-c29017.html"),
        ("Sudaderas", "https://www.pullandbear.com/es/mujer/ropa/sudaderas-c29018.html"),
        ("Tops y brallettes", "https://www.pullandbear.com/es/mujer/ropa/tops-y-brallettes-c1030061504.html"),
        ("Blusas y camisas", "https://www.pullandbear.com/es/mujer/ropa/blusas-y-camisas-c29019.html"),
        ("Camisetas", "https://www.pullandbear.com/es/mujer/ropa/camisetas-c29020.html"),
        ("Jeans", "https://www.pullandbear.com/es/mujer/ropa/jeans-c29022.html"),
        ("Pantalones", "https://www.pullandbear.com/es/mujer/ropa/pantalones-c29021.html"),
        ("Shorts", "https://www.pullandbear.com/es/mujer/ropa/shorts-c29023.html"),
        ("Faldas", "https://www.pullandbear.com/es/mujer/ropa/faldas-c29024.html"),
        ("Basicos", "https://www.pullandbear.com/es/mujer/ropa/b%C3%A1sicos-c29025.html"),
        ("Bañadores", "https://www.pullandbear.com/es/mujer/ropa/beachwear-c1010091060.html"),
        ("Accesorios", "https://www.pullandbear.com/es/mujer/accesorios/ver-todo-c1030004059.html"),
        ("Zapatos", "https://www.pullandbear.com/es/mujer/zapatos/ver-todo-c670007.html")]
    
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
            EC.presence_of_element_located((By.CLASS_NAME, "grid_image"))
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
        products = dr.find_elements_by_css_selector("#grid_innerContainerRestyling > a")
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
