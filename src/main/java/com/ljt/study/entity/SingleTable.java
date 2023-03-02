package com.ljt.study.entity;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * @author LiJingTang
 * @date 2023-03-01 16:18
 */
@Data
public class SingleTable {

    private Long id;
    private String type;
    private String content;
    @Version
    private Integer version;
    private Byte deleteFlag;

}
