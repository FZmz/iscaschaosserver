package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.HistoryPwd;
import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.UserQueryCriteria;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.iscas.lndicatormonitor.mapper.HistoryPwdMapper;
import com.iscas.lndicatormonitor.mapper.RoleMapper;
import com.iscas.lndicatormonitor.mapper.UsersMapper;
import com.iscas.lndicatormonitor.service.RoleService;
import com.iscas.lndicatormonitor.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Autowired
    UsersMapper usersMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    HistoryPwdMapper historyPwdMapper;

    @Autowired
    RoleService roleService;

    @Override
    public int usersInsert(UsersDTO usersDTO) throws Exception {
        Users users = new Users();
        BeanUtils.copyProperties(usersDTO, users);
        users.setRealName(usersDTO.getReal_name());
        System.out.println(users);
        usersMapper.insert(users);
        return users.getId();
    }

    @Override
    public Users userLogin(UsersDTO usersDTO) throws Exception {
        Users users = usersMapper.selectByUserName(usersDTO.getUsername());
        // 检查用户是否存在
        if (users != null) {
            // 比较数据库中的明文密码和加密后的密码
            if (usersDTO.getPassword().equals(users.getPassword())) {
                return users; // 密码匹配
            } else {
                users.setId(-999); // 密码不匹配
                return users;
            }
        }
        return null; // 用户名不存在
    }

    @Override
    public String getRealNameById(int userId) throws Exception {
        Users users = usersMapper.selectByPrimaryKey(userId);
        if (users != null) {
            return users.getRealName();
        }
        return null;
    }

    @Override
    public Users selectByUserName(String userName) throws Exception {
        return usersMapper.selectByUserName(userName);
    }

    public List<UsersDTO> getAllUsersDto() throws Exception {
        List<Users> usersList = usersMapper.getAll();
        List<Role> roleList = roleMapper.selectAllRole();

        // 创建一个Map来快速查找每个用户的角色
        Map<Integer, Role> roleMap = roleList.stream()
                .collect(Collectors.toMap(Role::getUserid, role -> role));

        // 将每个Users对象转换为UsersDTO对象
        List<UsersDTO> usersDTOList = usersList.stream().map(users -> {
            UsersDTO dto = new UsersDTO();
            dto.setId(users.getId());
            dto.setUsername(users.getUsername());
            // dto.setPassword(users.getPassword()); // 注意: 根据实际情况考虑是否需要传输密码
            dto.setReal_name(users.getRealName());

            // 查找并设置相应的角色
            Role role = roleMap.get(users.getId());
            if (role != null) {
                dto.setRole(role.getRoleType());
            }

            return dto;
        }).collect(Collectors.toList());

        return usersDTOList;
    }

    public List<Users> getAllUsers() {
        try {
            List<Users> usersList = usersMapper.getAll();
            return usersList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public int deleteById(int userId) throws Exception {
        int userDeleted = usersMapper.deleteByPrimaryKey(userId);
        int roleDeleted = roleMapper.deleteByUserId(userId);
        if (userDeleted > 0 && roleDeleted > 0) {
            return 1; // 表示用户及其角色都成功删除
        } else if (userDeleted > 0) {
            return 2; // 表示只有用户删除成功
        } else {
            return 0; // 表示删除失败
        }
    }

    @Override
    @Transactional
    public int updateUser(UsersDTO usersDTO) throws Exception {
        Users users = new Users();
        Role role = roleMapper.selectByUserId(usersDTO.getId());
        role.setRoleType(usersDTO.getRole());
        role.setUserid(usersDTO.getId());
        BeanUtils.copyProperties(usersDTO, users);
        int userUpdated = usersMapper.updateByPrimaryKeySelective(users);
        int roleUptated = roleMapper.updateByPrimaryKey(role);
        if (userUpdated > 0 && roleUptated > 0) {
            return 1; // 表示用户及其角色都成功删除
        } else if (userUpdated > 0) {
            return 2; // 表示只有用户删除成功
        } else {
            return 0; // 表示删除失败
        }
    }

    @Override
    public Boolean isHaveCommonUser(String userName) {
        return usersMapper.checkUsernameExists(userName) == 1 ? true : false;
    }

    @Override
    public int updatePwd(UsersDTO usersDTO) throws Exception {
        // 1、判断与当前密码是否相同 相同则返回 2
        Users user = usersMapper.selectByPrimaryKey(usersDTO.getId());
        if (!user.getPassword().equals(usersDTO.getPassword())) {
            // 2、判断修改密码是否出现在最近5次的修改记录当中 是则返回 3
            List<HistoryPwd> historyPwdList = historyPwdMapper.selectLastFiveByUserId(user.getId());
            for (HistoryPwd historyPwd : historyPwdList) {
                if (historyPwd.getPasswordhash().equals(usersDTO.getPassword())) {
                    return 3;
                }
            }
            usersMapper.updatePwdById(usersDTO.getId(), usersDTO.getPassword());
            // 3、如果都通过了 那么即可修改 返回1
            // 记住需要添加历史修改记录
            HistoryPwd historyPwd = new HistoryPwd();
            historyPwd.setUserid(usersDTO.getId());
            historyPwd.setChangedate(new Date());
            historyPwd.setPasswordhash(usersDTO.getPassword());
            historyPwdMapper.insertSelective(historyPwd);
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public List<Integer> getIdsByName(String name) {
        if (StringUtils.isBlank(name)) {
            return new ArrayList<>();
        }

        try {
            // 构建查询条件
            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("real_name", name) // 假设数据库字段名为 real_name
                    .select("id"); // 只查询 id 字段

            // 执行查询并收集 id
            return usersMapper.selectList(queryWrapper)
                    .stream()
                    .map(Users::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据用户名查询ID失败，name={}", name, e);
            return new ArrayList<>();
        }
    }

    @Override
    public IPage<UsersDTO> getUsersByPage(UserQueryCriteria criteria) {
        // 创建分页对象
        Page<Users> page = new Page<>(criteria.getPage(), criteria.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();

        // 添加用户名模糊查询条件
        if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
            queryWrapper.like(Users::getUsername, criteria.getUsername());
        }

        // 添加真实姓名模糊查询条件
        if (criteria.getRealName() != null && !criteria.getRealName().isEmpty()) {
            queryWrapper.like(Users::getRealName, criteria.getRealName());
        }

        // 执行分页查询
        IPage<Users> userPage = this.page(page, queryWrapper);

        // 转换为 DTO 对象
        IPage<UsersDTO> dtoPage = userPage.convert(user -> {
            UsersDTO dto = new UsersDTO();
            BeanUtils.copyProperties(user, dto);
            dto.setReal_name(user.getRealName());

            // 查询用户角色
            if (user.getId() != null) {
                Role role = roleService.selectByUserId(user.getId());
                if (role != null) {
                    dto.setRole(role.getRoleType());
                }
            }

            // 隐藏密码
            dto.setPassword("******");

            return dto;
        });

        // 如果需要按角色类型过滤
        if (criteria.getRoleType() != null) {
            List<UsersDTO> filteredRecords = dtoPage.getRecords().stream()
                    .filter(dto -> criteria.getRoleType().equals(dto.getRole()))
                    .collect(Collectors.toList());

            // 重新设置记录和总数
            long total = filteredRecords.size();
            dtoPage.setRecords(filteredRecords);
            dtoPage.setTotal(total);
        }

        return dtoPage;
    }
}
