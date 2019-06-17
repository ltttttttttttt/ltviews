package com.lt.select_country;

import java.io.Serializable;

/**
 * 创    建:  lt  2019/5/25--13:42    lt.dygzs@qq.com
 * 作    用:
 * 注意事项:
 */
public class CountryBean implements Serializable {

    /**
     * en : Angola
     * zh : 安哥拉
     * locale : AO
     * code : 244
     */

    private String en;
    private String zh;
    private String locale;
    private String code;
    private String shoupinyin;

    public CountryBean() {
    }

    public CountryBean(String en, String zh, String locale, String code, String shoupinyin) {
        this.en = en;
        this.zh = zh;
        this.locale = locale;
        this.code = code;
        this.shoupinyin = shoupinyin;
    }

    public CountryBean(String zh) {
        this.zh = zh;
    }

    public String getShoupinyin() {
        return shoupinyin == null ? "" : shoupinyin;
    }

    public void setShoupinyin(String shoupinyin) {
        this.shoupinyin = shoupinyin;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
