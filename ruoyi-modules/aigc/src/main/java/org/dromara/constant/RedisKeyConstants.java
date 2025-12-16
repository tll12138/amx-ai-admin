package org.dromara.constant;


/**
 * System Redis Key 枚举类
 *
 * @author 芋道源码
 */
public interface RedisKeyConstants {

    String DINGTALK_ACCESS_TOKEN = "dingtalk:access_token";
    String START_TIME = "start_time";
    String LOCATION = "location:%s";
    String LOCATION_INFO = "location:info:%s";
    String LOCATION_BOOKING_LOCK = "location_booking_lock:%s";

    String BOOKED = "booked:%s";
    String USER_LOCK = "user:lock:%s";

    String DINGTALK_USER_UNION_ID = "dingtalk:user_union_id:%s";

}
