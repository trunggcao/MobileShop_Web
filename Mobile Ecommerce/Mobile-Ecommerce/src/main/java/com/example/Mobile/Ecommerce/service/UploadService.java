package com.example.Mobile.Ecommerce.service;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

    public String handleSaveUploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            System.out.println("File is empty!");
            return "";
        }

        try {
            // Đường dẫn thư mục đầy đủ
            String uploadDir = "upload/images/" + folder;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Tạo tên file duy nhất
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = System.currentTimeMillis() + fileExtension;

            // Lưu file
            File serverFile = new File(dir.getAbsolutePath() + File.separator + uniqueFilename);
            file.transferTo(serverFile);

            System.out.println("Đã lưu file tại: " + serverFile.getAbsolutePath());

            // Trả về đường dẫn tương đối để dùng trong HTML
            return "/images/" + folder + "/" + uniqueFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
