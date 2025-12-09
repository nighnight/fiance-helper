package com.finance.controller;

import com.finance.exception.BusinessException;
import com.finance.mapper.FinanceRecordMapper; // 用于获取最新ID
import com.finance.po.DataSync;
import com.finance.service.SyncService;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/sync")
public class SyncController {

    @Autowired
    private SyncService syncService;
    @Autowired
    private FinanceRecordMapper financeRecordMapper; // 假设需要获取最新记录ID来同步
    @Autowired
    private HttpSession session;

    private Long getUserId() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录或会话已过期");
        }
        return loginUser.getId();
    }

    /**
     * 跳转数据同步页面
     * @param model Model
     * @return "sync/page"
     */
    @GetMapping("/page")
    public String syncPage(Model model) {
        Long userId = getUserId();
        // 获取各类数据的同步信息
        DataSync recordSync = syncService.getSyncInfo(userId, "record");
        DataSync accountSync = syncService.getSyncInfo(userId, "account");
        DataSync categorySync = syncService.getSyncInfo(userId, "category");

        model.addAttribute("recordSync", recordSync);
        model.addAttribute("accountSync", accountSync);
        model.addAttribute("categorySync", categorySync);
        return "sync/page";
    }

    /**
     * 执行同步操作（这里以同步收支记录为例，更新其max_sync_id）
     * 实际离线同步会复杂得多，这只是一个标记同步状态的示例
     * @param syncType 同步数据类型
     * @return JSON结果
     */
    @PostMapping("/api/triggerSync")
    @ResponseBody
    public Result<String> triggerSync(@RequestParam String syncType) {
        Long userId = getUserId();
        Long maxId = null;

        // 根据不同的syncType获取不同的最大ID
        if ("record".equals(syncType)) {
            maxId = financeRecordMapper.selectMaxIdByUserId(userId);
        } else if ("account".equals(syncType)) {
            // TODO: accountMapper 补充selectMaxIdByUserId方法
        } else if ("category".equals(syncType)) {
            // TODO: financeCategoryMapper 补充selectMaxIdByUserId方法
        } else {
            return ResultUtil.error("不支持的同步类型");
        }

        if (maxId == null) {
            maxId = 0L; // 如果没有数据，最大ID为0
        }
        syncService.updateSyncInfo(userId, syncType, maxId);
        return ResultUtil.success("数据类型[" + syncType + "]同步标记更新成功");
    }

    // TODO: 实现离线同步的增量数据拉取API，例如：
    // @GetMapping("/api/pullRecords")
    // @ResponseBody
    // public Result<List<RecordVO>> pullRecords(@RequestParam Long lastSyncId) {
    //    Long userId = getUserId();
    //    List<RecordVO> deltaRecords = financeRecordService.getDeltaRecords(userId, lastSyncId); // 需要在service中实现
    //    return ResultUtil.success(deltaRecords);
    // }
}