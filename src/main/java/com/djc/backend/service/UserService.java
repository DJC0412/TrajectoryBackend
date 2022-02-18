package com.djc.backend.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

public interface UserService {
    String queryUserById(String id);
    String queryAllUsers();
    ArrayList<JSONObject> getUserODs();
    ArrayList<JSONObject> getUserTrajectoryById(String id);
    ArrayList<Object> getUserTrajectoryByRegex(String id,String searchNum);
    JSONObject getOneTrajectoryByTraId(String trajId);
    List<JSONObject> getUserTrajectoryByIdInChunk(String id,Integer ChunkSize,Integer ChunkNum);
    ArrayList<JSONObject> getUserHistoryTrajectory(String trajId,int days);
    JSONObject getUserTrajectoryByIdAndTime(String id,int[] month,int[] weekday,int[] hour);
    JSONObject getDateTrajCount(String id);
    JSONObject getUserTrajCount(String id);
    List<List> getNodesProp(String id);
    JSONObject getPredictResult(String modelSetting);
}
