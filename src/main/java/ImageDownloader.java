import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageDownloader {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar ImageDownloader <input_file>");
            return;
        }
        String inputFile = args[0];
        List<String> urls = readUrlsFromFile(inputFile);
        if (urls == null) return;
        Path downloadsDir = Paths.get("downloads");
        try {
            Files.createDirectories(downloadsDir);
        } catch (IOException e) {
            System.err.println("Failed to create downloads directory: " + e.getMessage());
            return;
        }
        for (String url : urls) {
            System.out.println("Processing: " + url);
            try {
                Document doc = Jsoup.connect(url).get();
                // Find links that open images (not thumbnails)
                Elements links = doc.select("a[href]");
                Set<String> imageLinks = new HashSet<>();
                for (Element link : links) {
                    String href = link.absUrl("href");
                    if (isImageUrl(href)) {
                        imageLinks.add(href);
                    }
                }
                // Create a folder for this URL using the path after the domain
                String folderName = "";
                try {
                    java.net.URI uri = java.net.URI.create(url);
                    String path = uri.getRawPath();
                    if (path != null && !path.isEmpty()) {
                        // Remove leading slash if present
                        if (path.startsWith("/")) path = path.substring(1);
                        folderName = path.replaceAll("/", "_");
                    } else {
                        folderName = "root";
                    }
                } catch (Exception e) {
                    folderName = url.replaceAll("[\\/:*?\"<>|]", "_");
                }
                Path urlDir = downloadsDir.resolve(folderName);
                Files.createDirectories(urlDir);
                List<String> retryList = new ArrayList<>();
                for (String imgUrl : imageLinks) {
                    boolean success = downloadImage(imgUrl, urlDir);
                    if (!success) {
                        retryList.add(imgUrl);
                    }
                }
                // Retry failed downloads at the end
                if (!retryList.isEmpty()) {
                    System.out.println("Retrying failed downloads...");
                    for (String imgUrl : retryList) {
                        downloadImage(imgUrl, urlDir);
                        try {
                            Thread.sleep(3000); // 3 second delay between retry downloads
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            System.err.println("Download interrupted during retry");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing " + url + ": " + e.getMessage());
            }
        }
    }

    private static List<String> readUrlsFromFile(String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("Failed to read input file: " + e.getMessage());
            return null;
        }
    }

    private static boolean isImageUrl(String url) {
        String lower = url.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    private static boolean downloadImage(String imgUrl, Path destDir) {
        try {
            java.net.URI uri = java.net.URI.create(imgUrl);
            java.net.URL url = java.net.URL.of(uri, null);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            int status = connection.getResponseCode();
            if (status != 200) {
                System.err.println("Failed to download image (HTTP status: " + status + "): " + imgUrl);
                connection.disconnect();
                return false;
            }
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            File destFile = destDir.resolve(fileName).toFile();
            try (InputStream in = connection.getInputStream(); OutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            connection.disconnect();
            System.out.println("Downloaded: " + imgUrl + " -> " + destFile.getPath());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to download image: " + imgUrl + " - " + e.getMessage());
            return false;
        }
    }
}
