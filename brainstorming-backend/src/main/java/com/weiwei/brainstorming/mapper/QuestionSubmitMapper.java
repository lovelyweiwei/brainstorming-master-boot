package com.weiwei.brainstorming.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-08-31 18:27:23
* @Entity com.weiwei.brainstorming.model.entity.QuestionSubmit
*/
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {
    Integer getPassCount(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}




