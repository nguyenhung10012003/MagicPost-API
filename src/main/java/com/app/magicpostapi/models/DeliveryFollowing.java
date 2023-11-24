package com.app.magicpostapi.models;

import com.app.magicpostapi.components.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "delivery_following")
public class DeliveryFollowing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_delivery")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lading_code", referencedColumnName = "lading_code", nullable = false)
    @JsonIgnore
    private Order order;
    @Column(name = "present_destination")
    private String presentDes;
    @Column(name = "next_destination")
    private String nextDes;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus deliveryStatus;
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @PrePersist
    protected void onInsert() {
        dateCreated = new Date(System.currentTimeMillis());
    }

    public DeliveryFollowing() {
    }

    public DeliveryFollowing(Long id, Order order, String presentDes, String nextDes, DeliveryStatus deliveryStatus, Date dateCreated) {
        this.id = id;
        this.order = order;
        this.presentDes = presentDes;
        this.nextDes = nextDes;
        this.deliveryStatus = deliveryStatus;
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


}
