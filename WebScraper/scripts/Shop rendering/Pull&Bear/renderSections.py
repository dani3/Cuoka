import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]

# Lista de secciones con sus URL's
urls = [("Abrigos", "http://www.pullandbear.com/es/es/mujer/abrigos-y-parkas-c359505.html"),
        ("Chaquetas", "http://www.pullandbear.com/es/es/mujer/cazadoras-y-americanas-c29015.html"),
        ("Kimonos", "http://www.pullandbear.com/es/es/mujer/kimonos-y-ponchos-c1389502.html"),
        ("Vestidos", "http://www.pullandbear.com/es/es/mujer/vestidos-c29016.html"),
        ("Petos", "http://www.pullandbear.com/es/es/mujer/petos-y-monos-c1073503.html"),
        ("Jerseys", "http://www.pullandbear.com/es/es/mujer/jerseys-y-chaquetas-c29017.html"),
        ("Sudaderas", "http://www.pullandbear.com/es/es/mujer/sudaderas-c29018.html"),
        ("Camisas", "http://www.pullandbear.com/es/es/mujer/blusas-y-camisas-c29019.html"),
        ("Camisetas", "http://www.pullandbear.com/es/es/mujer/camisetas-c29020.html"),
        ("Tops", "http://www.pullandbear.com/es/es/mujer/tops-c1010087522.html"),
        ("Jeans", "http://www.pullandbear.com/es/es/mujer/jeans-c29022.html"),
        ("Pantalones", "http://www.pullandbear.com/es/es/mujer/pantalones-c29021.html"),
        ("Leggings", "http://www.pullandbear.com/es/es/mujer/leggings-y-joggings-c1533593.html"),
        ("Faldas", "http://www.pullandbear.com/es/es/mujer/faldas-c29024.html"),
        ("Gymgear", "http://www.pullandbear.com/es/es/mujer/gymwear-c986003.html"),
        ("Shorts", "http://www.pullandbear.com/es/es/mujer/bermudas-y-shorts-c29023.html")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Se recorren la lista de secciones
for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "grid_itemContainer"))
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

    # Escribimos el HTML, teniendo en cuenta el cambio de codificacion.
    file = open(path + k + ".html", 'w')
    html = dr.page_source.encode(sys.stdout.encoding, errors='replace')
    html = html.decode('cp1252').encode('utf-8').decode('utf-8')
    file.write(html)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
