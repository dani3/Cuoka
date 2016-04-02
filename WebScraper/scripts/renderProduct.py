from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

path_to_chromedriver = 'C:\\Users\\Dani\\Documents\\chromedriver'

path = sys.argv[1]
url = sys.argv[2]

dr = webdriver.Chrome(executable_path = path_to_chromedriver)

dr.get(url)

# Esperamos a que aparezca la imagen un maximo de 60 segundos.
element = WebDriverWait(dr, 60).until(
    EC.presence_of_element_located((By.CLASS_NAME, "imageLink"))
)

# Los escribimos en fichero.
file = open(path, 'w')
file.write(dr.page_source)
file.close()

dr.quit()
