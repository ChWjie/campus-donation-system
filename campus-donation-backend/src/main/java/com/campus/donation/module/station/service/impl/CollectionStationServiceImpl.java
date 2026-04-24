package com.campus.donation.module.station.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.station.entity.CollectionStation;
import com.campus.donation.module.station.mapper.CollectionStationMapper;
import com.campus.donation.module.station.service.CollectionStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 回收点服务实现类
 */
@Service
public class CollectionStationServiceImpl
        extends ServiceImpl<CollectionStationMapper, CollectionStation>
        implements CollectionStationService {

    private static final Logger log = LoggerFactory.getLogger(CollectionStationServiceImpl.class);

    @Override
    public List<CollectionStation> getAllStations() {
        return lambdaQuery()
                .eq(CollectionStation::getDeleted, 0)
                .orderByDesc(CollectionStation::getCreateTime)
                .list();
    }

    @Override
    public List<CollectionStation> getAvailableStations() {
        return baseMapper.selectAvailableStations();
    }

    @Override
    public void checkCapacity(Long stationId) {
        CollectionStation station = getById(stationId);
        if (station == null) {
            throw new BusinessException("回收点不存在");
        }
        if (station.getStatus() == 0) {
            throw new BusinessException("回收点已关闭");
        }
        if (station.getCurrentNum() >= station.getCapacity()) {
            throw new BusinessException("回收点容量已满，请选择其他回收点");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentNum(Long stationId, int delta) {
        CollectionStation station = getById(stationId);
        if (station == null) {
            throw new BusinessException("回收点不存在");
        }

        int newNum = station.getCurrentNum() + delta;
        if (newNum < 0) {
            throw new BusinessException("回收点存量不能为负数");
        }
        if (newNum > station.getCapacity()) {
            throw new BusinessException("超出回收点容量上限");
        }

        lambdaUpdate()
                .eq(CollectionStation::getId, stationId)
                .set(CollectionStation::getCurrentNum, newNum)
                .update();

        log.info("更新回收点存量: stationId={}, delta={}, newNum={}", stationId, delta, newNum);
    }

    @Override
    public boolean createStation(CollectionStation station) {
        // 设置默认值
        if (station.getCapacity() == null) {
            station.setCapacity(100);
        }
        if (station.getCurrentNum() == null) {
            station.setCurrentNum(0);
        }
        if (station.getStatus() == null) {
            station.setStatus(1);
        }
        return save(station);
    }

    @Override
    public boolean updateStation(CollectionStation station) {
        return updateById(station);
    }

    @Override
    public boolean deleteStation(Long id) {
        return removeById(id);
    }
}
