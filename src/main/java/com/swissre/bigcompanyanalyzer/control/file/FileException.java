package com.swissre.bigcompanyanalyzer.control.file;

import com.swissre.bigcompanyanalyzer.control.ReportException;

public class FileException extends ReportException {
    FileException(String error) {
        super(error);
    }
}
