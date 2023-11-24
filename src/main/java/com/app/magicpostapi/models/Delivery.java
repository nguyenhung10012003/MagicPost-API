package com.app.magicpostapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_id")
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "id_order", nullable = false)
    @JsonIgnore
    private Order order;
    @Column(name = "present_des", nullable = false)
    private String presentDes;
    @Column(name = "next_des", nullable = false)
    private String nextDes;
    @Column(name = "isShipped")
    private boolean isShipped;
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @PrePersist
    protected void onInsert() {
        dateCreated = new Date(System.currentTimeMillis());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Delivery() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getPresentDes() {
        return presentDes;
    }

    public void setPresentDes(String presentDes) {
        this.presentDes = presentDes;
    }

    public String getNextDes() {
        return nextDes;
    }

    public void setNextDes(String nextDes) {
        this.nextDes = nextDes;
    }

    public boolean isShipped() {
        return isShipped;
    }

    public void setShipped(boolean shipped) {
        isShipped = shipped;
    }
}
