package dev.sagar.smsblocker.tech.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sagarpawar on 06/02/18.
 */

public class IndexedHashMap<K,V>{
    private Map<K,V> map;
    private ArrayList<V> list;

    public IndexedHashMap(){
        map = new LinkedHashMap<>();
        list = new ArrayList<>();
    }

    /**
     * This method put value in map
     * @param key key for map
     * @param value value for key
     * @return if updated then [old_position, new_position] else if new entry [-1, new_position]
     */
    public PositionLog put(K key, V value){
        int oldPosition = -1;
        int newPosition = -1;
        if(map.containsKey(key)) {
            V tValue = map.get(key);
            oldPosition = list.indexOf(tValue);
            list.remove(oldPosition);
            map.remove(key);
        }

        map.put(key, value);
        list.add(value);
        newPosition = list.size()-1;

        PositionLog result = new PositionLog(oldPosition, newPosition);
        return result;
    }

    public void update(IndexedHashMap<K, V> newMap){
        map = newMap.getMap();
        list = newMap.getArrayList();
    }

    public void remove(K key){
        V value = map.get(key);
        list.remove(value);
        map.remove(key);
    }

    public V get(int index){
        return list.get(index);
    }

    public V get(K key){
        return map.get(key);
    }

    public int size(){
        return list.size();
    }

    public boolean containsKey(String key){
        return map.containsKey(key);
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    private Map<K,V> getMap(){
        return map;
    }

    private ArrayList<V> getArrayList(){
        return list;
    }
}
