package com.weiwei.brainstorming.model.vo;

import lombok.Data;

@Data
public class SubmitSummaryVO {
    //题库总数
    private Integer total;
    //简单
    private Integer easyPass;
    private Integer easyTotal;
    //中等
    private Integer mediumPass;
    private Integer mediumTotal;

    //困难
    private Integer hardPass;
    private Integer hardTotal;

    //提交总数
    private Integer submitCount;
    //通过总数
    private Integer passCount;
}
