package io.github.tesla.filter.support.circuitbreaker.state;

import java.io.Serializable;

import io.github.tesla.filter.support.circuitbreaker.AbstractCircuitBreaker;

/**
 * 熔断器状态
 */
public interface CBState extends Serializable {

    String CIRCUITBREAKER_HALFOPENCBSTATE_FAILNUM = "CIRCUITBREAKER_HALFOPENCBSTATE_FAILNUM";

    String CIRCUITBREAKER_HALFOPENCBSTATE_PASSNUM = "CIRCUITBREAKER_HALFOPENCBSTATE_PASSNUM";

    String CIRCUITBREAKER_CLOSECBSTATE_FAILNUM = "CIRCUITBREAKER_CLOSECBSTATE_FAILNUM";

    /**
     * 获取当前状态名称
     */
    String getStateName();

    /**
     * 检查以及校验当前状态是否需要扭转
     */
    void checkAndSwitchState(AbstractCircuitBreaker cb);

    /**
     * 是否允许通过熔断器
     */
    boolean canPassCheck(AbstractCircuitBreaker cb);

    /**
     * 统计失败次数
     */
    void countFailNum(AbstractCircuitBreaker cb);
}
