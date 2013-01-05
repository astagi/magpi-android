#!/bin/bash

# Author: Andrea Stagi (4ndreaSt4gi)
# Description: converts tabs in 4 spaces in every source file. Use it before commit!
# License: MIT

cd ..
for fname in `find . -type f -regex ".*\.\(js\|java\|html\|xml\|css\)"`
do
    expand -t 4 $fname > aux.txt
    cat aux.txt > $fname
done
rm aux.txt
