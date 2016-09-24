import sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
#path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"
#path_to_chromedriver = "C:\\Users\\Dani\\Documents\\chromedriver"

# Nombre de la seccion
section = sys.argv[2]
#section = "Camisetas"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Blanco_true\\false\\"
#path = "C:\\Users\\Dani\\Documentos\\shops\\Blanco_true\\false\\"

listOfLinks = []

file = open(path + section + ".txt", 'r')
for link in file:
    #Quitamos los saltos de linea
    listOfLinks.append(link.rstrip())

chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Creamos fichero con los productos
result = open(path + section + "_products.txt", 'w')

for link in listOfLinks:
    # Linea de guiones para separar cada producto
    result.write("-----------------------------------------------------------" + "\n")

    # Nos conectamos
    dr.get(link)

    # Esperamos a que aparezca la imagen un maximo de 60 segundos.
    element = WebDriverWait(dr, 60).until(
        EC.presence_of_element_located((By.CLASS_NAME, "gallery-image"))
    )

    name = dr.find_element_by_xpath('//*[@id="product_addtocart_form"]/div[3]/div/div[2]/span').text.upper()
    
    description = dr.find_element_by_xpath('//*[@id="product-description"]').text.rstrip()[:255]

    price = dr.find_element_by_class_name("price").text.replace(",", ".").replace("€", "")

    result.write("Nombre: " + name + "\n")
    result.write("Descripcion: " + description + "\n")
    result.write("Precio: " + price + "\n")
    result.write("Link: " + link + "\n")

    # Colores
    colors = dr.find_elements_by_class_name("color")
    for color in colors:
        # Hacemos click en cada icono
        color.find_element_by_xpath(".//img").click()
        
        colorName = color.find_element_by_xpath(".//img").get_attribute("title").upper()
        colorIcon = color.find_element_by_xpath(".//img").get_attribute("src")
        reference = dr.find_element_by_id("reference").find_element_by_xpath(".//span").text
        
        result.write("  Color: " + colorName + "\n")
        result.write("  Icono: " + colorIcon + "\n")
        result.write("  Referencia: " + reference + "\n")

        # Sacamos las imagenes
        images = dr.find_elements_by_class_name("gallery-image")
        for img in images:
            result.write("     Imagen: " + img.get_attribute("src") + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

result.close()
file.close()
dr.quit()


    


