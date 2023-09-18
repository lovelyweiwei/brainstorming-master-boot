package com.weiwei.brainstorming.judge.codesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {

    //退出码
    private Integer exitValue;

    //正常信息
    private String message;

    //错误信息
    private String errorMessage;

    //运行时间
    private Long time;

    //消耗内存
    private Long memory;
}
