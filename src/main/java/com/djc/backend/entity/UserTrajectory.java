package com.djc.backend.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;


@Data
@Document("user")
public class UserTrajectory {
    @Id
    private String _id;
    private String userid;
    private String date;
    private String time;
    private int month;
    private int weekday;
    private int hour;
    private float dis_total;
    private ArrayList<?> origin;
    private ArrayList<?> destination;
    private ArrayList<?> lats;
    private ArrayList<?> lngs;
    private ArrayList<?> spd;
    private ArrayList<?> travel_dis;
    private ArrayList<?> azimuth;

}
