import os, time, sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[2]

# Xpath del boton de 'Ver Todos'
allxpath = '//*[@id="cLnkViewAll"]'

# Lista de secciones con sus URL's
urls = [("Blazers", "https://www.blanco.com/category/24/blazers"),
        ("Chaquetas", "https://www.blanco.com/category/18/chaquetas"),
        ("Chalecos", "https://www.blanco.com/category/42/chalecos"),
        ("Camisas", "https://www.blanco.com/category/22/camisas"),
        ("Tops", "https://www.blanco.com/category/119/tops"),
        ("Camisetas", "https://www.blanco.com/category/21/t-shirts"),
        ("Sudaderas", "https://www.blanco.com/category/58/sudaderas"),
        ("Pantalones", "https://www.blanco.com/category/27/pantalones"),
        ("Faldas", "https://www.blanco.com/category/28/faldas"),
        ("Shorts", "https://www.blanco.com/category/177/shorts"),
        ("Vestidos", "https://www.blanco.com/category/203/vestidos"),
        ("Jerseys", "https://www.blanco.com/category/37/jerseys"),
        ("Cardigans", "https://www.blanco.com/category/38/cardigans"),
        ("Zapatos", "https://www.blanco.com/category/4/zapatos")]

# Driver de Chrome
dr = webdriver.Chrome(executable_path = path_to_chromedriver)

# Funcion que comrpueba que el boton de 'Ver Todos'
def check_exists_by_xpath(xpath):
    try:
        dr.find_element_by_xpath(xpath)
        
    except NoSuchElementException:
        return False
    
    return True

# Se recorren la lista de secciones
for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "cell-link"))
    )

    # Se hace scroll hasta abajo para encontrar el boton de 'Ver Todos'
    dr.execute_script("window.scrollTo(0, document.body.scrollHeight);")
    # Se espera un segundo
    time.sleep(1)

    # Comprobamos que esta el boton de 'Ver todos', si existe, se hace click y se espera
    if check_exists_by_xpath(allxpath):
        dr.find_element_by_xpath(allxpath).click()
        time.sleep(3)

    # Escribimos el HTML en fichero.
    file = open(path + k + ".html", 'w')
    file.write(dr.page_source)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

# Cerramos el navegador
dr.quit()
