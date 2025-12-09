package com.finance.controller;

import com.finance.dto.AccountDTO;
import com.finance.exception.BusinessException;
import com.finance.service.AccountService;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.AccountVO;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private HttpSession session;

    private Long getUserId() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录或会话已过期"); // 应该被拦截器处理
        }
        return loginUser.getId();
    }

    /**
     * 跳转账户列表页面
     * @param model Model
     * @return "account/list"
     */
    @GetMapping("/list")
    public String accountListPage(Model model) {
        Long userId = getUserId();
        List<AccountVO> accounts = accountService.getAccountsByUserId(userId);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalAssets", accountService.getTotalAssets(userId));
        return "account/list";
    }

    /**
     * 跳转添加账户页面
     * @return "account/add"
     */
    @GetMapping("/add")
    public String addAccountPage(Model model) {
        model.addAttribute("accountDTO", new AccountDTO()); // 为表单提供一个空对象
        return "account/add";
    }

    /**
     * 添加账户API
     * @param accountDTO 账户DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/add")
    @ResponseBody
    public Result<String> addAccount(@Valid @RequestBody AccountDTO accountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        accountService.addAccount(userId, accountDTO);
        return ResultUtil.success("账户添加成功");
    }

    /**
     * 跳转编辑账户页面
     * @param id 账户ID
     * @param model Model
     * @return "account/edit"
     */
    @GetMapping("/edit/{id}")
    public String editAccountPage(@PathVariable Long id, Model model) {
        Long userId = getUserId();
        AccountVO accountVO = accountService.getAccountById(userId, id);
        if (accountVO == null) {
            throw new BusinessException("账户不存在");
        }
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(accountVO, accountDTO);
        accountDTO.setInitialBalance(accountVO.getCurrentBalance()); // 编辑时默认显示当前余额
        model.addAttribute("accountDTO", accountDTO);
        model.addAttribute("accountId", id); // 传递ID用于表单提交
        return "account/edit";
    }

    /**
     * 更新账户API
     * @param id 账户ID
     * @param accountDTO 账户DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/update/{id}")
    @ResponseBody
    public Result<String> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        accountDTO.setId(id); // 设置DTO的ID
        accountService.updateAccount(userId, accountDTO);
        return ResultUtil.success("账户更新成功");
    }

    /**
     * 删除账户API
     * @param id 账户ID
     * @return JSON结果
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public Result<String> deleteAccount(@PathVariable Long id) {
        Long userId = getUserId();
        accountService.deleteAccount(userId, id);
        return ResultUtil.success("账户删除成功");
    }

    /**
     * 获取用户所有账户的API（用于下拉选择等场景）
     * @return JSON结果
     */
    @GetMapping("/api/all")
    @ResponseBody
    public Result<List<AccountVO>> getAllAccounts() {
        Long userId = getUserId();
        List<AccountVO> accounts = accountService.getAccountsByUserId(userId);
        return ResultUtil.success(accounts);
    }
}