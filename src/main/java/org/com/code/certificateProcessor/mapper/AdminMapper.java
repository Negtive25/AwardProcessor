package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.entity.Admin;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin getAdminAuth(String userName);
    int addAdmin(Admin admin);
    Admin getAdminByUserName(String username);
    int updateAdminInfo(Admin admin);

    List<Admin> getLatterAdmin(String lastId, Integer pageSize);
    List<Admin> getPreviousAdmin(String lastId, Integer pageSize);

    int updateAdminAuth(String username, String auth);
}
