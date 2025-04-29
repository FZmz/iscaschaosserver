package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iscas.lndicatormonitor.dto.UserQueryCriteria;

import java.util.List;

public interface UsersService extends IService<Users> {
    int usersInsert(UsersDTO usersDTO) throws Exception;

    Users userLogin(UsersDTO usersDTO) throws Exception;

    String getRealNameById(int userId) throws Exception;

    Users selectByUserName(String userName) throws Exception;
    List<UsersDTO> getAllUsersDto() throws Exception;

    List<Users> getAllUsers() throws  Exception;

    int deleteById(int userId) throws  Exception;

    int updateUser(UsersDTO usersDTO) throws  Exception;

    Boolean isHaveCommonUser(String userName);

    int updatePwd(UsersDTO usersDTO) throws  Exception;
    List<Integer> getIdsByName(String name);

    /**
     * 分页查询用户
     * @param criteria 查询条件
     * @return 分页结果
     */
    IPage<UsersDTO> getUsersByPage(UserQueryCriteria criteria);
}
