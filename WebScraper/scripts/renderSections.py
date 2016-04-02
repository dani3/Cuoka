import os, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

path_to_chromedriver = 'C:\\..\\chromedriver'

path = "C:\\..."

urls = [("Seccion 1", "url"),
        ("Seccion 2", "url")]

dr = webdriver.Chrome(executable_path = path_to_chromedriver)

for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "link"))
    )

    # Escribimos el HTML en fichero.
    file = open(path + k + ".html", 'w')
    file.write(dr.page_source)
    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')

dr.quit()
