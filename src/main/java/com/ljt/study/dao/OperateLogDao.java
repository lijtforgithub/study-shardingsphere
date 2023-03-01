package com.ljt.study.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljt.study.entity.OperateLog;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2022-12-02 9:57
 */
public interface OperateLogDao extends BaseMapper<OperateLog> {

    int insertList(List<OperateLog> list);

}
