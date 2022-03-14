package com.djc.backend.service;

import com.alibaba.fastjson.JSONObject;
import com.djc.backend.dao.UserDaoImpl;
import com.djc.backend.entity.User;
import com.djc.backend.entity.UserTrajectory;
import com.djc.backend.mapper.mysql.MySQLMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService{

    @Autowired
    private MySQLMapper mySQLMapper;

    @Autowired
    private UserDaoImpl userDaoMongoDB;

    private String chunkUserId;
    private ArrayList<JSONObject> chunkUserTrajs;

    @Override
    public String queryUserById(String id) {
        return JSONObject.parseObject(mySQLMapper.queryUserById(id).toString()).toString();
    }

    @Override
    public String queryAllUsers(){
        List<User> allUsers = mySQLMapper.queryAllUsers();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < allUsers.size(); i++) {
            jsonObject.put(String.valueOf(i),JSONObject.parseObject(allUsers.get(i).toString()));
        }
        return jsonObject.toString();
    }

    @Override
    public Integer getUsersNum() {
        return mySQLMapper.getUsersNum();
    }

    @Override
    public Map<String, Object> getUsersAVG() {
        return mySQLMapper.getUsersAVG();
    }

    @Override
    public JSONObject getAverageEntropy() {
        Map<String, Object> userAVG=getUsersAVG();
        JSONObject entropyJson=new JSONObject();
        entropyJson.put("随机熵",userAVG.get("random_entropy"));
        entropyJson.put("时间无关熵",userAVG.get("uncorrelated_entropy"));
        entropyJson.put("真实熵",userAVG.get("real_entropy"));
        entropyJson.put("离家距离熵",userAVG.get("distance_from_home_entropy"));
        entropyJson.put("旅行熵",userAVG.get("OD_entropy"));
        entropyJson.put("日内节律熵",userAVG.get("day_entropy"));
        entropyJson.put("节律熵",userAVG.get("datetime_entropy"));
        return entropyJson;
    }

    @Override
    public JSONObject getAverageOcean() {
        Map<String, Object> userAVG=getUsersAVG();
        JSONObject oceanJson=new JSONObject();
        oceanJson.put("外向性",userAVG.get("extroversion"));
        oceanJson.put("开放性",userAVG.get("openness"));
        oceanJson.put("神经质性",userAVG.get("neuroticism"));
        oceanJson.put("尽责性",userAVG.get("conscientiousness"));
        return oceanJson;
    }

    @Override
    public JSONObject getAverageMean() {
        Map<String, Object> userAVG=getUsersAVG();
        JSONObject meanJson=new JSONObject();
        meanJson.put("休闲地理语义均值",userAVG.get("entertainment_prob"));
        meanJson.put("餐饮地理语义均值",userAVG.get("restaurant_prob"));
        meanJson.put("休闲POI数量均值",userAVG.get("entertainment_num"));
        meanJson.put("餐饮POI数量均值",userAVG.get("restaurant_num"));
        meanJson.put("速度标准差均值",userAVG.get("speed_std_mean"));
        meanJson.put("加速度标准差均值",userAVG.get("acceleration_std_mean"));
        meanJson.put("急变速比率均值",userAVG.get("harsh_shift_ratio_std"));
        meanJson.put("急转弯比率均值",userAVG.get("harsh_steering_ratio_std"));
        meanJson.put("速度均值",userAVG.get("speed_mean"));
        meanJson.put("超速比率均值",userAVG.get("over_speed_ratio"));
        meanJson.put("超速数量均值",userAVG.get("over_speed_quantity"));
        meanJson.put("路口超速数量均值",userAVG.get("junction_over_speed"));
        meanJson.put("路口速度均值",userAVG.get("junction_speed_mean"));
        return meanJson;
    }

    @Override
    public Long getTrajNum() {
        return userDaoMongoDB.getTrajNum();
    }

    @Override
    public ArrayList<JSONObject> getUserODs() {
        List<UserTrajectory> userOds_Mongo= userDaoMongoDB.getUserODs();
        Map<String,List<UserTrajectory>> userMap = userOds_Mongo.stream().collect(Collectors.groupingBy(UserTrajectory::getUserid));
        ArrayList<JSONObject> usersOds = new ArrayList<>();
        userMap.forEach((key, value) -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",key);
            ArrayList<JSONObject> userOds = new ArrayList<>();
                value.forEach((traj)->{
                    JSONObject ODobject = new JSONObject();
                    ODobject.put("O",traj.getOrigin());
                    ODobject.put("D",traj.getDestination());
                    userOds.add(ODobject);
                });
                jsonObject.put("ODs",userOds);
            usersOds.add(jsonObject);
        });
        return usersOds;
    }

    @Override
    public ArrayList<JSONObject> getUserTrajectoryById(String id) {
        List<Document> userTrajs_Mongo = userDaoMongoDB.getUserTrajectoryById(id);
        ArrayList<JSONObject> userTrajs = new ArrayList<>();
        userTrajs_Mongo.forEach((traj)-> userTrajs.add(Doc2Json(traj)));
        return userTrajs;
    }

    @Override
    public ArrayList<Object> getUserTrajectoryByRegex(String id, String searchNum) {
        List<Document> userTrajsRegex_Mongo = userDaoMongoDB.getUserTrajectoryByRegex(id,searchNum);
        ArrayList<Object> RegexTrajs = new ArrayList<>();
        userTrajsRegex_Mongo.forEach((traj)-> RegexTrajs.add(traj.get("_id")));
        return RegexTrajs;
    }

    @Override
    public JSONObject getOneTrajectoryByTraId(String trajId) {
        Document findTraj = userDaoMongoDB.getOneTrajectoryByTraId(trajId);
        return Doc2Json(findTraj);
    }

    private JSONObject Doc2Json(Document trajDoc) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", trajDoc.get("_id"));
        jsonObject.put("userid", trajDoc.get("userid"));
        jsonObject.put("date", trajDoc.get("date"));
        jsonObject.put("month", trajDoc.get("month"));
        jsonObject.put("hour", trajDoc.get("hour"));
        jsonObject.put("time", trajDoc.get("time"));
        jsonObject.put("weekday", trajDoc.get("weekday"));
        jsonObject.put("origin", trajDoc.get("origin"));
        jsonObject.put("destination", trajDoc.get("destination"));
        jsonObject.put("lats", trajDoc.get("lats"));
        jsonObject.put("lngs", trajDoc.get("lngs"));
        jsonObject.put("spd", trajDoc.get("spd", ArrayList.class));
        jsonObject.put("azimuth", trajDoc.get("azimuth",ArrayList.class));
        jsonObject.put("dis", trajDoc.get("travel_dis",ArrayList.class));
        jsonObject.put("disTotal", trajDoc.get("dis_total"));
        return jsonObject;
    }

    @Override
    public List<JSONObject> getUserTrajectoryByIdInChunk(String id, Integer ChunkSize, Integer ChunkNum) {
        if(!Objects.equals(chunkUserId, id)){
//            System.out.println(chunkUserId);
            chunkUserId=id;
            chunkUserTrajs=getUserTrajectoryById(id);
        }
        int startIndex = Math.min(ChunkNum*ChunkSize, chunkUserTrajs.size());
        int endIndex = Math.min((ChunkNum+1)*ChunkSize, chunkUserTrajs.size());
        return chunkUserTrajs.subList(startIndex,endIndex);
    }

    @Override
    public ArrayList<JSONObject> getUserHistoryTrajectory(String trajId, int days) {
        List<Document> userTrajs_Mongo = userDaoMongoDB.getUserHistoryTrajectory(trajId,days);
        ArrayList<JSONObject> userTrajs = new ArrayList<>();
        userTrajs_Mongo.forEach((traj)-> userTrajs.add(Doc2Json(traj)));
        return userTrajs;
    }

    @Override
    public JSONObject getUserTrajectoryByIdAndTime(String id, int[] month, int[] weekday, int[] hour) {
        List<Document> userTrajsFBT_Mongo = userDaoMongoDB.getUserTrajectoryByIdAndTime(id,month,weekday,hour);//FBT means filter by time
        JSONObject userTrajsFBT = new JSONObject();
        int[] monthCount=new int[12];
        int[] weekdayCount=new int[7];
        int[] hourCount=new int[24];
        userTrajsFBT_Mongo.forEach((traj)->{
            monthCount[(int) traj.get("month")]++;
            weekdayCount[(int) traj.get("weekday")]++;
            hourCount[(int) traj.get("hour")]++;
        });
        userTrajsFBT.put("monthCount",monthCount);
        userTrajsFBT.put("weekdayCount",weekdayCount);
        userTrajsFBT.put("hourCount",hourCount);
        return userTrajsFBT;
    }

    @Override
    public JSONObject getUserTrajectoryCountBetweenDate(String id, String startDate, String endDate) {
        List<Document> userTrajsBD_Mongo = userDaoMongoDB.getUserTrajectoryBetweenDate(id,startDate,endDate);
        JSONObject userTrajsBD = new JSONObject();
        int[][] Count=new int[7][24];
        userTrajsBD_Mongo.forEach((traj)->{
//            System.out.println(traj);
            Count[(int) traj.get("weekday")][(int) traj.get("hour")]++;
        });
        userTrajsBD.put("MondayHourCount",Count[0]);
        userTrajsBD.put("TuesdayHourCount",Count[1]);
        userTrajsBD.put("WednesdayHourCount",Count[2]);
        userTrajsBD.put("ThursdayHourCount",Count[3]);
        userTrajsBD.put("FridayHourCount",Count[4]);
        userTrajsBD.put("SaturdayHourCount",Count[5]);
        userTrajsBD.put("SundayHourCount",Count[6]);
        return userTrajsBD;
    }

    @Override
    public JSONObject getDateTrajCount(String id) {
        JSONObject trajCountJSON = new JSONObject();
        List<JSONObject> trajCount = userDaoMongoDB.getDateTrajCount(id);
        for (JSONObject dateObject : trajCount) {
            JSONObject CountJSON = new JSONObject();
            CountJSON.put("count",dateObject.getIntValue("TrajCount"));
            trajCountJSON.put(dateObject.getString("_id"),CountJSON);
        }
        return trajCountJSON;
    }

    @Override
    public JSONObject getUserTrajCount(String id) {
        JSONObject trajCountJSON = new JSONObject();
        List<JSONObject> trajCount = userDaoMongoDB.getUserTrajCount(id);
        for (JSONObject UserObject : trajCount) {
            trajCountJSON.put(id,UserObject.getIntValue("TrajCount"));
//            System.out.println(trajCountJSON);
        }
        return trajCountJSON;
    }

    @Override
    public List<List> getNodesProp(String id) {
        List<Document> trajs= userDaoMongoDB.getUserTrajectoryById(id);
        List<List> list = new ArrayList<>();
        for (Document traj : trajs) {
            List<Double> lats= traj.getList("lats",Double.class);
            List<Double> lngs= traj.getList("lngs",Double.class);
            List<Double> spd= traj.getList("spd",Double.class);
            if(lngs.size()==lats.size()&&lngs.size()==spd.size()){
                for (int i = 0; i < lats.size(); i++) {
                    double[] nodeProp = {lngs.get(i),lats.get(i),spd.get(i)};
                    list.add(Collections.singletonList(nodeProp));
                }
            }
        }
        return list;
    }

    private JSONObject remoteCall(String modelSetting){
        // 访问服务进程的套接字
        try (Socket socket = new Socket("192.168.61.60", 2474)) {
            // 初始化套接字，设置访问服务的主机和进程端口号，HOST是访问python进程的主机名称，可以是IP地址或者域名，PORT是python进程绑定的端口号
            // 获取输出流对象
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);
            // 发送内容
            out.print(modelSetting);
            // 告诉服务进程，内容发送完毕，可以开始处理
            out.print("over");
            // 获取服务进程的输入流
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String tmp = null;
            StringBuilder sb = new StringBuilder();
            // 读取内容
            while ((tmp = br.readLine()) != null)
                sb.append(tmp);
            // 解析结果
            return JSONObject.parseObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("远程接口调用结束.");
        }
        return null;
    }
    @Override
    public JSONObject getPredictResult(String trajID,String cutPoint,String modelSetting) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("trajID", trajID);
        jsonObject.put("cutPoint", cutPoint);
        return remoteCall(jsonObject.toJSONString());
    }


}
