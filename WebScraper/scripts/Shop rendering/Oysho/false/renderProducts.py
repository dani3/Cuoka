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
#section = "Bodies"

# Path donde se encuentra el script -> "C:\\..\\false\\"
path = sys.argv[3]
#path = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Oysho_true\\false\\"
#path = "C:\\Users\\Dani\\Documents\\shops\\Oysho_false\\false\\"

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
            EC.presence_of_element_located((By.CLASS_NAME, "content"))
        )
        
    except:
        file_error.write("Imagen no encontrada en: " + link + "\n")
        continue

    time.sleep(1)

    try:
        # ****** N O M B R E ****** #
        name = dr.find_element_by_id("productName").text
        if (len(name) == 0):
            raise Exception("Nombre vacio")
            
        result.write("Nombre: " + name + "\n")
        
    except:
        result.write("Nombre: null\n")
        file_error.write("Nombre no encontrado en: " + link + "\n")
        continue 

    try:
        # ****** D E S C R I P C I O N ****** #
        description = "".join(dr.find_element_by_id("productDescription2").text.splitlines())[:255]
        result.write("Descripcion: " + description + "\n")
        
    except:
        result.write("Descripcion: null\n")

    try:
        # ****** P R E C I O   Y   D E S C U E N T O ****** #
        price = dr.find_element_by_class_name("productOldPrice").text.replace(",", ".").replace("€", "")
        if (len(price) == 0):
            raise Exception("Precio vacio")
        
        result.write("Precio: " + price + "\n")
        
        discount = dr.find_element_by_class_name("price").text.replace(",", ".").replace("€", "")      
        result.write("Descuento: " + discount + "\n")
        
    except:
        # Si salta la excepción significa que el precio no tiene descuento
        try:
            price = dr.find_element_by_class_name("product_price").text.replace(",", ".").replace("€", "")
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
        colors_palette = dr.find_element_by_class_name("color_palette")
        colors = colors_palette.find_elements_by_tag_name("span")
        numElem = len(colors)
        
    except:
        result.write("*********************************************************\n")
        result.write("  Color: null\n")
        result.write("  Icono: null\n")
        result.write("  Referencia: null\n")
        file_error.write("Colores no encontrados en: " + link + "\n")
        continue
    
    for i in range(1, numElem):
        try:
            colors_palette = dr.find_element_by_class_name("color_palette")
            colors = colors_palette.find_elements_by_tag_name("span")
              
            # Hacemos click en cada icono
            colors[i - 1].find_element_by_tag_name("img").click()
            time.sleep(1)

            WebDriverWait(dr, 10).until(
                EC.presence_of_element_located((By.CLASS_NAME, "swiper-slide"))
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
            colorName = colors_palette.text
            result.write("*********************************************************\n")
            result.write("  Color: " + colorName + "\n")
            
        except:
            result.write("*********************************************************\n")
            result.write("  Color: null\n")
            result.write("  Icono: null\n")
            result.write("  Referencia: null\n")
            file_error.write("Nombre de color no encontrado en: " + link + "\n")
            continue
        
        try:
            # ****** C O L O R   I C O N O ****** #
            colors_palette = dr.find_element_by_class_name("color_palette")
            colors = colors_palette.find_elements_by_tag_name("span")
            colorIcon = colors[i-1].find_element_by_tag_name("img").get_attribute("src")
            result.write("  Icono: " + colorIcon + "\n")
        
        except:
            result.write("  Icono: null\n")

        try:
            # ****** C O L O R   R E F E R E N C I A ****** #
            reference = dr.find_element_by_class_name("productRef").text.replace("/", "").replace("Ref.","").rstrip()
            result.write("  Referencia: " + reference + "\n")
            
        except:
            result.write("  Referencia: null\n")
            file_error.write("Referencia no encontrada en: " + link + "\n")
            continue
        
        # Sacamos las imagenes
        try:
            numPantallas = dr.find_elements_by_class_name("swiper-pagination-bullet")
            numImagenes = len(numPantallas) * 2
            images = dr.find_elements_by_class_name("swiper-slide")
            if (len(images) == 0):
                raise Exception("Imagenes no encontradas")

        except:
            file_error.write("Imagenes no encontradas en: " + link + "\n")
            continue

        # ****** I M A G E N E S ****** #
        for i in range(1, numImagenes + 1):
            try:                
                result.write("     Imagen: " + images[i].find_element_by_tag_name("img").get_attribute("src") + "\n")

            except:
                result.write("     Imagen: null" + "\n")

# Creamos un fichero vacio para indicar que ya hemos terminado.
open(path + section + '_done.dat', 'w')

file_error.close()
result.close()
file.close()

dr.quit()


