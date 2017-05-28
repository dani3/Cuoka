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
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Bershka_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Descubre_Polar Company_true\\true\\"

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
    while not connected and retries > 0:
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
            EC.presence_of_element_located((By.ID, "slider"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_css_selector("h2.product_title").text
        if (len(name) == 0):
            raise Exception("Nombre vacio")
        
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P C I O N ****** #
        description = dr.find_element_by_css_selector('div.post-content > p').text[:255]
        result.write("Descripcion: " + description + "\n")

    except:
        try:
            description = dr.find_element_by_css_selector('div.tab-content ').text[:255]
            result.write("Descripcion: " + description + "\n")
            
        except:
            result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O ****** #
        price = dr.find_element_by_css_selector("p.price span.woocommerce-Price-amount").text.replace(",", ".").replace("€", "")

        if (len(price) == 0):
            raise Exception("Precio vacio")
        
        result.write("Precio: " + price + "\n")
        
    except:
        result.write("Precio: null\n")
        file_error.write("Precio no encontrado en: " + link + "\n")
        continue

    try:
        # ****** D E S C U E N T O ****** #
        discount = prices_list[1].replace("€", "")
        result.write("Descuento: " + discount + "\n")
        
    except:
        result.write("Descuento: \n")

    result.write("Link: " + link + "\n")

    # Colores
    try:
        # ****** C O L O R   N O M B R E ****** #
        colorName = dr.find_element_by_css_selector("div.product-accordion__content-container > div > div > ul > li").text.upper().split(":")[1].rstrip()
        if (len(colorName) == 0):
            raise Exception("Nombre del color vacio")
            
        result.write("*********************************************************\n")
        result.write("  Color: " + colorName + "\n")
            
    except:
        try:
            colorName = dr.find_element_by_css_selector("div.tab-content ul.ul1 li.li1").text.upper().split(": ")[1].rstrip()
            if (len(colorName) == 0):
                raise Exception("Nombre del color vacio")
                
            result.write("*********************************************************\n")
            result.write("  Color: " + colorName + "\n")

        except:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Nombre de color no encontrado en: " + link + "\n")
            continue

    # ****** C O L O R   I C O N O ****** #
    result.write("  Icono: null\n")

    # ****** C O L O R   R E F E R E N C I A ****** #
    result.write("  Referencia: NA\n")

    # Se saca la primera imagen.
    try:                
        result.write("     Imagen: " + dr.find_element_by_css_selector("ul.slides li img").get_attribute("src") + "\n")

    except:
        result.write("     Imagen: null" + "\n")

    # Sacamos las imagenes.
    try:
        images = dr.find_elements_by_css_selector("ul.slides li")

    except:
        file_error.write("Imagenes no encontradas en: " + link + "\n")
        continue

    # ****** I M A G E N E S ****** #
    for i in range(1, (int)(len(images) / 2)):
        try:                
            result.write("     Imagen: " + images[i].find_element_by_css_selector("img").get_attribute("src") + "\n")

        except:
            result.write("     Imagen: null" + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

file_error.close()
result.close()
file.close()

dr.quit()


