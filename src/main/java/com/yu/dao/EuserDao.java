package com.yu.dao;

import com.yu.domain.Euser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author za'y
* @description 针对表【euser】的数据库操作Mapper
* @createDate 2022-07-30 17:25:31
* @Entity com.yu.domain.Euser
*/
@Mapper
public interface EuserDao extends BaseMapper<Euser> {

}




