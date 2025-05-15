package com.foodDelivery.project.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Controller
//@CrossOrigin
public class ImageController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            // Create the full path to the image file
            Path imagePath = Paths.get(UPLOAD_DIR + filename);
            File file = imagePath.toFile();
            
            // Check if the file exists
            if (!file.exists()) {
                System.err.println("Image file not found: " + imagePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            // Determine the media type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                // Default to image/jpeg if content type cannot be determined
                String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "png":
                        contentType = "image/png";
                        break;
                    case "gif":
                        contentType = "image/gif";
                        break;
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    default:
                        contentType = "application/octet-stream";
                }
            }
            
            System.out.println("Serving image: " + filename + " with content type: " + contentType);
            
            // Create a resource from the file
            Resource resource = new FileSystemResource(file);
            
            // Return the resource with appropriate headers to prevent caching
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);
        } catch (IOException e) {
            System.err.println("Error serving image: " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
