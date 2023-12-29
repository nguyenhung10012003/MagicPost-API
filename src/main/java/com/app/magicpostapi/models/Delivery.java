package com.app.magicpostapi.models;

import com.app.magicpostapi.components.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "delivery")
@EntityListeners(value = DeliveryEntityListener.class)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lading_code", referencedColumnName = "lading_code", nullable = false)
    @JsonIgnoreProperties("['senderName', 'senderPhone', 'senderAddress', 'receiverName', 'receiverPhone', 'receiverAddress', 'charge', 'note', 'tellersName','orderStatus', 'dateCreated', 'lastUpdate']")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "present_des", referencedColumnName = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint presentDes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_des", referencedColumnName = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint nextDes;
    @ManyToOne
    @JoinColumn(name = "last_des", referencedColumnName = "id_gathering_point")
    //@JsonIgnore
    private GatheringPoint lastDes;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "delivery")
    @JsonIgnore
    private Set<DeliveryFollowing> deliveryFollowingSet;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(name = "isShipped", nullable = false)
    private boolean isShipped;

    public GatheringPoint getLastDes() {
        return lastDes;
    }

    public Set<DeliveryFollowing> getDeliveryFollowingSet() {
        return deliveryFollowingSet;
    }

    public void setDeliveryFollowingSet(Set<DeliveryFollowing> deliveryFollowingSet) {
        this.deliveryFollowingSet = deliveryFollowingSet;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isShipped() {
        return isShipped;
    }

    public void setShipped(boolean shipped) {
        isShipped = shipped;
    }

    public void setLastDes(GatheringPoint lastDes) {
        this.lastDes = lastDes;
    }

    @PrePersist
    protected void onInsert() {
        dateCreated = new Date(System.currentTimeMillis());
        isShipped = false;
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

    public GatheringPoint getPresentDes() {
        return presentDes;
    }

    public void setPresentDes(GatheringPoint presentDes) {
        this.presentDes = presentDes;
    }

    public GatheringPoint getNextDes() {
        return nextDes;
    }

    public void setNextDes(GatheringPoint nextDes) {
        this.nextDes = nextDes;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delivery delivery)) return false;
        return Objects.equals(id, delivery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
