urls = [("Chaquetas", "http://www.massimodutti.com/es/es/men/chaquetas-c680512.html"),
        ("Cazadoras", "http://www.massimodutti.com/es/es/men/cazadoras-de-piel-c680513.html")]

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Borrar el contenido de ejecuciones anteriores
for k,v in urls:
    file = open(k + ".txt", 'w')
    file.close()

for k,v in urls:
    file = open(k + ".txt", 'a')
    
    dr = webdriver.PhantomJS()
    dr.get(v)

    element = WebDriverWait(dr, 5).until(
        EC.presence_of_element_located((By.CLASS_NAME, "product-image  "))
    )

    product_links = [a.get_attribute('href') for a in dr.find_elements_by_xpath("//a[@class='product-image  ']")]
    for link in product_links: 
        file.write(link + "\n")

    file.close()

    # CERRAR PHANTOM
