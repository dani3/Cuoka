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
#section = "Camisas"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\HyM_true\\false\\"

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
        element = WebDriverWait(dr, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "product-detail-main-image-container"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        dr.find_element_by_css_selector("div.cookie-notification button.close").click()
    
    except:
        pass

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_class_name('product-item-headline').text
        if (len(name) == 0):
            raise Exception("Nombre vacio")
        
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P C I O N ****** #
        description = "".join(dr.find_element_by_class_name("product-detail-description-text").text.splitlines())[:255]
        result.write("Descripcion: " + description + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:        
        # ****** P R E C I O ****** #
        price = dr.find_element_by_css_selector("section.product-detail-meta div.product-detail-options div.product-item-price span.price-value").text.replace(",", ".").replace("€", "")
        if (len(price) == 0):
            raise Exception("Precio vacio")

        result.write("Precio: " + price + "\n")
        
    except Exception as e:
        print(str(e))
        result.write("Precio: null\n")
        file_error.write("Precio no encontrado en: " + link + "\n")
        continue

    try:
        # ****** D E S C U E N T O ****** #
        discount = dr.find_element_by_class_name("price-value-original").text.replace(",", ".").replace("€", "")
        result.write("Descuento: " + discount + "\n")
        
    except:
        result.write("Descuento: \n")

    result.write("Link: " + link + "\n")

    # Colores
    try:
        # ****** C O L O R E S ****** #
        colors = dr.find_element_by_css_selector("div.product-colors > ul.inputlist").find_elements_by_xpath(".//li")
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
                time.sleep(1)
                
                # Hacemos click en cada icono
                color.find_element_by_css_selector("a").click()

                element = WebDriverWait(dr, 10).until(
                    EC.presence_of_element_located((By.CLASS_NAME, "product-detail-main-image-container"))
                )
            
        except:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Color no encontrado en: " + link + "\n")
            continue

        try:
            # ****** C O L O R   N O M B R E ****** #
            colorName = dr.find_element_by_css_selector("div.product-colors > div.product-input-label").text.upper().replace("/","-")
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
            colorIcon = color.find_element_by_css_selector("img").get_attribute("src")
            result.write("  Icono: " + colorIcon + "\n")
            
        except:
            result.write("  Icono: null\n")

        try:
            # ****** C O L O R   R E F E R E N C I A ****** #
            dr.find_element_by_css_selector("section > div.product-detail-details > div.details > div > ul > li:nth-child(2) > a").click()
            reference = dr.find_element_by_class_name("product-detail-article-code").text
            reference = ''.join(ch for ch in reference if ch.isdigit())
            
            if (len(reference) == 0):
                raise Exception("Referencia vacia")
    
            result.write("  Referencia: " + reference + "\n")
            
        except:
            result.write("  Referencia: null\n")
            file_error.write("Referencia no encontrado en: " + link + "\n")
            continue    

        # Sacamos las imagenes, tenemos que hacer click para que se cargue la imagen grande
        try:
            thumbnails = dr.find_elements_by_class_name("product-detail-thumbnail")
            if (len(thumbnails) == 0):
                raise Exception("Imagenes no encontradas")

        except:
            file_error.write("Imagenes no encontradas en: " + link + "\n")
            continue

        # ****** I M A G E N E S ****** #
        for thumbnail in thumbnails:
            try:
                thumbnail.click()
                
                # Esperamos a que se cargue bien la imagen
                WebDriverWait(dr, 10).until(
                    EC.presence_of_element_located((By.CLASS_NAME, "product-detail-main-image-container"))
                )
                
                image = dr.find_element_by_css_selector("div.product-detail-main-image-container > img")
                result.write("     Imagen: " + image.get_attribute("src") + "\n")

            except:
                result.write("     Imagen: null" + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

file_error.close()
result.close()
file.close()

dr.quit()


