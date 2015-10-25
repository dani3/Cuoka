#!/usr/bin/python

import PIL
import os
import sys
from PIL import Image

shop = sys.argv[1]

for fname in os.listdir('images/' + shop):
	img = Image.open('images/' + shop + '/' + fname)
	img = img.resize((350,500), PIL.Image.NEAREST)
	img.save('/var/www/html/images/' + shop + '/' + fname)

		
