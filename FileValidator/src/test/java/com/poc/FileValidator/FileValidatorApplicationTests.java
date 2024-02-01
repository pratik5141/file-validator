package com.poc.FileValidator;

import com.poc.FileValidator.helper.ResponseMessage;
import com.poc.FileValidator.service.FileServiceImpl;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class FileValidatorApplicationTests {

    //@Test
    void contextLoads() {
    }

    //Test scenariaos
    // 1. Mock multipart file and validations success --> moved to success folder

    @Test
    void testValidateFileAndSuccess() throws IOException {
        FileServiceImpl fileServiceImpl = new FileServiceImpl();
        MockMultipartFile file = createMockFile("TestData.csv");
        ResponseMessage responseMessage = fileServiceImpl.validateCsvFile(file);
        assertEquals("All validations passed and file moved to Success folder", responseMessage.getMessage());
    }

    // 2. 1. Mock multipart file and validations error --> moved to error folder
    @Test
    void testValidateFileAndError() throws IOException {
        FileServiceImpl fileServiceImpl = new FileServiceImpl();
        MockMultipartFile file = createMockFile("TestData - Copy.csv");
        ResponseMessage responseMessage = fileServiceImpl.validateCsvFile(file);
        System.out.println(responseMessage.getMessage());
        assertNotEquals("All validations passed and file moved to Success folder", responseMessage.getMessage());
    }

   // @Test
    void testValidateEachRecord() {
        FileServiceImpl fileServiceImpl = new FileServiceImpl();
		CSVRecord validRecord = mock(CSVRecord.class);
		when(fileServiceImpl.errorList).thenReturn(new ArrayList<>());
		when(validRecord.get("item_id")).thenReturn("1");
		when(validRecord.get("item_name")).thenReturn("Item");
		when(validRecord.get("item_type")).thenReturn("laptop");
		when(validRecord.get("item_price")).thenReturn("25000.00");
		when(validRecord.get("item_qty")).thenReturn("5");
		when(validRecord.get("item_manufactured_date")).thenReturn("12/11/2022");
		when(validRecord.get("item_expiry_date")).thenReturn("12/11/2022");
        fileServiceImpl.validateEachRecord(validRecord);
        assertTrue(fileServiceImpl.errorList.isEmpty());
        //assertTrue(fileServiceImpl.validateEachRecord(validRecord));
    }




    private MockMultipartFile createMockFile(String name) throws IOException {
        Path path = Paths.get("src/test/resources", name);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", name, "text/csv", content);
    }
}
