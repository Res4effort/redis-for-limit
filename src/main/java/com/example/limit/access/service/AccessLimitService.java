package com.example.limit.access.service;


public interface AccessLimitService {

    /**
     * record user's access times
     * @return save successfully or not
     */
   void saveAccessTimes(String key, int duration, int limitTimes);

    /**
     * judge the user is limited or not
     * @return is limited or not
     */
   boolean isLimited(String limitId);
}
