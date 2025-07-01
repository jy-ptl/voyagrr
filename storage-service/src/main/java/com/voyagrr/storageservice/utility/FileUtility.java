package com.voyagrr.storageservice.utility;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtility {

    public String getMimeType(MultipartFile file) {
        Tika tika = new Tika();
        return tika.detect(file.getOriginalFilename());
    }

}
