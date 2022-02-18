package com.djc.backend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.net.UnknownHostException;
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
        userTrajs_Mongo.forEach((traj)->{
            userTrajs.add(Doc2Json(traj));
        });
        return userTrajs;
    }

    @Override
    public ArrayList<Object> getUserTrajectoryByRegex(String id, String searchNum) {
        List<Document> userTrajsRegex_Mongo = userDaoMongoDB.getUserTrajectoryByRegex(id,searchNum);
        ArrayList<Object> RegexTrajs = new ArrayList<>();
        userTrajsRegex_Mongo.forEach((traj)->{
            RegexTrajs.add(traj.get("_id"));
        });
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
            System.out.println(chunkUserId);
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
        userTrajs_Mongo.forEach((traj)->{
            userTrajs.add(Doc2Json(traj));
        });
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
            System.out.println(trajCountJSON);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("modelSetting", modelSetting);
        String str = jsonObject.toJSONString();
        // 访问服务进程的套接字
        Socket socket = null;
        try {
            // 初始化套接字，设置访问服务的主机和进程端口号，HOST是访问python进程的主机名称，可以是IP地址或者域名，PORT是python进程绑定的端口号
            socket = new Socket("192.168.61.60", 2474);
            // 获取输出流对象
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);
            // 发送内容
            out.print(str);
            // 告诉服务进程，内容发送完毕，可以开始处理
            out.print("over");
            // 获取服务进程的输入流
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String tmp = null;
            StringBuilder sb = new StringBuilder();
            // 读取内容
            while((tmp=br.readLine())!=null)
                sb.append(tmp);
            // 解析结果
            JSONObject res = new JSONObject();
            res.put("result",sb.toString());
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {if(socket!=null) socket.close();} catch (IOException e) {}
            System.out.println("远程接口调用结束.");
        }
        return null;
    }
    @Override
    public JSONObject getPredictResult(String modelSetting) {
        return remoteCall(modelSetting);
    }


}
