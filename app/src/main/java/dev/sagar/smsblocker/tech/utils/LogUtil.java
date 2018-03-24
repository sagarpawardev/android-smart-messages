package dev.sagar.smsblocker.tech.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

/**
 * Created by sagarpawar on 28/10/17.
 */

public class LogUtil {
    private String className;

    public LogUtil(String className) {
        this.className = className;
    }

    /**
     * This method logs an Error
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void error(String method, String msg){
        Log.e(className, method+" ==> "+msg);
    }

    /**
     * This method logs an Error
     * @param method Current Method Name
     * @param ex Message to Log
     */
    public void error(String method, Exception ex){
        Log.e(className, method+" ==> "+ex.getMessage());

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File (root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "SmartMessages.txt");

        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            pw.append("\n\n\n\n------ Got Exception at "+ts+"--------\n");
            StringWriter sw = new StringWriter();
            PrintWriter pw2 = new PrintWriter(sw);
            ex.printStackTrace(pw2);
            String sStackTrace = sw.toString();
            pw.append(sStackTrace);

            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("SGR Logtest", "Gone to file not found exception: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("SGR Logtest", "Gone to IO Exception");
        }
        Log.i("SGR Logtest", "Log written to file");
    }


    /**
     * This method logs an Information
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void info(String method, String msg){
        Log.i(className, method+" ==> "+msg);
    }


    /**
     * This method logs a Debug
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void debug(String method, String msg){
        Log.d(className, method+" ==> "+msg);
    }


    /**
     * This method logs a Verbose
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void verbose(String method, String msg){
        Log.v(className, method+" ==> "+msg);
    }


    /**
     * This method print Returning.. in log
     * @param method Current Method Name
     */
    public void returning(String method){
        debug(method, "Returning..");
    }


    /**
     * This method print Just Entered.. in log
     * @param method Current Method Name
     */
    public void justEntered(String method){
        debug(method, "Just Entered..");
    }
}
