package com.djc.backend.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.djc.backend.entity.User;
import com.djc.backend.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true",originPatterns = "*")//跨域
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation("获取所有用户的大五人格数据")
    @GetMapping("/ocean_score_all")
    public JSONObject oceanScoreAll() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",userService.queryAllUsers());
        jsonObject.put("timestamps",System.currentTimeMillis());
        return jsonObject;
    }

    @ApiOperation("获取单个用户的大五人格数据")
    @GetMapping("/ocean_score_one")
    public String oceanScoreOne(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.queryUserById(id);
    }

    @ApiOperation("获取用户数目")
    @GetMapping("/getUsersNum")
    public Integer getUsersNum(){
        return userService.getUsersNum();
    }

    @ApiOperation("获取轨迹数目")
    @GetMapping("/getTrajNum")
    public Long getTrajNum(){
        return userService.getTrajNum();
    }

    @ApiOperation("获取用户熵均值")
    @GetMapping("/getAverageEntropy")
    public JSONObject getAverageEntropy(){
        return userService.getAverageEntropy();
    }

    @ApiOperation("获取用户人格均值")
    @GetMapping("/getAverageOcean")
    public JSONObject getAverageOcean(){
        return userService.getAverageOcean();
    }

    @ApiOperation("获取用户mean均值")
    @GetMapping("/getAverageMean")
    public JSONObject getAverageMean(){
        return userService.getAverageMean();
    }

    @ApiOperation("获取所有用户的ODs")
    @GetMapping("/getUserODs")
    public ArrayList<JSONObject> getUserODs(){
        return  userService.getUserODs();
    }

    @ApiOperation("获取所有用户的TOP5")
    @GetMapping("/getUsersTopFive")
    public JSONObject getUsersTopFive(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",userService.getUsersTopFive());
        jsonObject.put("timestamps",System.currentTimeMillis());
        return jsonObject;
    }

    @ApiOperation("获取各天各用户出行次数")
    @GetMapping("/getUserTrajNumsByDay")
    public JSONObject getUserTrajNumsByDay(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",userService.getUserTrajNumsByDay());
        jsonObject.put("timestamps",System.currentTimeMillis());
        return jsonObject;
    }

    @ApiOperation("获取单个用户的所有轨迹数据")
    @GetMapping("/getUserTraj")
    public ArrayList<JSONObject> getUserTrajectoryById(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.getUserTrajectoryById(id);
    }

    @ApiOperation("轨迹模糊匹配")
    @GetMapping("/getUserTrajRegex")
    public ArrayList<Object> getUserTrajectoryByRegex(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id,@ApiParam(value = "模糊搜索",required =  true) @RequestParam("searchNum") String searchNum){
        return userService.getUserTrajectoryByRegex(id,searchNum);
    }

    @ApiOperation("获取单个轨迹数据")
    @GetMapping("/getOneTraj")
    public JSONObject getOneTrajectoryByTrajId(@ApiParam(value = "轨迹id",required =  true) @RequestParam("trajId") String trajId){
        return userService.getOneTrajectoryByTraId(trajId);
    }

    @ApiOperation("获取某个轨迹之前的历史轨迹数据")
    @GetMapping("/getUserHistoryTraj")
    public ArrayList<JSONObject> getUserHistoryTrajectory(@ApiParam(value = "轨迹id",required =  true) @RequestParam("trajId") String trajId,int days){
        return userService.getUserHistoryTrajectory(trajId,days);
    }

    @ApiOperation("获取单个用户的分块轨迹数据")
    @GetMapping("/getUserTrajInChunk")
    public List<JSONObject> getUserTrajectoryByIdInChunk(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id,int chunkSize,int chunkNum){
        return userService.getUserTrajectoryByIdInChunk(id,chunkSize,chunkNum);
    }

    @ApiOperation("根据用户id和时间筛选轨迹数据")
    @GetMapping("/getUserTrajByTime")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "monthMin", value = "月份最小值", defaultValue = "0"),
            @ApiImplicitParam(name = "monthMax", value = "月份最大值", defaultValue = "11"),
            @ApiImplicitParam(name = "weekdayMin", value = "星期最小值", defaultValue = "0"),
            @ApiImplicitParam(name = "weekdayMax", value = "星期最大值", defaultValue = "6"),
            @ApiImplicitParam(name = "hourMin", value = "小时最小值", defaultValue = "0"),
            @ApiImplicitParam(name = "hourMax", value = "小时最大值", defaultValue = "23"),
    }
    )
    public JSONObject getUserTrajectoryByIdAndTime(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id,Integer monthMin,Integer monthMax,Integer weekdayMin,Integer weekdayMax,Integer hourMin,Integer hourMax){
        if(monthMin==null)monthMin=0;
        if(monthMax==null)monthMax=11;
        if(weekdayMin==null)weekdayMin=0;
        if(weekdayMax==null)weekdayMax=6;
        if(hourMin==null)hourMin=0;
        if(hourMax==null)hourMax=23;
        int[] month = {monthMin,monthMax};
        int[] weekday = {weekdayMin,weekdayMax};
        int[] hour = {hourMin,hourMax};
        return userService.getUserTrajectoryByIdAndTime(id,month,weekday,hour);
    }

    @ApiOperation("统计时间段内的轨迹计数分布")
    @GetMapping("/getUserTrajectoryCountBetweenDate")
    public JSONObject getUserTrajectoryCountBetweenDate(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id,String startDate, String endDate){
        return userService.getUserTrajectoryCountBetweenDate(id,startDate,endDate);
    }

    @ApiOperation("获取该用户每天的轨迹计数")
    @GetMapping("/getDateTrajCount")
    public JSONObject getDateTrajCount(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.getDateTrajCount(id);
    }

    @ApiOperation("获取该用户的轨迹总数")
    @GetMapping("/getUserTrajCount")
    public JSONObject getUserTrajCount(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.getUserTrajCount(id);
    }

    @ApiOperation("获取该用户各轨迹点的属性")
    @GetMapping("/getNodesProp")
    public List<List> getNodesProp(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.getNodesProp(id);
    }

    @ApiOperation("获取预测结果")
    @GetMapping("/getPredictResult")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelSetting", value = "模型参数", defaultValue = ""),
            @ApiImplicitParam(name = "cutPoint", value = "切割比例", defaultValue = "1"),
    }
    )
    public JSONObject getPredictResult(@ApiParam(value = "轨迹ID") @RequestParam("trajID") String trajID,String cutPoint,String modelSetting){
        return userService.getPredictResult(trajID,cutPoint,modelSetting);
    }
}
