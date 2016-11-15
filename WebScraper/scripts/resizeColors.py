#!/usr/bin/python

import PIL
import os
import sys
from PIL import Image

path = sys.argv[1]
width = int(sys.argv[2])
height = int(sys.argv[3])

for fname in os.listdir(path):
    if 'ICON' in fname:
        try:
            img = Image.open(path + '/' + fname)
            img = img.resize((width,height), PIL.Image.NEAREST)
            img.save(path + '/' + fname, optimize = True, quality = 85)
            
        except Exception:
            pass
