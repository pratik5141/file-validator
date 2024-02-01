package com.poc.FileValidator.controller;

import com.poc.FileValidator.helper.ResponseMessage;
import com.poc.FileValidator.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/validate-csv-file")
    public ResponseEntity<ResponseMessage> validateCSV(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty() || !fileService.hasCsvFormat(file)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Requested file is not in correct format"));
        }
        ResponseMessage responseMessage = fileService.validateCsvFile(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseMessage);
    }
}
