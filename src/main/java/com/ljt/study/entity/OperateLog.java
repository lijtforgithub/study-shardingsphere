package com.ljt.study.entity;

import lombok.Data;

/**
 * @author LiJingTang
 * @date 2022-12-01 19:21
 */
@Data
public class OperateLog {
    private Long id;
    private Long userId;
    private String operate;
    private String content;

}
