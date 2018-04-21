package dev.sagar.smsblocker.ux.filterable;

import android.content.Context;
import android.widget.Filter;

import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 08/03/18.
 */

public class  ConversationMapFilter extends Filter {
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Core
    private IndexedHashMap<String, Conversation> conversationMap;
    private Callback callback;

    //Java Android
    private Context context;

    public ConversationMapFilter(Context context, IndexedHashMap<String, Conversation> conversationMap, Callback callback) {
        this.conversationMap = conversationMap;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        String searchStr = charSequence.toString().toLowerCase();
        IndexedHashMap<String, Conversation> filteredConvMap = new IndexedHashMap<>();
        if (searchStr.isEmpty()) {
            filteredConvMap.update(conversationMap);
        } else {
            for (int i=0 ;i<conversationMap.size(); i++) {
                Conversation conv = conversationMap.get(i);
                String addr = conv.getAddress().toLowerCase();
                String displayName = conv.getContactName();
                String body = conv.getBody().toLowerCase();
                String threadId = conv.getThreadId();
                if(displayName != null)
                    displayName = displayName.toLowerCase();
                if(addr.contains(searchStr) ||
                        (displayName!=null && displayName.contains(searchStr)) ||
                        body.contains(searchStr)
                        ){
                    filteredConvMap.put(threadId, conv);
                }
            }
        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredConvMap;
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        final String methodName =  "publishResults()";
        log.justEntered(methodName);

        IndexedHashMap<String, Conversation> filteredConvMap = (IndexedHashMap<String, Conversation>) filterResults.values;
        log.info(methodName, "Filtered ConverList: "+filteredConvMap.size());
        callback.onResultsFiltered(filteredConvMap);

        log.returning(methodName);
    }

    public interface Callback{
        void onResultsFiltered(IndexedHashMap<String, Conversation> mFilteredConvMap);
    }
}
