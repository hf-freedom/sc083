package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.dto.RecallRequest;
import com.vaccine.entity.RecallNotice;
import com.vaccine.entity.User;
import com.vaccine.service.RecallService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/recalls")
public class RecallController {

    @Resource
    private RecallService recallService;

    @PostMapping
    public ApiResponse<RecallNotice> recallBatch(@Valid @RequestBody RecallRequest request) {
        try {
            RecallNotice notice = recallService.recallBatch(
                    request.getBatchId(),
                    request.getRecallReason(),
                    request.getRecallLevel()
            );
            return ApiResponse.success(notice, "批次召回成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<RecallNotice>> getAllRecallNotices() {
        return ApiResponse.success(recallService.getAllRecallNotices());
    }

    @GetMapping("/{id}")
    public ApiResponse<RecallNotice> getRecallNoticeById(@PathVariable Long id) {
        RecallNotice notice = recallService.getRecallNoticeById(id);
        if (notice == null) {
            return ApiResponse.error("召回通知不存在");
        }
        return ApiResponse.success(notice);
    }

    @PostMapping("/{id}/mark-notified")
    public ApiResponse<RecallNotice> markAsNotified(@PathVariable Long id) {
        try {
            RecallNotice notice = recallService.markAsNotified(id);
            return ApiResponse.success(notice, "已标记为已通知");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/affected-users/{batchId}")
    public ApiResponse<List<User>> getAffectedUsers(@PathVariable Long batchId) {
        return ApiResponse.success(recallService.getAffectedUsers(batchId));
    }
}
