package dev.sagar.smsblocker.tech.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class DateUtilSingleton {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android

    //Java Core
    private static final DateUtilSingleton ourInstance = new DateUtilSingleton();

    /**
     * This method is part of Singleton Design pattern.
     * @return
     */
    public synchronized static DateUtilSingleton getInstance() {
        return ourInstance;
    }

    /**
     * This method is part of Singleton Design pattern
     */
    private DateUtilSingleton() {
    }


    /**
     * This method returns date in social format (like moments ago, One week ago) on passing time in milliseconds
     * @param time
     * @return
     */
    public String socialFormat(long time){
        PrettyTime pretty = new PrettyTime();
        Date date = new Date(time);
        return pretty.format(date);
    }
}
