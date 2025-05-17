# ImageDownloader

A Java command-line tool to download images from a list of web gallery URLs. Images are organized into folders based on the URL path after the domain.

## Features
- Downloads all images linked from a list of gallery URLs.
- Organizes images into folders named after the URL path (slashes replaced with underscores).
- Retries failed downloads at the end (no delay between retries).
- Allows custom downloads folder as a command-line parameter.
- Ignores `input.txt` and `downloads/` in version control (see `.gitignore`).

## Usage

### Build

```
mvn package
```

This will generate a fat JAR with dependencies in `target/ImageDownloader-1.0.jar`.

### Run

```
java -jar target/ImageDownloader-1.0.jar <input_file> [downloads_folder]
```
- `<input_file>`: Path to a text file containing one gallery URL per line (e.g., `input.txt`).
- `[downloads_folder]` (optional): Directory to save downloaded images. Defaults to `downloads` if not specified.

Example:
```
java -jar target/ImageDownloader-1.0.jar input.txt my_downloads
```

## How it works
- For each URL in the input file:
  - Downloads the page and finds all image links.
  - Creates a folder named after the URL path (slashes replaced with underscores).
  - Downloads each image into the folder.
  - If a download fails (non-200 HTTP status), retries at the end (no delay between retries).

## Notes
- The `downloads/` folder and `input.txt` are excluded from git by default.
- If you download too quickly, some servers may block you. The tool retries failed downloads at the end.
- Requires Java 21+ and Maven.

## Example `input.txt`
```
https://example.com/gallery/album1/
https://example.com/gallery/album2/
```

## License
MIT
