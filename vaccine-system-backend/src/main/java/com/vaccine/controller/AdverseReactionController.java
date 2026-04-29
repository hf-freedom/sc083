package com.vaccine.controller;

import com.vaccine.dto.AdverseReactionRequest;
import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.AdverseReaction;
import com.vaccine.entity.enums.AdverseReactionStatus;
import com.vaccine.service.AdverseReactionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/adverse-reactions")
public class AdverseReactionController {

    @Resource
    private AdverseReactionService adverseReactionService;

    @PostMapping("/report")
    public ApiResponse<AdverseReaction> reportAdverseReaction(@Valid @RequestBody AdverseReactionRequest request) {
        try {
            AdverseReaction reaction = adverseReactionService.reportAdverseReaction(
                    request.getVaccinationRecordId(),
                    request.getSymptoms(),
                    request.getSeverity(),
                    request.getReactionTime()
            );
            return ApiResponse.success(reaction, "异常反应已上报");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<AdverseReaction>> getAllAdverseReactions() {
        return ApiResponse.success(adverseReactionService.getAllAdverseReactions());
    }

    @GetMapping("/{id}")
    public ApiResponse<AdverseReaction> getAdverseReactionById(@PathVariable Long id) {
        AdverseReaction reaction = adverseReactionService.getAdverseReactionById(id);
        if (reaction == null) {
            return ApiResponse.error("异常反应记录不存在");
        }
        return ApiResponse.success(reaction);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AdverseReaction>> getAdverseReactionsByUserId(@PathVariable Long userId) {
        return ApiResponse.success(adverseReactionService.getAdverseReactionsByUserId(userId));
    }

    @GetMapping("/batch/{batchId}")
    public ApiResponse<List<AdverseReaction>> getAdverseReactionsByBatchId(@PathVariable Long batchId) {
        return ApiResponse.success(adverseReactionService.getAdverseReactionsByBatchId(batchId));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdverseReaction> updateAdverseReaction(
            @PathVariable Long id,
            @RequestParam(required = false) AdverseReactionStatus status,
            @RequestParam(required = false) String treatmentMeasures,
            @RequestParam(required = false) String handler,
            @RequestParam(required = false) String remark) {
        try {
            AdverseReaction reaction = adverseReactionService.updateAdverseReaction(
                    id, status, treatmentMeasures, handler, remark
            );
            return ApiResponse.success(reaction, "更新成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
