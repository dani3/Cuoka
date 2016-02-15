#!/usr/bin/python

import PIL
import os
import sys
from PIL import Image
from PIL import ImageFile

path = sys.argv[1]
ratio = float(sys.argv[2])
height_l = int(sys.argv[3])
height_s = int(sys.argv[4])

width_l = int(height_l / ratio)
width_s = int(height_s / ratio)

ImageFile.LOAD_TRUNCATED_FILES = True

for fname in os.listdir(path):
    if 'Small' not in fname and 'ICON' not in fname and 'Large' not in fname:
        try:
            img = Image.open(path + '/' + fname)
            img_s = img.resize((width_s,height_s), PIL.Image.NEAREST)
            img_s.save(path + '/' + fname.replace(".jpg", "_Small.jpg"))

            img_l = img.resize((width_l,height_l), PIL.Image.NEAREST)
            img_l.save(path + '/' + fname.replace(".jpg", "_Large.jpg"))

            os.remove(path + '/' + fname)
        except Exception:
            pass
