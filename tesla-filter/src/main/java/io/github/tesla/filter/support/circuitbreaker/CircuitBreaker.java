package io.github.tesla.filter.support.circuitbreaker;

/**
 * 
 * 熔断器接口
 */
public interface CircuitBreaker {

    /**
     * 带参数重置熔断器，如果参数相同则重置，否则不重置
     */
    void reset(String failRateForClose, int idleTimeForOpen, String passRateForHalfOpen, int failNumForHalfOpen);

    /**
     * 重置熔断器
     */
    void reset();

    /**
     * 是否允许通过熔断器
     */
    boolean canPassCheck();

    /**
     * 统计失败次数
     */
    void countFailNum();
}
