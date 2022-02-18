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
    public String oceanScoreAll() throws JSONException {
        return  userService.queryAllUsers();
    }

    @ApiOperation("获取单个用户的大五人格数据")
    @GetMapping("/ocean_score_one")
    public String oceanScoreOne(@ApiParam(value = "用户id",required =  true) @RequestParam("id") String id){
        return userService.queryUserById(id);
    }

    @ApiOperation("获取所有用户的ODs")
    @GetMapping("/getUserODs")
    public ArrayList<JSONObject> getUserODs(){
        return  userService.getUserODs();
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
    public JSONObject getPredictResult(@ApiParam(value = "模型参数",required =  true) @RequestParam("modelSetting") String modelSetting){
        return userService.getPredictResult(modelSetting);
    }
}
