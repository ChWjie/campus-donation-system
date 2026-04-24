package com.campus.donation.module.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.donation.module.station.entity.CollectionStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 回收点 Mapper 接口
 */
@Mapper
public interface CollectionStationMapper extends BaseMapper<CollectionStation> {

    /**
     * 查询可用回收点列表（状态为开放且未满）
     */
    @Select("SELECT * FROM collection_station " +
            "WHERE deleted = 0 AND status = 1 AND current_num < capacity " +
            "ORDER BY create_time DESC")
    List<CollectionStation> selectAvailableStations();
}
