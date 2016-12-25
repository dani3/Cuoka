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
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\HyM_true\\true\\"

# Se recorre el fichero de links y se guardan en una lista
listOfLinks = []

file = open(path + section + ".txt", 'r')
for link in file:
    # Quitamos los saltos de linea
    listOfLinks.append(link.rstrip())
    
# Driver de Chrome
chrome_options = Options()
chrome_options.add_argument("--lang=es")
chrome_options.add_argument("--start-maximized")

dr = webdriver.Chrome(executable_path = path_to_chromedriver, chrome_options = chrome_options)

# Creamos fichero con los productos
result = open(path + section + "_products.txt", 'w')
file_error = open(path + section + "_error.txt", 'w')

for link in listOfLinks:
    # Linea de guiones para separar cada producto
    result.write("-----------------------------------------------------------" + "\n")
    
    try:
        # Nos conectamos
        dr.get(link)
        
    except:
        file_error.write("No se ha podido abrir el link: " + link + "\n")
        continue

    try:
        # Esperamos a que aparezca la imagen un maximo de 60 segundos.
        element = WebDriverWait(dr, 60).until(
            EC.presence_of_element_located((By.CLASS_NAME, "product-detail-main-image-container"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_class_name('product-item-headline').text
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P T I O N ****** #
        description = dr.find_element_by_class_name("product-detail-description-text").text.rstrip()[:255]
        result.write("Descripcion: " + description + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O ****** #
        price = dr.find_element_by_class_name("price-value").text.replace(",", ".").replace("€", "")
        result.write("Precio: " + price + "\n")
        
    except:
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
        colors = dr.find_element_by_class_name("inputlist").find_elements_by_xpath(".//li")
        
    except:
        file_error.write("Colores no encontrados en: " + link + "\n")
        continue

    for color in colors:
        try:
            # Hacemos click en cada icono
            color.find_element_by_class_name("detailbox-pattern").click()

            element = WebDriverWait(dr, 60).until(
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
            colorName = dr.find_element_by_class_name("product-input-label").find_element_by_xpath(".//span").text.upper().replace("/","-")
            result.write("*********************************************************\n")
            result.write("  Color: " + colorName + "\n")
            
        except:            
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Color no encontrado en: " + link + "\n")
            continue

        try:
            # ****** C O L O R   I C O N O ****** #
            colorIcon = color.find_element_by_xpath(".//img").get_attribute("src")
            result.write("  Icono: " + colorIcon + "\n")
            
        except:
            result.write("  Icono: null\n")

        try:
            # ****** C O L O R   R E F E R E N C I A ****** #
            dr.find_element_by_css_selector("section > div.product-detail-details > div.parbase.details > div > ul > li:nth-child(2) > a").click()
            reference = dr.find_element_by_class_name("product-detail-article-code").text
            result.write("  Referencia: " + reference + "\n")
            
        except:
            result.write("  Referencia: null\n")
            file_error.write("Referencia no encontrado en: " + link + "\n")
            continue    

        # Sacamos las imagenes, tenemos que hacer click para que se cargue la imagen grande
        try:
            thumbnails = dr.find_elements_by_class_name("product-detail-thumbnail")

        except:
            file_error.write("Imagenes no encontradas en: " + link + "\n")
            continue

        # ****** I M G A E N E S ****** #
        for thumbnail in thumbnails:
            try:
                thumbnail.click()
                
                #Esperamos a que se cargue bien la imagen
                WebDriverWait(dr, 60).until(
                    EC.presence_of_element_located((By.CLASS_NAME, "product-detail-main-image-container"))
                )
                
                image = dr.find_element_by_class_name("product-detail-main-image-container")
                result.write("     Imagen: " + image.find_element_by_xpath(".//img").get_attribute("src") + "\n")

            except:
                result.write("     Imagen: null")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

file_error.close()
result.close()
file.close()

dr.quit()


