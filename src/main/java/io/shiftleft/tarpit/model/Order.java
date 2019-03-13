package io.shiftleft.tarpit.model;

import java.util.Date;

public class Order {

  private String orderId;
  private String custId;
  private Date orderDate;
  private String orderStatus;
  private Date shipDate;
  private String street;
  private String city;
  private String state;
  private String zipCode;

  public Order(String orderId, String custId, Date orderDate, String orderStatus,
      Date shipDate, String street, String city, String state, String zipCode) {
    this.orderId = orderId;
    this.custId = custId;
    this.orderDate = orderDate;
    this.orderStatus = orderStatus;
    this.shipDate = shipDate;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getCustId() {
    return custId;
  }

  public void setCustId(String custId) {
    this.custId = custId;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Date getShipDate() {
    return shipDate;
  }

  public void setShipDate(Date shipDate) {
    this.shipDate = shipDate;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  @Override
  public String toString() {
    return "Order{" +
        "orderId='" + orderId + '\'' +
        ", custId='" + custId + '\'' +
        ", orderDate=" + orderDate +
        ", orderStatus='" + orderStatus + '\'' +
        ", shipDate=" + shipDate +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zipCode='" + zipCode + '\'' +
        '}';
  }
}
