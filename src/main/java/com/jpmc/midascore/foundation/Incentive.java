package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Incentive {
  private float amount;
  
  public float getAmount() {
    return this.amount;
  }
  
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  public String toString() {
    return "Incentive {amount=" + this.amount + "}";
  }
}