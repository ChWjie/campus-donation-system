package com.campus.donation.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.donation.common.result.R;
import com.campus.donation.module.user.entity.SysUser;
import com.campus.donation.module.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器 (仅管理员可见)
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public R<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType) {
        
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        
        if (userType != null) {
            wrapper.eq(SysUser::getUserType, userType);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        return R.ok(userService.page(page, wrapper));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? R.ok("状态更新成功") : R.fail("更新失败");
    }

    @Operation(summary = "更新用户类型/角色")
    @PutMapping("/{id}/role")
    public R<String> updateRole(@PathVariable Long id, @RequestParam Integer userType) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUserType(userType);
        boolean success = userService.updateById(user);
        return success ? R.ok("角色更新成功") : R.fail("更新失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<String> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? R.ok("删除成功") : R.fail("删除失败");
    }
}
