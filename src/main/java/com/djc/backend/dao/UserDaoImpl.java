package com.djc.backend.dao;

import com.alibaba.fastjson.JSONObject;
import com.djc.backend.entity.UserTrajectory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class UserDaoImpl implements UserDao{

    public static String collectionName="simplifyData50";//选用的集合名称

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<UserTrajectory> getUserODs() {
        Query query = new Query();
        query.fields().include("userid","origin","destination");
        return mongoTemplate.find(query,UserTrajectory.class,collectionName);
    }

    @Override
    public Long getTrajNum() {
        return mongoTemplate.getCollection(collectionName).estimatedDocumentCount();
    }

    @Override
    public List<Document> getUserTrajectoryById(String id) {
        List<Document> list = new ArrayList<>();
        Query query = new Query(Criteria
                .where("userid").is(id));
//        return mongoTemplate.find(query,UserTrajectory.class);
        for (Document next : mongoTemplate.getCollection(collectionName).
                find(query.getQueryObject())) {
            list.add(next);
        }
        return list;
    }

    @Override
    public List<Document> getUsersTopFive() {
        List<Document> list = new ArrayList<>();
        Query query = new Query();
        for (Document next : mongoTemplate.getCollection("userTop5").
                find(query.getQueryObject())) {
            list.add(next);
        }
        return list;
    }

    @Override
    public List<Document> getUserTrajectoryByRegex(String id,String searchNum) {
        List<Document> list = new ArrayList<>();
        Query query = new Query(Criteria
                .where("_id").regex("^"+id+"_.*"+searchNum+".*$"));
        for (Document next : mongoTemplate.getCollection(collectionName).
                find(query.getQueryObject())) {
            list.add(next);
        }
        return list;
    }

    @Override
    public Document getOneTrajectoryByTraId(String trajId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(trajId));
        MongoCursor<Document> cursor= mongoTemplate.getCollection(collectionName).
                find(query.getQueryObject())
                .iterator();
        return cursor.next();
    }

    @Override
    public List<Document> getUserTrajectoryByIdAndTime(String id, int[] month,int[] weekday,int[] hour) {
        List<Document> list = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(id));
        query.addCriteria(Criteria.where("month").gte(month[0]).lte(month[1]));
        query.addCriteria(Criteria.where("weekday").gte(weekday[0]).lte(weekday[1]));
        query.addCriteria(Criteria.where("hour").gte(hour[0]).lte(hour[1]));
        for (Document next : mongoTemplate.getCollection(collectionName).
                find(query.getQueryObject())) {
            list.add(next);
        }
        return list;
    }

    @Override
    public List<Document> getUserTrajectoryBetweenDate(String id, String startDate, String endDate) {
        List<Document> list = new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        if(startDate==null || endDate==null) return list;
        try {
            Date end=sdf.parse(endDate);
            Date start=sdf.parse(startDate);
            Query query = new Query();
            query.addCriteria(Criteria.where("userid").is(id));
            query.addCriteria(Criteria.where("date").gte(sdf.format(start)).lte(sdf.format(end)));
            for (Document next : mongoTemplate.getCollection(collectionName).
                    find(query.getQueryObject())) {
                list.add(next);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Document> getUserHistoryTrajectory(String trajId, int days) {
        List<Document> list = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(trajId));
        MongoCursor<Document> cursor= mongoTemplate.getCollection(collectionName).
                find(query.getQueryObject())
                .iterator();
        Document findTraj = cursor.next();
        String userid = (String) findTraj.get("userid");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date endDate=sdf.parse((String) findTraj.get("date"));
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(endDate);
            rightNow.add(Calendar.DAY_OF_YEAR,-1*days);
            Date startDate=rightNow.getTime();
            Query historyQuery = new Query();
            historyQuery.addCriteria(Criteria.where("userid").is(userid));
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("date").gte(sdf.format(startDate)).lt(sdf.format(endDate)),
                    Criteria.where("date").is(sdf.format(endDate)).and("time").lt(findTraj.get("time")));
            historyQuery.addCriteria(criteria);
            for (Document next : mongoTemplate.getCollection(collectionName).
                    find(historyQuery.getQueryObject())) {
                list.add(next);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<JSONObject> getDateTrajCount(String id) {
        List<JSONObject> list = new ArrayList<>();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userid").is(id)),
                Aggregation.group("date").count().as("TrajCount"));
        AggregationResults<BasicDBObject> trajCount = mongoTemplate.aggregate(aggregation,collectionName,BasicDBObject.class);
        for (DBObject object : trajCount) {
            list.add(JSONObject.parseObject(object.toString()));
        }
        return list;
    }

    @Override
    public List<JSONObject> getUserTrajNumsByDay() {
        List<JSONObject> list = new ArrayList<>();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("date").push("userid").as("userid"));
        AggregationResults<BasicDBObject> trajCount = mongoTemplate.aggregate(aggregation,collectionName,BasicDBObject.class);
        for (DBObject object : trajCount) {
            list.add(JSONObject.parseObject(object.toString()));
        }
        return list;
    }

    @Override
    public List<JSONObject> getUserTrajCount(String id) {
        List<JSONObject> list = new ArrayList<>();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userid").is(id)),
                Aggregation.count().as("TrajCount"));
        AggregationResults<BasicDBObject> trajCount = mongoTemplate.aggregate(aggregation,collectionName,BasicDBObject.class);
        for (DBObject object : trajCount) {
            list.add(JSONObject.parseObject(object.toString()));
        }
        return list;
    }
}
