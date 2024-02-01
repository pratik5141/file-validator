package com.poc.FileValidator.service;

import com.poc.FileValidator.helper.ResponseMessage;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public ResponseMessage validateCsvFile(MultipartFile file);
    public boolean hasCsvFormat(MultipartFile file);
}
