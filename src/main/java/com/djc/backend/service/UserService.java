package com.djc.backend.service;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface UserService {
    String queryUserById(String id);
    String queryAllUsers();
    Integer getUsersNum();
    Map<String, Object> getUsersAVG();
    JSONObject getAverageEntropy();
    JSONObject getAverageOcean();
    JSONObject getAverageMean();
    Long getTrajNum();
    ArrayList<JSONObject> getUserODs();
    ArrayList<JSONObject> getUserTrajectoryById(String id);
    ArrayList<Object> getUserTrajectoryByRegex(String id,String searchNum);
    JSONObject getOneTrajectoryByTraId(String trajId);
    List<JSONObject> getUserTrajectoryByIdInChunk(String id,Integer ChunkSize,Integer ChunkNum);
    ArrayList<JSONObject> getUserHistoryTrajectory(String trajId,int days);
    JSONObject getUserTrajectoryByIdAndTime(String id,int[] month,int[] weekday,int[] hour);
    JSONObject getUserTrajectoryCountBetweenDate(String id, String startDate, String endDate);
    JSONObject getDateTrajCount(String id);
    JSONObject getUserTrajCount(String id);
    List<List> getNodesProp(String id);
    JSONObject getPredictResult(String trajID,String cutPoint,String modelSetting );
}
