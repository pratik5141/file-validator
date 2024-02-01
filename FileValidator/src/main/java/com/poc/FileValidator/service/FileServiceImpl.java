package com.poc.FileValidator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.poc.FileValidator.helper.ResponseMessage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private Environment environment;

    ObjectMapper mapper = new ObjectMapper();
    public List<String> errorList = new ArrayList<>();

    @Override
    public ResponseMessage validateCsvFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader fileReader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            List<CSVRecord> records = csvParser.getRecords();

            for (CSVRecord record : records) {
                validateEachRecord(record);
            }

            if (!errorList.isEmpty()) {
                moveFile(file.getOriginalFilename(),  "ERROR");
                return new ResponseMessage("error", errorList);
            }
            moveFile(file.getOriginalFilename(),"SUCCESS");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseMessage("All validations passed and file moved to Success folder");
    }

    @Override
    public boolean hasCsvFormat(MultipartFile file) {
        String type = "text/csv";
        if (!type.equals(file.getContentType()))
            return false;
        return true;
    }

    //All validations at once
    public void validateEachRecord(CSVRecord record) {
        String itemId = record.get("item_id");

        if (ObjectUtils.isEmpty(itemId) || itemId.length() < 0) {
            errorList.add("ItemId should not null for record:"+itemId);
        } else {
            try {
                int qty = Integer.parseInt(itemId);
                if (qty < 1) {
                    errorList.add("Item ID should be a positive integer for record:"+itemId);
                }
            } catch (NumberFormatException e) {
                errorList.add("Item ID should be a valid integer for record:"+itemId);
            }
        }


        String itemName = record.get("item_name");
        if (ObjectUtils.isEmpty(itemName) || itemName.length() < 0) {

            errorList.add( "itemName should not null for record:"+itemId);
        } else {
            if (!Character.isUpperCase(itemName.charAt(0))) {
                errorList.add("Item name should start with a capital letter for record:"+itemId);
            }
            //Allow some special char, restrict. null check
            for(Character c : itemName.toCharArray()){
                if ((Character.isLetter(c) && (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')) || c == '$' || c == '&' || c == ' ') {
                }else{
                    errorList.add("Item name not contain special character:"+c+" for record:"+itemId);
                }
            }
        }


        String itemType = record.get("item_type");
        if (ObjectUtils.isEmpty(itemType) || itemType.length() < 0) {
            errorList.add( "itemType should not null for record:"+itemId);
        } else {
            try {
                ItemType.valueOf(itemType.toUpperCase());
            } catch (IllegalArgumentException e) {
                errorList.add( "Item type should be one of laptop, phone, tablet, tv for record:"+itemId);
            }
        }


        String itemPrice = record.get("item_price");
        //upto 2 decimal 45.90
        // 45.9092 -> 45.90

        if (ObjectUtils.isEmpty(itemPrice) || itemPrice.length() < 0) {
            errorList.add("itemPrice should not null for record:"+itemId);
        }else{
            try {
               double price = Double.parseDouble(itemPrice);
               //String formattedValue = String.format("%.2f", price);
               if(price < 1){
                   errorList.add( "itemPrice should be a positive integer for record:"+itemId);
               }
            } catch (NumberFormatException e) {
                errorList.add( "itemPrice should be not null for record:"+itemId);;
            }
        }

        String itemQty = record.get("item_qty");
        if (ObjectUtils.isEmpty(itemQty) || itemQty.length() < 0) {
            errorList.add("itemQty should not null for record:"+itemId);
        }
        try {
            Integer qty = Integer.parseInt(itemQty);
            if(qty < 1){
                errorList.add( "itemQty should be positive for record:"+itemId);
            }
        } catch (NumberFormatException e) {
            errorList.add( "itemQty should be valid integer for record:"+itemId);
        }

        String manufacturedDate = record.get("item_manufactured_date");
        if (ObjectUtils.isEmpty(manufacturedDate) || manufacturedDate.length() < 0) {
            errorList.add( "manufacturedDate should not null for record:"+itemId);
        }else{
            if (!isValidDateFormat(manufacturedDate, "dd/MM/yyyy")) {
                errorList.add( "manufacturedDate should be in dd/MM/yyyy format for record:"+itemId);
            }
        }


        String expiryDate = record.get("item_expiry_date");
        if (ObjectUtils.isEmpty(expiryDate) || expiryDate.length() < 0) {
            errorList.add("expiryDate should not null for record:"+itemId);
        }else{
            if (!isValidDateFormat(expiryDate, "dd/MM/yyyy")) {
                errorList.add( "expiryDate should be in dd/MM/yyyy format for record:"+itemId);
            }
        }
    }

    public enum ItemType {
        LAPTOP, PHONE, TABLET, TV
    }

    public boolean isValidDateFormat(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    public void moveFile(String fileName, String action) throws IOException {
        String localPath = environment.getProperty("local-file-path");
        String sourceFilePath = localPath + fileName;
        String destinationFolder = localPath + action;
        Path sourcePath = Paths.get(sourceFilePath);
        Path destinationPath = Paths.get(destinationFolder, sourcePath.getFileName().toString());

        //java 8 - check alternative
        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
