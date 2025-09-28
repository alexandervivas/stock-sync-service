package com.upwork.vendor.b.application.ports;

import com.upwork.vendor.b.domain.ProductRow;

import java.nio.file.Path;
import java.util.List;

public interface CsvWriterPort {

    void write(Path path, List<ProductRow> productRows);

}
