package com.douyin.service.bo;

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
public class UpdatedUserBo {
    @Id
    private String id;
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


}