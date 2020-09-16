package com.mapping.mapping.model;


//객체를 영문필드명 순으로 정렬하기 위해 Comparable 인터페이스 상속
public class ExcelData implements Comparable<ExcelData>{
    private Integer no;
    private String english_field;
    private String korean_field;
    private Integer length;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getEnglish_field() {
        return english_field;
    }

    public void setEnglish_field(String english_field) {
        this.english_field = english_field;
    }

    public String getKorean_field() {
        return korean_field;
    }

    public void setKorean_field(String korean_field) {
        this.korean_field = korean_field;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }


    @Override
    public int compareTo(ExcelData o) {
        return this.english_field.compareTo(o.getEnglish_field());
    }
}
