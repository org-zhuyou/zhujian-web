package com.proflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.proflow.entity.User;
import com.proflow.entity.vo.UserVO;
import com.proflow.mapper.UserMapper;
import com.proflow.service.MenuService;
import com.proflow.service.RoleService;
import com.proflow.service.UserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Leonid
 * @since 2018-08-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Override
    public UserVO login(String username, String password) throws Exception {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new IllegalArgumentException();
        }
        User user = this.findUserByUsername(username);
        if (ObjectUtil.isNull(user)) {
            throw new Exception("用户名不存在");
        }
        String checkPwd = SecureUtil.md5(password);
        if (!checkPwd.equals(user.getPassword())) {
            throw new Exception("用户名或密码错误");
        }
        UserVO userVO = new UserVO();
        // copy
        BeanUtil.copyProperties(user, userVO);
        // 获取所有角色
        userVO.setRoles(roleService.findRolesByUserId(user.getId()));
        // 获取所有权限
        userVO.setMenus(menuService.findMenusByUserId(user.getId()));
        return userVO;
    }

    @Override
    public User findUserByUsername(String username) throws Exception {
        if(StrUtil.isBlank(username)) {
            throw new Exception("请输入用户名");
        }
        User user = new User();
        if (Validator.isMobile(username)) {
            // 如果此用户名是一个手机号
            user.setMobile(username);
        } else if (Validator.isMobile(username)) {
            // 如果此用户名是一个邮箱
            user.setEmail(username);
        } else {
            // 如果此用户名就是一个用户名
            user.setUsername(username);
        }
        EntityWrapper<User> entityWrapper = new EntityWrapper<>();
        entityWrapper.setEntity(user);
        return this.selectOne(entityWrapper);
    }

    @Override
    public User save(User user) throws Exception {
        if (user == null) throw new IllegalArgumentException();
        if(StrUtil.isNotBlank(user.getPassword())) {
            String pwd = SecureUtil.md5(user.getPassword());
            user.setPassword(pwd);
        }
        if (!this.insertOrUpdate(user)) {
            throw new Exception("保存失败");
        }
        return user;
    }
}
