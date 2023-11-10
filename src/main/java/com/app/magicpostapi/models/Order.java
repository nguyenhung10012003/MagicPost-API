package com.app.magicpostapi.models;

import com.app.magicpostapi.components.HashMapConverter;
import com.app.magicpostapi.components.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order", nullable = false, unique = true)
    private Long id;
    @Column(name = "sender", nullable = false)
    private String senderName;
    @Column(name = "sender_phone", nullable = false)
    private String senderPhone;
    @Column(name = "sender_address")
    private String senderAddress;
    @Column(name = "receiver", nullable = false)
    private String receiverName;
    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;
    @Column(name = "receiver_address")
    private String receiverAddress;
    @Column(name = "lading_code", nullable = false, unique = true)
    private String ladingCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus orderStatus;
    @Column(name = "last_update", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_from", referencedColumnName = "id_transaction_point")
    @JsonIgnore
    private TransactionPoint transactionPointFrom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_to", referencedColumnName = "id_transaction_point")
    @JsonIgnore
    private TransactionPoint transactionPointTo;
    @Column(name = "tellers_name", nullable = false)
    private String tellersName;
    @Column(name = "note")
    private String note;
    @Column(name = "charge")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Integer> charge;

    public Order(Long id, String senderName, String senderPhone, String senderAddress, String receiverName, String receiverPhone, String receiverAddress, String ladingCode, OrderStatus orderStatus, Date lastUpdate, Date dateCreated, TransactionPoint transactionPointFrom, TransactionPoint transactionPointTo, String tellersName, String note, Map<String, Integer> charge) {
        this.id = id;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.senderAddress = senderAddress;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.ladingCode = ladingCode;
        this.orderStatus = orderStatus;
        this.lastUpdate = lastUpdate;
        this.dateCreated = dateCreated;
        this.transactionPointFrom = transactionPointFrom;
        this.transactionPointTo = transactionPointTo;
        this.tellersName = tellersName;
        this.note = note;
        this.charge = charge;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getLadingCode() {
        return ladingCode;
    }

    public void setLadingCode(String ladingCode) {
        this.ladingCode = ladingCode;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TransactionPoint getTransactionPointFrom() {
        return transactionPointFrom;
    }

    public void setTransactionPointFrom(TransactionPoint transactionPointFrom) {
        this.transactionPointFrom = transactionPointFrom;
    }

    public TransactionPoint getTransactionPointTo() {
        return transactionPointTo;
    }

    public void setTransactionPointTo(TransactionPoint transactionPointTo) {
        this.transactionPointTo = transactionPointTo;
    }

    public String getTellersName() {
        return tellersName;
    }

    public void setTellersName(String tellersName) {
        this.tellersName = tellersName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Map<String, Integer> getCharge() {
        return charge;
    }

    public void setCharge(Map<String, Integer> charge) {
        this.charge = charge;
    }

    @PrePersist
    protected void onInsert() {
        lastUpdate = new Date(System.currentTimeMillis());
        dateCreated = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Date(System.currentTimeMillis());
    }
}
