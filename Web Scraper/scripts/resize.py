#!/usr/bin/python

import PIL
import os
import sys
from PIL import Image

path = sys.argv[1]
width_l = int(sys.argv[2])
height_l = int(sys.argv[3])
width_s = int(sys.argv[4])
height_s = int(sys.argv[5])

for fname in os.listdir(path):
    if 'Small' not in fname and 'Large' not in fname:
        img = Image.open(path + '/' + fname)
        img_s = img.resize((width_s,height_s), PIL.Image.NEAREST)
        img_s.save(path + '/' + fname.replace(".jpg", "_Small.jpg"))

        img_l = img.resize((width_l,height_l), PIL.Image.NEAREST)
        img_l.save(path + '/' + fname.replace(".jpg", "_Large.jpg"))

        os.remove(path + '/' + fname)
