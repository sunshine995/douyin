package com.douyin.service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersVo {
    @Id
    private String id;
    private String mobile;
    private String nickname;
    @Column(name = "imooc_num")
    private String imoocNum;
    private String face;
    private Integer sex;

    private Date birthday;

    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    @Column(name = "bg_img")
    private String bgImg;
    @Column(name = "can_imooc_num_be_updated")
    private Integer canImoocNumBeUpdated;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;

    private String userToken; // 用户token传递给前端



    private Integer myFollowsCounts; // 我关注的总数
    private Integer myFansCounts; // 关注我的粉丝总数
    //private Integer myLinkedVlogCounts; //点赞我的视频的总数
    //private Integer totalLinkedMeCounts; // 所有喜欢我的总数
    private Integer totalLikeMeCounts; // 所有喜欢我的总数



}