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
#path_to_chromedriver = "D:\\Documentos\\1. Cuoka\\Scraping\\chromedriver"

# Nombre de la seccion
section = sys.argv[2]
#section = "Abrigos"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Dolores Promesas_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Dolores Promesas_true\\false\\"
#path = "D:\\Documentos\\1. Cuoka\\Scraping\\shops\\Dolores Promesas\\false\\"

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
            EC.presence_of_element_located((By.ID, "IMG_PRINC"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_class_name("content").find_element_by_tag_name("h2").text
        if (len(name) == 0):
            raise Exception("Nombre vacio")
            
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P C I O N ****** #
        result.write("Descripcion: " + "" + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O   Y   D E S C U E N T O ****** #
        price = dr.find_element_by_class_name("precio_anterior").text.replace(",", ".").replace("€", "")
        if (len(price) == 0):
            raise Exception("Precio vacio")
        
        result.write("Precio: " + price + "\n")
        
        discount = dr.find_element_by_class_name("precio_oferta").text.replace(",", ".").replace("€", "")      
        result.write("Descuento: " + discount + "\n")
        
    except:
        # Si salta la excepción significa que el precio no tiene descuento
        try:
            price = dr.find_element_by_class_name("precio").text.replace(",", ".").replace("€", "")
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
        # ****** C O L O R   N O M B R E ****** #
        colorName_largo = dr.find_element_by_css_selector("#p_detalle > div.content > h4").text.split()
        colorName = colorName_largo[1]
        result.write("*********************************************************\n")
        result.write("  Color: " + colorName + "\n")
        result.write("  Icono: null\n")
            
    except:
        result.write("*********************************************************\n")
        result.write("  Color: null\n")
        result.write("  Icono: null\n")
        result.write("  Referencia: null\n")
        file_error.write("Nombre de color no encontrado en: " + link + "\n")
        continue
            

    try:
        # ****** C O L O R   R E F E R E N C I A ****** #
        reference_largo = dr.find_element_by_css_selector("#p_detalle > div.content > h4").text.split()
        numElem = len(reference_largo)
        for i in range(0, numElem):
            if reference_largo[i].startswith("CÓDIGO"):
                reference = reference_largo[i+1]
                
        result.write("  Referencia: " + reference + "\n")
            
    except:
        result.write("  Referencia: null\n")
        file_error.write("Referencia no encontrada en: " + link + "\n")
        continue
        
    # Sacamos las imagenes
    try:
        images = dr.find_elements_by_css_selector("#gallery > ul > li")
        if (len(images) == 0):
            raise Exception("Imagenes no encontradas")

        dr.execute_script("arguments[0].scrollIntoView();", images[0])

    except:
        file_error.write("Imagenes no encontradas en: " + link + "\n")
        continue

    # ****** I M A G E N E S ****** #
    for image in images:
        try:
            image.find_element_by_css_selector("img").click()
            time.sleep(1)
            
            result.write("     Imagen: " + dr.find_element_by_id("Zoomer").get_attribute("href") + "\n")

        except:
            result.write("     Imagen: null" + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

file_error.close()
result.close()
file.close()

dr.quit()


