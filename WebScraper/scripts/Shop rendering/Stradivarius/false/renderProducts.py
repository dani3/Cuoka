import os, sys, time, shutil
import color_finder, image_downloader, image_processor, color_translator
from random import randint
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
#path_to_chromedriver = "D:\\Documentos\\1. Cuoka\\Scraping\\chromedriver"

# Nombre de la seccion
section = sys.argv[2]
#section = "Camisas"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Stradivarius_true\\false\\"
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Stradivarius\\false\\"

# Se recorre el fichero de links y se guardan en una lista
listOfLinks = []

file = open(path + "Seccion_" + section + ".txt", 'r')
for link in file:
    # Quitamos los saltos de linea
    listOfLinks.append(link.rstrip())
    
# Driver de Chrome
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Creamos fichero con los productos
result = open(path + "Productos_" + section + ".txt", 'w')
file_error = open(path + "Productos_Error_" + section + ".txt", 'w')

for link in listOfLinks:
    # Linea de guiones para separar cada producto
    result.write("-----------------------------------------------------------" + "\n")
    
    connected = False
    retries = 3
    while not connected & retries > 0:
        try:
            # Nos conectamos
            dr.get(link)
            
            connected = True

        except:
            retries -= 1
            time.sleep(2)
            continue

    if not connected:
        file_error.write("No se ha podido abrir el link: " + link + "\n")
        continue

    try:
        # Esperamos a que aparezca la imagen un maximo de 10 segundos.
        WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "image-container"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_class_name("product-name").text
        if (len(name) == 0):
            raise Exception("Nombre vacio")
        
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P T I O N ****** #
        description = "".join(dr.find_element_by_class_name("product-description").text.splitlines())[:255]
        result.write("Descripcion: " + description + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O   Y   D E S C U E N T O ****** #
        price = dr.find_element_by_class_name("one-old-price").text.replace(",", ".").replace("€", "")
        if (len(price) == 0):
            raise Exception("Precio vacio")
        
        result.write("Precio: " + price + "\n")
        
        discount = dr.find_element_by_class_name("current-price").text.replace(",", ".").replace("€", "")      
        result.write("Descuento: " + discount + "\n")
        
    except:
        # Si salta la excepción significa que el precio no tiene descuento
        try:
            price = dr.find_element_by_class_name("current-price").text.replace(",", ".").replace("€", "")
            if (len(price) == 0):
                raise Exception("Precio vacio")
            
            result.write("Precio: " + price + "\n")
            result.write("Descuento: \n")
            
        except:
            result.write("Precio: null\n")
            file_error.write("Precio no encontrado en: " + link + "\n")
            continue


    result.write("Link: " + link + "\n")

    # Colores
    try:
        # ****** C O L O R E S ****** #
        colors_long = dr.find_element_by_class_name("product-color-icons")
        colors = colors_long.find_elements_by_class_name("display-inline-block")
        if (len(colors) == 0):
            raise Exception("Colores no encontrados")
        
    except:
        result.write("*********************************************************\n")
        result.write("  Color: null\n")
        result.write("  Icono: null\n")
        result.write("  Referencia: null\n")
        file_error.write("Colores no encontrados en: " + link + "\n")
        continue

    for color in colors:
        try:
            if (len(colors) > 1):                
                # Hacemos click en cada icono
                color.find_element_by_class_name("item-colors").click()

                time.sleep(1)

                WebDriverWait(dr, 10).until(
                    EC.presence_of_element_located((By.CLASS_NAME, "image-container"))
                )
            
        except:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Color no encontrado en (click): " + link + "\n")
            continue

        try:
            # ****** C O L O R   N O M B R E ****** #
            color_icon = color.find_element_by_css_selector("div > img").get_attribute("src")

            # Se descarga el icono
            if not image_downloader.download_image(color_icon, path + section + "_aux_icon.jpg"):
                raise Exception("Error downloading image")

            # Se obtiene el codigo RGB
            r, g , b = image_processor.get_average_color(path + section + "_aux_icon.jpg")

            # Se obtiene el nombre del color de ese codigo
            color_name = color_finder.get_colour_name((r, g, b))

            # Se borra el icono
            os.remove(path + section + "_aux_icon.jpg")

            if color_name == None or len(color_name) == 0:
                raise Exception("Color not found")

            # Se traduce el color
            color_name = color_translator.translate_color(color_name)
            
            result.write("*********************************************************\n")
            result.write("  Color: " + color_name + "\n")
            
        except Exception as e:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Nombre de color no encontrado en: " + link + "\n")
            continue

        try:
            # ****** C O L O R   I C O N O ****** #
            result.write("  Icono: " + color_icon + "\n")
            
        except:
            result.write("  Icono: null\n")

        try:
            # ****** C O L O R   R E F E R E N C I A ****** #
            reference = dr.find_element_by_class_name("product-ref-details").text.replace("REF. ", "").replace("/", "-").replace("I2017 | Ver detalles", "").rstrip()

            reference += str(randint(0, 9999));
    
            if (len(reference) == 0):
                raise Exception("Referencia vacia")

            result.write("  Referencia: " + reference + "\n")
            
        except:
            result.write("  Referencia: null\n")
            file_error.write("Referencia no encontrada en: " + link + "\n")
            continue

        # Sacamos las imagenes
        try:
            images = dr.find_elements_by_css_selector("#productComponentMiddle > div > div")
            if (len(images) == 0):
                raise Exception("Imagenes no encontradas")

        except:
            file_error.write("Imagenes no encontradas en: " + link + "\n")
            continue

        # ****** I M A G E N E S ****** #
        for image in images:
            try:                
                result.write("     Imagen: " + image.find_element_by_css_selector("img").get_attribute("src") + "\n")

            except:
                result.write("     Imagen: null" + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

# Se borra el directorio de __pycache__
shutil.rmtree(path + "__pycache__")

file_error.close()
result.close()
file.close()

dr.quit()


