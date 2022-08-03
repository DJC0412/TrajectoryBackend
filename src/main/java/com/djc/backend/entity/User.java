package com.djc.backend.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("大五人格用户实体")
public class User {
    @ApiModelProperty("人员编号")
    private String user_id;
    @ApiModelProperty("每月出行天数")
    private float trips_per_month;
    @ApiModelProperty("居家时间半径")
    private float ratio_stay_time_in_home;
    @ApiModelProperty("总出行距离")
    private float trip_length;
    @ApiModelProperty("时间加权的旋转半径")
    private float rg_time;
    @ApiModelProperty("旋转半径")
    private float rg_quantity;
    @ApiModelProperty("购物")
    private float shopping;
    @ApiModelProperty("娱乐")
    private float recreation;
    @ApiModelProperty("餐饮")
    private float restaurant;
    @ApiModelProperty("非兴趣停留点比率")
    private float ratio_of_uninterested_trips;
    @ApiModelProperty("前四旋转半径比率")
    private float rg4_rg_quantity;
    @ApiModelProperty("k值")
    private float k_quantity;
    @ApiModelProperty("随机熵")
    private float random_entropy;
    @ApiModelProperty("位置熵")
    private float location_entropy;
    @ApiModelProperty("旅行熵")
    private float OD_entropy;
    @ApiModelProperty("序列熵")
    private float sequence_entropy;
    @ApiModelProperty("离家距离熵")
    private float distance_from_home_entropy;
    @ApiModelProperty("速度标准差均值")
    private float speed_std_mean;
    @ApiModelProperty("速度均值标准差")
    private float speed_mean_std;
    @ApiModelProperty("速度标准差最大值")
    private float speed_std_max;
    @ApiModelProperty("加速度标准差最大值")
    private float acceleration_std_max;
    @ApiModelProperty("急变速比率均值")
    private float harsh_shift_ratio_std;
    @ApiModelProperty("急转弯比率均值")
    private float harsh_steering_ratio_std;
    @ApiModelProperty("急变速比率标准差")
    private float harsh_shift_ratio_mean;
    @ApiModelProperty("急转弯比率标准差")
    private float harsh_steering_ratio_mean;
    @ApiModelProperty("速度均值")
    private float speed_mean;
    @ApiModelProperty("速度最大值")
    private float speed_max;
    @ApiModelProperty("超速比率均值")
    private float over_speed_ratio;
    @ApiModelProperty("超速数量均值")
    private float over_speed_quantity;
    @ApiModelProperty("路口超速数量均值")
    private float junction_over_speed;
    @ApiModelProperty("路口速度均值")
    private float junction_speed_mean;
    @ApiModelProperty("日内节律熵")
    private float day_entropy;
    @ApiModelProperty("节律熵")
    private float datetime_entropy;
    @ApiModelProperty("外向性")
    private float extroversion;
    @ApiModelProperty("开放性")
    private float openness;
    @ApiModelProperty("神经质性")
    private float neuroticism;
    @ApiModelProperty("尽责性")
    private float conscientiousness;

    @Override
    public String toString() {
        return "{" +
                "人员编号:" + user_id +
                ", 每月出行天数:" + trips_per_month +
                ", 居家时间半径:" + ratio_stay_time_in_home +
                ", 总出行距离:" + trip_length +
                ", 时间加权的旋转半径:" + rg_time +
                ", 旋转半径:" + rg_quantity +
                ", 购物:" + shopping +
                ", 娱乐:" + recreation +
                ", 餐饮:" + restaurant +
                ", 非兴趣停留点比率:" + ratio_of_uninterested_trips +
                ", 前四旋转半径比率:" + rg4_rg_quantity +
                ", k值:" + k_quantity +
                ", 随机熵:" + random_entropy +
                ", 位置熵:" + location_entropy +
                ", 旅行熵:" + OD_entropy +
                ", 序列熵:" + sequence_entropy +
                ", 离家距离熵:" + distance_from_home_entropy +
                ", 速度标准差均值:" + speed_std_mean +
                ", 速度均值标准差:" + speed_mean_std +
                ", 速度标准差最大值:" + speed_std_max +
                ", 加速度标准差最大值:" + acceleration_std_max +
                ", 急变速比率均值:" + harsh_shift_ratio_std +
                ", 急转弯比率均值:" + harsh_steering_ratio_std +
                ", 急变速比率标准差:" + harsh_shift_ratio_mean +
                ", 急转弯比率标准差:" + harsh_steering_ratio_mean +
                ", 速度均值:" + speed_mean +
                ", 速度最大值:" + speed_max +
                ", 超速比率均值:" + over_speed_ratio +
                ", 超速数量均值:" + over_speed_quantity +
                ", 路口超速数量均值:" + junction_over_speed +
                ", 路口速度均值:" + junction_speed_mean +
                ", 日内节律熵:" + day_entropy +
                ", 节律熵:" + datetime_entropy +
                ", 外向性:" + extroversion +
                ", 开放性:" + openness +
                ", 神经质性:" + neuroticism +
                ", 尽责性:" + conscientiousness +
                '}';
    }
}
