import sys, os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]

# Lista de secciones con sus URL's
urls = [("Abrigos", "http://www.zara.com/es/es/hombre/abrigos-y-trench-c799002.html"),
		("Cazadoras", "http://www.zara.com/es/es/hombre/cazadoras/ver-todo-c798001.html"),
		("Bombers", "http://www.zara.com/es/es/hombre/bombers-c813531.html"),
		("Blazers", "http://www.zara.com/es/es/hombre/blazers-c392502.html"),
		("Trajes", "http://www.zara.com/es/es/hombre/trajes-c358052.html"),
		("Pantalones", "http://www.zara.com/es/es/hombre/pantalones/ver-todo-c719514.html"),
		("Bermudas", "http://www.zara.com/es/es/hombre/bermudas-c381001.html"),
		("Jeans", "http://www.zara.com/es/es/hombre/jeans/ver-todo-c719519.html"),
		("Camisas", "http://www.zara.com/es/es/hombre/camisas/ver-todo-c719520.html"),
		("Camisetas", "http://www.zara.com/es/es/hombre/camisetas/ver-todo-c719523.html"),
		("Polos", "http://www.zara.com/es/es/hombre/polos-c715535.html"),
		("Sudaderas", "http://www.zara.com/es/es/hombre/sudaderas-c799012.html"),
		("Jerseys", "http://www.zara.com/es/es/hombre/jerseys-y-chaquetas/ver-todo-c719526.html"),
		("Zapatos", "http://www.zara.com/es/es/hombre/zapatos/ver-todo-%7C-desde-talla-39-c719027.html")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Se recorren la lista de secciones
for k,v in urls:
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 20 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "item"))
    )

    # Escribimos el HTML, teniendo en cuenta el cambio de codificacion.
    file = open(path + k + ".html", 'w')
    file.write(dr.page_source)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
