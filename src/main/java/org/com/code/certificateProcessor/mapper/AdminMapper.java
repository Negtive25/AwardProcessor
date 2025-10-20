package org.com.code.certificateProcessor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.com.code.certificateProcessor.pojo.Admin;
import org.com.code.certificateProcessor.pojo.AwardSubmission;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin getAdminAuth(String userName);
    int addAdmin(Admin admin);
    Admin getAdminByUserName(String username);
}
