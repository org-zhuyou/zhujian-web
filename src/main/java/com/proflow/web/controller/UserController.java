package com.proflow.web.controller;

import com.proflow.entity.User;
import com.proflow.entity.vo.UserVO;
import com.proflow.service.UserService;
import com.proflow.web.form.ResultForm;
import com.proflow.web.utils.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Leonid on 2018/8/8.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public Object save(User user, HttpServletRequest request) {
        ResultForm<?> resultForm = null;
        try {
            UserVO currentUser = SessionUtil.getCurrentUser(request);
            user = userService.save(user);
            resultForm = ResultForm.createSuccess("保存成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultForm = ResultForm.createError(e.getMessage());
        }
        return resultForm;
    }

    @PostMapping("/delete")
    public Object delete(Long id) {
        ResultForm<?> resultForm = null;
        try {
            if (userService.delete(id)) {
                resultForm = ResultForm.createSuccess("删除成功", null);
            } else {
                resultForm = ResultForm.createError("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultForm = ResultForm.createError(e.getMessage());
        }
        return resultForm;
    }

    @PostMapping("/findById")
    public Object findById(Long id) {
        ResultForm<?> resultForm = null;
        try {
            User user = userService.selectById(id);
            if (null == user) {
                throw new Exception("用户不存在");
            }
            resultForm = ResultForm.createSuccess("查询成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultForm = ResultForm.createError(e.getMessage());
        }
        return resultForm;
    }



}
