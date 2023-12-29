package com.app.magicpostapi.models;

import com.app.magicpostapi.components.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "delivery_following")
public class DeliveryFollowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "delivery_id")
    @JsonIgnore
    private Delivery delivery;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus deliveryStatus;
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromGathering", referencedColumnName = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint from;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toGathering", referencedColumnName = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint to;

    @PrePersist
    protected void onInsert() {
        dateCreated = new Date(System.currentTimeMillis());
    }

    public DeliveryFollowing() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GatheringPoint getFrom() {
        return from;
    }

    public void setFrom(GatheringPoint from) {
        this.from = from;
    }

    public GatheringPoint getTo() {
        return to;
    }

    public void setTo(GatheringPoint to) {
        this.to = to;
    }

    public DeliveryFollowing(Long id, Delivery delivery, DeliveryStatus deliveryStatus, Date dateCreated, GatheringPoint from, GatheringPoint to) {
        this.id = id;
        this.delivery = delivery;
        this.deliveryStatus = deliveryStatus;
        this.dateCreated = dateCreated;
        this.from = from;
        this.to = to;
    }

    public DeliveryFollowing(Long id, DeliveryStatus deliveryStatus, Date dateCreated) {
        this.id = id;
        this.deliveryStatus = deliveryStatus;
        this.dateCreated = dateCreated;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
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
