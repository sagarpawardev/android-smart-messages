package dev.sagar.smsblocker.tech.exceptions;

/**
 * Created by sagarpawar on 22/11/17.
 */

public class NotImplementedException extends Exception{

    private String className=null,
            methodName=null;
    public NotImplementedException(String className, String methodName){
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public String getMessage() {
        return methodName +" Not Impelemented In "+className;
    }
}
