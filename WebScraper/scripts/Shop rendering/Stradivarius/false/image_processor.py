from PIL import Image

'''
 Metodo que dada una image devuelve el codigo RGB del color predominante.
'''
def get_average_color(image_path): 
    height, width = Image.open(image_path).size
 
    image = Image.open(image_path).load()
    
    r, g, b = 0, 0, 0
    count = 0
    for s in range(0, height):
        for t in range(0, width):
            pixlr, pixlg, pixlb = image[s, t]
            
            r += pixlr
            g += pixlg
            b += pixlb
            
            count += 1
            
    return ((int)(r/count), (int)(g/count), (int)(b/count))
