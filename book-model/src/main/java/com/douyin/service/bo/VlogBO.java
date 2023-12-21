package com.douyin.service.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class VlogBO {

    private String id;
    private String vlogerId;
    private String url;
    private String cover;
    private String title;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
//    @Column(name = "is_private")
//    private Integer isPrivate;
//    @Column(name = "created_time")
//    private Date createdTime;
//
//    @Column(name = "updated_time")
//    private Date updatedTime;

}