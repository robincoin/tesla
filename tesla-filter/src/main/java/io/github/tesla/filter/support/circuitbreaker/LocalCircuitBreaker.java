package io.github.tesla.filter.support.circuitbreaker;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.support.circuitbreaker.state.CloseCBState;

/**
 * 本地熔断器(把它当成了工厂了)
 */
public class LocalCircuitBreaker extends AbstractCircuitBreaker {

    public LocalCircuitBreaker(String failRateForClose, int idleTimeForOpen, String passRateForHalfOpen,
        int failNumForHalfOpen) {
        this.thresholdFailRateForClose = failRateForClose;
        this.thresholdIdleTimeForOpen = idleTimeForOpen;
        this.thresholdPassRateForHalfOpen = passRateForHalfOpen;
        this.thresholdFailNumForHalfOpen = failNumForHalfOpen;
    }

    public void reset() {
        this.setState(new CloseCBState());
    }

    private boolean isSame(String failRateForClose, int idleTimeForOpen, String passRateForHalfOpen,
        int failNumForHalfOpen) {
        return StringUtils.equals(failRateForClose, thresholdFailRateForClose)
            && StringUtils.equals(passRateForHalfOpen, thresholdPassRateForHalfOpen)
            && thresholdIdleTimeForOpen == idleTimeForOpen && failNumForHalfOpen == thresholdFailNumForHalfOpen;

    }

    public void reset(String failRateForClose, int idleTimeForOpen, String passRateForHalfOpen,
        int failNumForHalfOpen) {
        if (!this.isSame(failRateForClose, idleTimeForOpen, passRateForHalfOpen, failNumForHalfOpen)) {
            this.thresholdFailRateForClose = failRateForClose;
            this.thresholdIdleTimeForOpen = idleTimeForOpen;
            this.thresholdPassRateForHalfOpen = passRateForHalfOpen;
            this.thresholdFailNumForHalfOpen = failNumForHalfOpen;
            this.setState(new CloseCBState());
        }
    }

    public boolean canPassCheck() {
        return getState().canPassCheck(this);
    }

    public void countFailNum() {
        getState().countFailNum(this);
    }
}
