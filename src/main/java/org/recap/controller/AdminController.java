package org.recap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/**
 * Created by dinakar on 24/11/20.
 */
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(BulkRequestController.class);

    @PostMapping(value = "/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            String content = new String(file.getBytes());
            logger.info("xml Data -->" + content);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            if (!file.isEmpty()) {
                return "file empty";
            } else {
                return "file not empty";
            }
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
        }
        return message;
    }
}