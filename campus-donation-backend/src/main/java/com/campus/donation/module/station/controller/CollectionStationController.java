package com.campus.donation.module.station.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.station.entity.CollectionStation;
import com.campus.donation.module.station.service.CollectionStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收点管理控制器
 */
@Tag(name = "回收点管理")
@RestController
@RequestMapping("/station")
public class CollectionStationController {

    private final CollectionStationService stationService;

    public CollectionStationController(CollectionStationService stationService) {
        this.stationService = stationService;
    }

    @Operation(summary = "查询所有回收点")
    @GetMapping("/list")
    public R<List<CollectionStation>> getAllStations() {
        List<CollectionStation> stations = stationService.getAllStations();
        return R.ok(stations);
    }

    @Operation(summary = "查询可用回收点")
    @GetMapping("/available")
    public R<List<CollectionStation>> getAvailableStations() {
        List<CollectionStation> stations = stationService.getAvailableStations();
        return R.ok(stations);
    }

    @Operation(summary = "根据ID查询回收点")
    @GetMapping("/{id}")
    public R<CollectionStation> getStationById(@PathVariable Long id) {
        CollectionStation station = stationService.getById(id);
        if (station == null) {
            return R.fail("回收点不存在");
        }
        return R.ok(station);
    }

    @Operation(summary = "创建回收点")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public R<String> createStation(@RequestBody CollectionStation station) {
        boolean success = stationService.createStation(station);
        return success ? R.ok("创建成功") : R.fail("创建失败");
    }

    @Operation(summary = "更新回收点")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public R<String> updateStation(@PathVariable Long id, @RequestBody CollectionStation station) {
        station.setId(id);
        boolean success = stationService.updateStation(station);
        return success ? R.ok("更新成功") : R.fail("更新失败");
    }

    @Operation(summary = "删除回收点")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public R<String> deleteStation(@PathVariable Long id) {
        boolean success = stationService.deleteStation(id);
        return success ? R.ok("删除成功") : R.fail("删除失败");
    }
}
