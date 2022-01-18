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

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<UserTrajectory> getUserODs() {
        Query query = new Query();
        query.fields().include("userid","origin","destination");
        return mongoTemplate.find(query,UserTrajectory.class,"simplifyData");
    }

    @Override
    public List<Document> getUserTrajectoryById(String id) {
        List<Document> list = new ArrayList<>();
        Query query = new Query(Criteria
                .where("userid").is(id));
//        return mongoTemplate.find(query,UserTrajectory.class);
        MongoCursor<Document> cursor= mongoTemplate.getCollection("simplifyData50").
                find(query.getQueryObject())
                .iterator();
        while (cursor.hasNext()){
            Document next = cursor.next();
            list.add(next);
        }
        return list;
    }

    @Override
    public Document getOneTrajectoryByTraId(String trajId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(trajId));
        MongoCursor<Document> cursor= mongoTemplate.getCollection("simplifyData").
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
        MongoCursor<Document> cursor= mongoTemplate.getCollection("simplifyData").
                find(query.getQueryObject())
                .iterator();
        while (cursor.hasNext()){
            Document next = cursor.next();
            list.add(next);
        }
        return list;
    }

    @Override
    public List<Document> getUserHistoryTrajectory(String trajId, int days) {
        List<Document> list = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(trajId));
        MongoCursor<Document> cursor= mongoTemplate.getCollection("simplifyData").
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
            MongoCursor<Document> historyCursor= mongoTemplate.getCollection("simplifyData").
                    find(historyQuery.getQueryObject())
                    .iterator();
            while (historyCursor.hasNext()){
                Document next = historyCursor.next();
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
        AggregationResults<BasicDBObject> trajCount = mongoTemplate.aggregate(aggregation,"simplifyData",BasicDBObject.class);
        for(Iterator<BasicDBObject> iterator = trajCount.iterator();iterator.hasNext();){
            DBObject object = iterator.next();
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
        AggregationResults<BasicDBObject> trajCount = mongoTemplate.aggregate(aggregation,"simplifyData",BasicDBObject.class);
        for(Iterator<BasicDBObject> iterator = trajCount.iterator();iterator.hasNext();){
            DBObject object = iterator.next();
            list.add(JSONObject.parseObject(object.toString()));
        }
        return list;
    }
}
