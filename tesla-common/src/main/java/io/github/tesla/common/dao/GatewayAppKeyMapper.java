package io.github.tesla.common.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.common.domain.GatewayAppKeyDO;

@Mapper
public interface GatewayAppKeyMapper extends BaseMapper<GatewayAppKeyDO> {

}
