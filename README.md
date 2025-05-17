# ImageDownloader Java Utility

This utility reads a text file containing URLs, fetches each URL, and downloads all images (not thumbnails, only images that open when clicking links) into separate folders under a `downloads` directory.

## Usage
- Place your text file with URLs in the project root.
- Run the utility specifying the input file.
- Images from each URL will be saved in a separate folder under `downloads/`.

## Requirements
- Java 11 or higher
- Internet connection

## How it works
- Reads each URL from the input file
- Fetches the HTML content
- Finds all links to images that open in a new page
- Downloads those images into a dedicated folder for each URL
