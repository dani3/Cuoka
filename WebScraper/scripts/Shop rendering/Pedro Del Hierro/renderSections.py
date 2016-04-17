import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]

# Lista de secciones con sus URL's
urls = [("Sport", "http://pedrodelhierro.com/es/tienda/mujer/activewear/s?per_page=200&page=1&taxon=248"),
        ("Chaquetas", "http://pedrodelhierro.com/es/tienda/mujer/chaquetas/s?per_page=32&page=1&taxon=225"),
        ("Jerseys", "http://pedrodelhierro.com/es/tienda/mujer/punto/s?per_page=200&page=1&taxon=227"),
        ("Pantalones", "http://pedrodelhierro.com/es/tienda/mujer/pantalones/s?per_page=200&page=1&taxon=230"),
        ("Blusas", "http://pedrodelhierro.com/es/tienda/mujer/blusas/s?per_page=200&page=1&taxon=228"),
        ("Jeans", "http://www.pedrodelhierro.com/es/tienda/mujer/jeans/?extended=1"),
        ("Camisetas", "http://pedrodelhierro.com/es/tienda/mujer/camisetas/s?per_page=200&page=1&taxon=229"),
        ("Vestidos", "http://pedrodelhierro.com/es/tienda/mujer/vestidos-y-faldas/s?per_page=200&page=1&taxon=224"),
	    ("Zapatos", "http://www.pedrodelhierro.com/es/tienda/mujer/zapatos/?extended=1")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Se recorren la lista de secciones
for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "content_product"))
    )

    # Los escribimos en fichero.
    file = open(path + k + ".html", 'w')
    file.write(dr.page_source)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
