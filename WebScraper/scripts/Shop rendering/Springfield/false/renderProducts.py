import sys, time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.common.exceptions import StaleElementReferenceException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

# Path al driver de Chrome -> "C:\\..\\chromedriver"
path_to_chromedriver = sys.argv[1]
#path_to_chromedriver = "C:\\Users\\lux_f\\Documents\\chromedriver"
#path_to_chromedriver = "C:\\Users\\Dani\\Documents\\chromedriver"

# Nombre de la seccion
section = sys.argv[2]
#section = "Camisas"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Springfield_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Springfield_true\\false\\"

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
        # Esperamos a que aparezca la imagen un maximo de 60 segundos.
        WebDriverWait(dr, 60).until(
            EC.presence_of_element_located((By.CLASS_NAME, "c01__media"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_css_selector('#product-content > div.c02__product-price > div.c02__product > h1').text
        if (len(name) == 0):
            raise Exception("Nombre vacio")

        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P T I O N ****** #
        description = "".join(dr.find_element_by_class_name("c02__product-description").text.splitlines()).replace("-", "")[:255]
        result.write("Descripcion: " + description + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O ****** #
        price = dr.find_element_by_css_selector("div.c02__pricing.large-only > span").text.replace(",", ".").replace("€", "")
        if (len(price) == 0):
            raise Exception("Precio vacio")

        result.write("Precio: " + price + "\n")
        
    except:
        result.write("Precio: null\n")
        file_error.write("Precio no encontrado en: " + link + "\n")
        continue

    try:
        # ****** D E S C U E N T O ****** #
        discount = dr.find_element_by_css_selector("div.c02__pricing.large-only > span.c02__sales-price").text.replace(",", ".").replace("€", "")

        result.write("Descuento: " + discount + "\n")
        
    except:
        result.write("Descuento: \n")

    result.write("Link: " + link + "\n")

    # Colores
    try:
        # ****** C O L O R E S ****** #
        colors = dr.find_elements_by_css_selector("div.c02__colors > ul > li")
        if (len(colors) == 0):
            raise Exception("Colores no encontrados")
        
    except:
        result.write("*********************************************************\n")
        result.write("  Color: null\n")
        result.write("  Icono: null\n")
        result.write("  Referencia: null\n")
        file_error.write("Colores no encontrados en: " + link + "\n")
        continue

    for i in range(len(colors)):
        if (len(colors) > 1):
            ok = False
            while not ok:
                try:
                    # Hacemos click en cada icono
                    colors[i].find_element_by_css_selector("a").click()
                    
                    time.sleep(1)

                    WebDriverWait(dr, 60).until(
                        EC.presence_of_element_located((By.CLASS_NAME, "c01__media"))
                    )

                    colors = dr.find_elements_by_css_selector("div.c02__colors > ul > li")

                    ok = True
                    
                except StaleElementReferenceException as e:
                    colors = dr.find_elements_by_css_selector("div.c02__colors > ul > li")
                    continue

        WebDriverWait(dr, 60).until(
            EC.presence_of_element_located((By.CLASS_NAME, "c01__media"))
        )

        try:
            # ****** C O L O R   N O M B R E ****** #
            colorName = colors[i].find_element_by_css_selector("a").get_attribute("title").upper().replace("/", "-")
            if (len(colorName) == 0):
                raise Exception("Nombre del color vacio")

            result.write("*********************************************************\n")
            result.write("  Color: " + colorName + "\n")
            
        except:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Color no encontrado en: " + link + "\n")
            continue

        try:
            # ****** C O L O R   I C O N O ****** #
            colorIcon = "http://myspringfield.com" + colors[i].find_element_by_css_selector("span").get_attribute("style").replace('background: url("', '').replace('");', '')
            
            result.write("  Icono: " + colorIcon + "\n")
            
        except:
            result.write("  Icono: null\n")

        try:
            # ****** C O L O R   R E F E R E N C I A ****** #
            reference = dr.find_element_by_css_selector("#product-content > div.c02__article-number").text.rstrip()
            reference = ''.join(ch for ch in reference if ch.isdigit())
            if (len(reference) == 0):
                raise Exception("Referencia vacia")

            result.write("  Referencia: " + reference + "\n")
            
        except:
            result.write("  Referencia: null\n")
            file_error.write("Referencia no encontrado en: " + link + "\n")
            continue    

        # Sacamos las imagenes
        try:
            images = dr.find_elements_by_css_selector("div.c01__slider div.c01__image > div.c01__media")
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

file_error.close()
result.close()
file.close()

dr.quit()


