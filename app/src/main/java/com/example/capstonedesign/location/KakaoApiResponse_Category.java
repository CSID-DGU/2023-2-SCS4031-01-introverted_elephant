package com.example.capstonedesign.location;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.List;

public class KakaoApiResponse_Category implements Serializable {
  private List<Document> documents;

  private Meta meta;

  public List<Document> getDocuments() {
    return this.documents;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  public Meta getMeta() {
    return this.meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }

  public static class Document implements Serializable {
    private String place_url;

    private String place_name;

    private String road_address_name;

    private String category_group_name;

    private String category_name;

    private String distance;

    private String phone;

    private String category_group_code;

    private String x;

    private String y;

    private String address_name;

    private String id;

    public String getPlace_url() {
      return this.place_url;
    }

    public void setPlace_url(String place_url) {
      this.place_url = place_url;
    }

    public String getPlace_name() {
      return this.place_name;
    }

    public void setPlace_name(String place_name) {
      this.place_name = place_name;
    }

    public String getRoad_address_name() {
      return this.road_address_name;
    }

    public void setRoad_address_name(String road_address_name) {
      this.road_address_name = road_address_name;
    }

    public String getCategory_group_name() {
      return this.category_group_name;
    }

    public void setCategory_group_name(String category_group_name) {
      this.category_group_name = category_group_name;
    }

    public String getCategory_name() {
      return this.category_name;
    }

    public void setCategory_name(String category_name) {
      this.category_name = category_name;
    }

    public String getDistance() {
      return this.distance;
    }

    public void setDistance(String distance) {
      this.distance = distance;
    }

    public String getPhone() {
      return this.phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }

    public String getCategory_group_code() {
      return this.category_group_code;
    }

    public void setCategory_group_code(String category_group_code) {
      this.category_group_code = category_group_code;
    }

    public String getX() {
      return this.x;
    }

    public void setX(String x) {
      this.x = x;
    }

    public String getY() {
      return this.y;
    }

    public void setY(String y) {
      this.y = y;
    }

    public String getAddress_name() {
      return this.address_name;
    }

    public void setAddress_name(String address_name) {
      this.address_name = address_name;
    }

    public String getId() {
      return this.id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class Meta implements Serializable {
    private Integer total_count;

    private Boolean is_end;

    private Integer pageable_count;

    private Object same_name;

    public Integer getTotal_count() {
      return this.total_count;
    }

    public void setTotal_count(Integer total_count) {
      this.total_count = total_count;
    }

    public Boolean getIs_end() {
      return this.is_end;
    }

    public void setIs_end(Boolean is_end) {
      this.is_end = is_end;
    }

    public Integer getPageable_count() {
      return this.pageable_count;
    }

    public void setPageable_count(Integer pageable_count) {
      this.pageable_count = pageable_count;
    }

    public Object getSame_name() {
      return this.same_name;
    }

    public void setSame_name(Object same_name) {
      this.same_name = same_name;
    }
  }
}
