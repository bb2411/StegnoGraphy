#!/bin/bash

# Create a directory to store the images
mkdir -p images

# Download 10 random images
for i in $(seq -w 1 10); do
    curl -L "https://picsum.photos/200/200?random=$i" -o "images/image_$i.jpg"
    echo "Downloaded image_$i.jpg"
done

echo "All images downloaded successfully!"
