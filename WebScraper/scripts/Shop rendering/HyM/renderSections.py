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
urls = [("Jerseys", "http://www2.hm.com/es_es/mujer/compra-por-producto/cardigans-y-jerseis/_jcr_content/main/productlisting.display.html?product-type=ladies_cardigansjumpers&sort=stock&offset=0&page-size=1000"),
        ("Chaquetas", "http://www2.hm.com/es_es/mujer/compra-por-producto/chaquetas-y-abrigos/_jcr_content/main/productlisting.display.html?product-type=ladies_jacketscoats&sort=stock&offset=0&page-size=1000"),
        ("Americanas", "http://www2.hm.com/es_es/mujer/compra-por-producto/americanas-y-chalecos/_jcr_content/main/productlisting.display.html?product-type=ladies_blazerswaistcoats&sort=stock&offset=0&page-size=1000"),
        ("Camisas", "http://www2.hm.com/es_es/mujer/compra-por-producto/camisas-y-blusas/_jcr_content/main/productlisting.display.html?product-type=ladies_shirtsblouses&sort=stock&offset=0&page-size=1000"),
        ("Tops", "http://www2.hm.com/es_es/mujer/compra-por-producto/tops/_jcr_content/main/productlisting.display.html?product-type=ladies_tops&sort=stock&offset=0&page-size=1000"),
        ("Pantalones", "http://www2.hm.com/es_es/mujer/compra-por-producto/pantalones/_jcr_content/main/productlisting.display.html?product-type=ladies_trousers&sort=stock&offset=0&page-size=1000"),
        ("Faldas", "http://www2.hm.com/es_es/mujer/compra-por-producto/faldas/_jcr_content/main/productlisting.display.html?product-type=ladies_skirts&sort=stock&offset=0&page-size=1000"),
        ("Shorts", "http://www2.hm.com/es_es/mujer/compra-por-producto/pantalones-cortos/_jcr_content/main/productlisting.display.html?product-type=ladies_shorts&sort=stock&offset=0&page-size=1000"),
        ("Vestidos", "http://www2.hm.com/es_es/mujer/compra-por-producto/vestidos/_jcr_content/main/productlisting.display.html?product-type=ladies_dresses&sort=stock&offset=0&page-size=1000"),
        ("Sport", "http://www2.hm.com/es_es/mujer/compra-por-producto/h-m-sport/_jcr_content/main/productlisting.display.html?product-type=ladies_sport&sort=stock&offset=0&page-size=1000"),
        ("Monos", "http://www2.hm.com/es_es/mujer/compra-por-producto/monos/_jcr_content/main/productlisting_ef86.display.html?product-type=ladies_jumpsuits&sort=stock&offset=0&page-size=1000"),
        ("Zapatos", "http://www2.hm.com/es_es/mujer/compra-por-producto/calzado/_jcr_content/main/productlisting.display.html?product-type=ladies_shoes&sort=stock&offset=0&page-size=1000")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Se recorren la lista de secciones
for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "product-item-headline"))
    )

    # Los escribimos en fichero.
    file = open(path + k + ".html", 'w')
    file.write(dr.page_source)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
