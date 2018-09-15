package com.pillsgt.pgt.managers;

import java.util.HashMap;
import java.util.Map;

public class CronManager {
    public static HashMap<Integer, Integer> getTypePositions() {
        HashMap<Integer, Integer> typePositions = new HashMap<>();

        typePositions.put(0, 1);//before_eat
        typePositions.put(1, 2);//after_eat
        typePositions.put(2, 3);//while_eating
        typePositions.put(3, 100);//others

        return typePositions;
    }


    public static Integer getTypePosition(Integer key) {
        Integer position = -1;
        HashMap<Integer, Integer> typeMap = getTypePositions();
        for(Map.Entry<Integer, Integer> e : typeMap.entrySet()) {
            if ( key == e.getValue() ){
                position = e.getKey();
            }
        }
        return position;
    }


    public static Integer getTypeByPosition(Integer position) {
        Integer result = 100;//default value: others
        HashMap<Integer, Integer> map = getTypePositions();
        if(map.containsKey( position )){
            result = map.get( position );
        }
        return result;

    }

    public static HashMap<Integer, Integer> getIntervalPositions() {
        HashMap<Integer, Integer> intervalPositions = new HashMap<>();

        intervalPositions.put(0, 1);//once_a_day
        intervalPositions.put(1, 2);//twice_a_day
        intervalPositions.put(2, 3);//three_times_a_day
        intervalPositions.put(3, 4);//four_times_a_day
        intervalPositions.put(4, 5);//five_times_a_day
        intervalPositions.put(5, 6);//six_times_a_day

        intervalPositions.put(6, 21);//every_30_minutes
        intervalPositions.put(7, 22);//every_1_hour
        intervalPositions.put(8, 23);//every_2_hours
        intervalPositions.put(9, 24);//every_3_hours
        intervalPositions.put(10, 25);//every_4_hours
        intervalPositions.put(11, 26);//every_5_hours
        intervalPositions.put(12, 27);//every_5_hours

        intervalPositions.put(13, 100);//others

        return intervalPositions;
    }

    public static Integer getIntervalPosition(Integer key) {
        Integer position = -1;
        HashMap<Integer, Integer> intervalMap = getIntervalPositions();
        for(Map.Entry<Integer, Integer> e : intervalMap.entrySet()) {
            if ( key == e.getValue() ){
                position = e.getKey();
            }
        }
        return position;
    }

    public static Integer getIntervalByPosition(Integer position) {
        Integer result = 100;//default value: others
        HashMap<Integer, Integer> map = getIntervalPositions();
        if(map.containsKey( position )){
            result = map.get( position );
        }
        return result;
    }

}
