package com.poc.FileValidator.helper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ResponseMessage {
    public String message;
    public List<String> errorList;

    public ResponseMessage(String message) {
        this.message = message;
    }
    public ResponseMessage(String message, List<String> errorList) {
        this.message = message;
        this.errorList = errorList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
