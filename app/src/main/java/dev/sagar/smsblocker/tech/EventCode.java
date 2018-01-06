package dev.sagar.smsblocker.tech;

import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSDeliveredReceiver;
import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSReceivedReceiver;
import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSSentReceiver;

/**
 * Created by sagarpawar on 07/01/18.
 */

public class EventCode {
    public static final String LOCAL_SMS_RECEIVED = LocalSMSReceivedReceiver.EVENT_RECEIVED;
    public static final String LOCAL_SMS_SENT = LocalSMSSentReceiver.EVENT_SENT;
    public static final String LOCAL_SMS_DELIVERED = LocalSMSDeliveredReceiver.EVENT_DELIVERED;

}
