import requests
import shutil

'''
 Metodo que descarga una imagen.
'''
def download_image(url, path):
    try:
        r = requests.get(url, stream = True, headers = {'User-agent': 'Mozilla/5.0'})

        if r.status_code == 200:
            with open(path, 'wb') as f:
                r.raw.decode_content = True
                shutil.copyfileobj(r.raw, f)

                return True

        return False

    except:
        return False

