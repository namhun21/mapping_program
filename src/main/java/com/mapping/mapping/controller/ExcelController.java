package com.mapping.mapping.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mapping.mapping.model.ExcelData;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ExcelController {

    @GetMapping("/excel")
    public String main() { // 1
        return "excel";
    }


    @PostMapping("/excel/read")
    public String readExcel(
                    @RequestParam("file1") MultipartFile file1,
                    @RequestParam("file2") MultipartFile file2, Model model)
            throws IOException { // 2

        List<ExcelData> dataList1 = new ArrayList<>();
        List<ExcelData> dataList2 = new ArrayList<>();
        String extension1 = FilenameUtils.getExtension(file1.getOriginalFilename()); // 3
        String extension2 = FilenameUtils.getExtension(file2.getOriginalFilename()); // 3

        if (!extension1.equals("xlsx") && !extension1.equals("xls") && !extension2.equals("xlsx") && !extension2.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook1 = null;
        Workbook workbook2 = null;
        if (extension1.equals("xlsx") && extension2.equals("xlsx") ) {
            workbook1 = new XSSFWorkbook(file1.getInputStream());
            workbook2 = new XSSFWorkbook(file2.getInputStream());
        } else if (extension1.equals("xls") && extension2.equals("xls")) {
            workbook1 = new HSSFWorkbook(file1.getInputStream());
            workbook2 = new HSSFWorkbook(file2.getInputStream());
        }

        Sheet worksheet1 = workbook1.getSheetAt(0);
        Sheet worksheet2 = workbook2.getSheetAt(0);

        dataMapping(worksheet1, dataList1);
        dataMapping(worksheet2, dataList2);
        model.addAttribute("data1", dataList1); // 5
        model.addAttribute("data2", dataList2);
        return "excelList";

    }

    private void dataMapping(Sheet worksheet1, List<ExcelData> dataList) {
        for (int i = 1; i < worksheet1.getPhysicalNumberOfRows(); i++) { // 4

            Row row = worksheet1.getRow(i);

            ExcelData data = new ExcelData();

            data.setNo((int) row.getCell(0).getNumericCellValue());
            data.setEnglish_field(row.getCell(1).getStringCellValue());
            data.setKorean_field(row.getCell(2).getStringCellValue());
            data.setLength((int) row.getCell(3).getNumericCellValue());

            dataList.add(data);
        }
    }

}
