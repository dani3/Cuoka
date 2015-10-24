#!/usr/bin/python

import PIL
import os
from PIL import Image

for root, dirs, files in os.walk('images/'):
	for folder in dirs:
		for fname in os.listdir('images/' + folder):
			img = Image.open('images/' + folder + '/' + fname)
			img = img.resize((350,500), PIL.Image.NEAREST)
			img.save('/var/www/html/images/' + folder + '/' + fname)
