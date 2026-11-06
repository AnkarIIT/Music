#!/bin/bash
# Download Inter font files for the project
# Run this script from the project root: ./download_fonts.sh

FONT_DIR="app/src/main/res/font"
mkdir -p "$FONT_DIR"

echo "Downloading Inter font family..."

# Download Inter font weights from Google Fonts
# Using the static font files (not variable font for better compatibility)
base_url="https://github.com/rsms/inter/raw/master/docs/font-files"

weights=("Regular" "Medium" "SemiBold" "Bold" "Black")

for weight in "${weights[@]}"; do
    filename="Inter-${weight}.ttf"
    url="${base_url}/${filename}"
    echo "Downloading ${filename}..."
    curl -L -o "${FONT_DIR}/inter_${weight,,}.ttf" "$url"
done

echo "Font files downloaded to ${FONT_DIR}"
ls -la "${FONT_DIR}"

# Note: You may need to manually download from https://github.com/rsms/inter/releases
# if the above URLs don't work. The Inter font is also available at:
# https://fonts.google.com/specimen/Inter