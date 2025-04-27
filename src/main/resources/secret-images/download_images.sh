#!/bin/bash

# Download 15 random images of different resolutions
for i in {11..25}; do
  # Generate a random width and height (between 400 and 1920)
  WIDTH=$((400 + RANDOM % 1521))
  HEIGHT=$((400 + RANDOM % 1521))

  # Download the image using Picsum Photos
  IMAGE_URL="https://picsum.photos/$WIDTH/$HEIGHT"
  IMAGE_NAME="image$i.jpg"

  echo "Downloading image $i: ${WIDTH}x${HEIGHT}..."
  curl -s -o "$IMAGE_NAME" "$IMAGE_URL"
done

echo "Done! Images are saved in the current directory."