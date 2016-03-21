from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

path = "C:\\....."

url = "www.url.com"

urls = [("Seccion 1", "URL"),
        ("Seccion 2", "URL")]

dr = webdriver.PhantomJS()

# Eliminamos todos los ficheros antiguos
for file in os.listdir(path):
    if (".txt" in file)
        os.remove(file)

for k,v in urls:    
    dr.get(v)

    # Esperamos a que aparezcan los productos un maximo de 5 segundos.
    element = WebDriverWait(dr, 5).until(
        EC.presence_of_element_located((By.CLASS_NAME, "LINK"))
    )

    # Sacamos la lista de links de los productos.
    product_links = [a.get_attribute('href') for a in dr.find_elements_by_xpath("//a[@class='LINK']")]

    # Los escribimos en fichero.
    file = open(path + k + ".txt", 'w')
    for link in product_links: 
        file.write(link + "\n")

    file.close()

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + 'done.dat', 'w')
