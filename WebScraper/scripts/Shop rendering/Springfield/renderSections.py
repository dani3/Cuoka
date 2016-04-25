import os, sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]

# Lista de secciones con sus URL's
urls = [("Cazadoras", "http://myspringfield.com/es/es/man/cazadoras?r=1"),
        ("Jerseis", "http://myspringfield.com/es/es/man/jerseis?r=1"),
        ("Sudaderas", "http://myspringfield.com/es/es/man/sudaderas?r=1"),
        ("Camisas", "http://myspringfield.com/es/es/man/camisas?r=1"),
        ("Camisetas", "http://myspringfield.com/es/es/man/camisetas?r=1"),
        ("Polos", "http://myspringfield.com/es/es/man/polos?r=1"),
        ("Jeans", "http://myspringfield.com/es/es/man/jeans?r=1"),
        ("Pantalones", "http://myspringfield.com/es/es/man/pantalones?r=1"),
        ("Bermudas", "http://myspringfield.com/es/es/man/bermudas?r=1"),
        ("Zapatos", "http://myspringfield.com/es/es/man/zapatos?r=1")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Funcion que comrpueba que el boton de 'Ver Todos'
for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 20 segundos.
    element = WebDriverWait(dr, 20).until(
        EC.presence_of_element_located((By.CLASS_NAME, "c05__thumb-link"))
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
