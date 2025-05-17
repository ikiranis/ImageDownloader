# ImageDownloader

A Java CLI tool to download images from gallery URLs listed in a text file.

## Usage

1. **Build:**
   ```
   mvn package
   ```
2. **Run:**
   ```
   java -jar target/ImageDownloader-1.0.jar <input_file> [downloads_folder]
   ```
   - `<input_file>`: Text file with one gallery URL per line (e.g., `input.txt`).
   - `[downloads_folder]`: (Optional) Output directory. Defaults to `downloads`.

## Features
- Downloads all images linked from each gallery URL.
- Images are saved in folders named after the URL path (slashes replaced with underscores).
- Mimics browser headers for better compatibility.

## Notes
- Requires Java 21+ and Maven.
