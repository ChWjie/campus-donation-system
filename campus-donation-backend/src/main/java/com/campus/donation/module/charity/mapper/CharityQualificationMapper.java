package com.campus.donation.module.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.donation.module.charity.entity.CharityQualification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公益资质审核 Mapper
 */
@Mapper
public interface CharityQualificationMapper extends BaseMapper<CharityQualification> {

    @Select("SELECT q.*, u.username, u.real_name FROM charity_qualification q " +
            "LEFT JOIN sys_user u ON q.user_id = u.id " +
            "WHERE q.deleted = 0 ${ew.customSqlSegment}")
    IPage<CharityQualification> selectQualificationPage(Page<CharityQualification> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<CharityQualification> wrapper);
}
