#!/bin/bash

# Author: Andrea Stagi (4ndreaSt4gi)
# Description: scale images from the drawable-xhdpi folder (ugly but fast)
# Requires: imagemagick
# License: MIT

cd ../res/drawable-xhdpi
for fname in *.png
do
    convert -resize 75% -quality 100% $fname ../drawable-hdpi/$fname
    convert -resize 50% -quality 100% $fname ../drawable-mdpi/$fname
    convert -resize 37.5% -quality 100% $fname ../drawable-ldpi/$fname
done
