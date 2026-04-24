package com.campus.donation.module.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.station.entity.CollectionStation;

import java.util.List;

/**
 * 回收点服务接口
 */
public interface CollectionStationService extends IService<CollectionStation> {

    /**
     * 查询所有回收点
     */
    List<CollectionStation> getAllStations();

    /**
     * 查询可用回收点
     */
    List<CollectionStation> getAvailableStations();

    /**
     * 检查回收点容量
     * 
     * @param stationId 回收点ID
     * @throws RuntimeException 如果容量已满
     */
    void checkCapacity(Long stationId);

    /**
     * 更新回收点存量
     * 
     * @param stationId 回收点ID
     * @param delta     变化量（正数增加，负数减少）
     */
    void updateCurrentNum(Long stationId, int delta);

    /**
     * 创建回收点
     */
    boolean createStation(CollectionStation station);

    /**
     * 更新回收点
     */
    boolean updateStation(CollectionStation station);

    /**
     * 删除回收点（逻辑删除）
     */
    boolean deleteStation(Long id);
}
