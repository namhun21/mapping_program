package com.mapping.mapping.controller;

import java.io.IOException;
import java.util.*;

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
                    @RequestParam("file2") MultipartFile file2, Model model) throws IOException { // 2

        List<ExcelData> dataList1 = getExcelList(file1); //엑셀파일을 읽어 원본을 리스트에 저장
        List<ExcelData> dataList2 = getExcelList(file2);
        List<List<ExcelData>> autoMappingResult = autoMapping(dataList1,dataList2); //autoMapping 시 데이터들 리스트로 받아옴

        //필요 데이터들을 view 단에 전송하기 위해
        model.addAttribute("data1", dataList1);
        model.addAttribute("data2", dataList2);
        model.addAttribute("result1",autoMappingResult.get(0));
        model.addAttribute("result2",autoMappingResult.get(1));

        return "excelList";

    }

    //전달받은 파일을 가져오기 위한 메소드
    private List<ExcelData> getExcelList(MultipartFile file) throws IOException{
        List<ExcelData> dataList = new ArrayList<>();

        //파일 확장자명 가져옴
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!extension.equals("xlsx") && !extension.equals("xls") ) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }
        Workbook workbook = null;

        //파일 스트림을 객체로 생성성
       if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(0);
        dataMapping(worksheet, dataList);

        return dataList;
    }

    //엑셀의 row를 돌며 데이터를 객체로 만들고 객체를 리스트에 저장
    private void dataMapping(Sheet worksheet, List<ExcelData> dataList) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

            Row row = worksheet.getRow(i);

            ExcelData data = new ExcelData();

            data.setNo((int) row.getCell(0).getNumericCellValue());
            data.setEnglish_field(row.getCell(1).getStringCellValue());
            data.setKorean_field(row.getCell(2).getStringCellValue());
            data.setLength((int) row.getCell(3).getNumericCellValue());

            dataList.add(data);
        }
    }

    //자동매핑시 중복 된 데이터를 영문필드명으로 정렬하고 나머지 데이터는 원래 순서대로 출력하기 위한 메소드
    private List<List<ExcelData>> autoMapping(List<ExcelData> dataList1,List<ExcelData> dataList2 ){
        List<List<ExcelData>> result = new ArrayList<>();
        List<ExcelData> autoMappingResult1 = new ArrayList<>();
        List<ExcelData> autoMappingResult2 = new ArrayList<>();
        List<ExcelData> dataList1Remain = new ArrayList<>();
        List<ExcelData> dataList2Remain = new ArrayList<>();

        for(int i =0; i< dataList1.size(); i++){
            for(int j =0; j< dataList2.size(); j++){
                if(dataList1.get(i).getEnglish_field().equals(dataList2.get(j).getEnglish_field())){
                    autoMappingResult1.add(dataList1.get(i));
                    autoMappingResult2.add(dataList2.get(j));
                    break;
                }
            }
        }

        Collections.sort(autoMappingResult1); //공통된 요소를 영문필드기준으로 정렬
        Collections.sort(autoMappingResult2);

        getRemainExcelData(dataList1, autoMappingResult1);
        getRemainExcelData(dataList2, autoMappingResult2);

        result.add(0,autoMappingResult1);
        result.add(1,autoMappingResult2);

        return result;
    }

    private void getRemainExcelData(List<ExcelData> dataList, List<ExcelData> autoMappingResult) {

        Map<String, Integer> map = new HashMap<>();
        for(int i =0; i< autoMappingResult.size(); i++){
            map.put(autoMappingResult.get(i).getEnglish_field(),1);
        }
        for(int i =0; i< dataList.size(); i++){
            if(map.getOrDefault(dataList.get(i).getEnglish_field() ,0) == 0){
                autoMappingResult.add(dataList.get(i));
            }
        }

    }

}
