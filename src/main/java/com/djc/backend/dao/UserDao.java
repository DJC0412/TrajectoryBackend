package com.djc.backend.dao;

import com.alibaba.fastjson.JSONObject;
import com.djc.backend.entity.UserTrajectory;
import org.bson.Document;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface UserDao {
    List<UserTrajectory> getUserODs();
    Long getTrajNum();
    List<Document> getUserTrajectoryById(String id);
    List<Document> getUsersTopFive();
    List<Document> getUserTrajectoryByRegex(String id,String searchNum);
    Document getOneTrajectoryByTraId(String trajId);
    List<Document> getUserTrajectoryByIdAndTime(String id,int[] month,int[] weekday,int[] hour);
    List<Document> getUserTrajectoryBetweenDate(String id,String startDate,String endDate);
    List<Document> getUserHistoryTrajectory(String trajId,int days) throws ParseException;
    List<JSONObject> getDateTrajCount(String id);
    List<JSONObject> getUserTrajNumsByDay();
    List<JSONObject> getUserTrajCount(String id);
}
