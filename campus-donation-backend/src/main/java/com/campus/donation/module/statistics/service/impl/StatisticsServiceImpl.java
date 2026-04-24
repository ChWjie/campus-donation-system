package com.campus.donation.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.donation.module.charity.mapper.CharityDockingMapper;
import com.campus.donation.module.station.mapper.CollectionStationMapper;
import com.campus.donation.module.donation.mapper.DonationAppointmentMapper;
import com.campus.donation.module.item.mapper.DonationItemMapper;
import com.campus.donation.module.statistics.service.StatisticsService;
import com.campus.donation.module.user.entity.SysUser;
import com.campus.donation.module.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DonationAppointmentMapper appointmentMapper;
    private final DonationItemMapper itemMapper;
    private final CharityDockingMapper dockingMapper;
    private final SysUserMapper userMapper;
    private final CollectionStationMapper stationMapper;

    public StatisticsServiceImpl(DonationAppointmentMapper appointmentMapper,
            DonationItemMapper itemMapper,
            CharityDockingMapper dockingMapper,
            SysUserMapper userMapper,
            CollectionStationMapper stationMapper) {
        this.appointmentMapper = appointmentMapper;
        this.itemMapper = itemMapper;
        this.dockingMapper = dockingMapper;
        this.userMapper = userMapper;
        this.stationMapper = stationMapper;
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 总预约单数
        overview.put("totalAppointments", appointmentMapper.selectCount(null));

        // 总公益对接次数
        overview.put("totalDockings", dockingMapper.countTotal());

        // 回收点数量
        overview.put("totalStations", stationMapper.selectCount(null));

        // 捐赠用户数 (类型为 1 的用户)
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUserType, 1);
        overview.put("totalDonors", userMapper.selectCount(userWrapper));

        // 其他扩展指标 (可选，保留作为内部参考)
        overview.put("activeUsers", appointmentMapper.countDistinctDonors());

        return overview;
    }

    @Override
    public List<Map<String, Object>> getMonthlyTrend() {
        return appointmentMapper.countMonthlyAppointments();
    }

    @Override
    public List<Map<String, Object>> getItemTypeDistribution() {
        return itemMapper.countByType();
    }

    @Override
    public Map<String, Object> getDockingStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 对接总数
        stats.put("total", dockingMapper.countTotal());

        // 月度对接趋势
        stats.put("monthlyTrend", dockingMapper.countMonthlyDocking());

        return stats;
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 用户的预约数
        QueryWrapper<com.campus.donation.module.donation.entity.DonationAppointment> wrapper = new QueryWrapper<>();
        wrapper.eq("donor_id", userId);
        stats.put("appointmentCount", appointmentMapper.selectCount(wrapper));

        // 用户捐赠的旧物总数
        List<com.campus.donation.module.donation.entity.DonationAppointment> appointments = appointmentMapper
                .selectList(wrapper);
        int totalQuantity = appointments.stream()
                .mapToInt(a -> a.getItemQuantity() != null ? a.getItemQuantity() : 0)
                .sum();
        stats.put("totalQuantity", totalQuantity);

        // 已完成的预约数
        QueryWrapper<com.campus.donation.module.donation.entity.DonationAppointment> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("donor_id", userId).eq("status", 4);
        stats.put("completedCount", appointmentMapper.selectCount(completedWrapper));

        return stats;
    }
}
