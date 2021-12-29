package com.semobook.common;

import java.util.HashMap;
import java.util.Map;

public class SemoConstant {
    public static final String[] CATEGORY_TYPE = {"A","100","200","300","400","500","600","700","800","900"};

    public static final Map<String, String> CATEGORY_TYPE_MAP = new HashMap<String, String>() {
        {
            put("100", "인문/철학");
            put("200", "종교");
            put("300", "사회과학");
            put("400", "자연과학");
            put("500", "기술/생활");
            put("600", "예술/스포츠");
            put("700", "언어");
            put("800", "문학");
            put("900", "지리/역사");
            put("A", "종합");

        }
    };
    public static final String REDIS_KEY_BEST_SELLER = "RECOM_BEST_SELLER:";
    public static final String REDIS_KEY_STEADY_SELLER = "RECOM_STEADY_SELLER:";

    public static final int FIRST_PRIORITY_RATIO = 5;
    public static final int SECOND_PRIORITY_RATIO = 3;
    public static final int THRID_PRIORITY_RATIO = 2;
    public static final int FIRTH_PRIORITY_RATIO = 1;
    public static final int FIFTH_PRIORITY_RATIO = 1;

    public static final String VARTICAL_BAR = "|";
    public static final String BACKSLASH_VARTICAL_BAR = "\\|";
    public static final String COLON = ":";

    //
    public static final int CHECK_USER_REVIEW_CNT = 5;

    public static final String OPEN_API_KAKAO_BOOK = "https://dapi.kakao.com";
    public static final int TEMP_PW = 8; // 임시 비밀번호 길이
}
