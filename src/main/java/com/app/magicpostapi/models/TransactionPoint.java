package com.app.magicpostapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Set;

@Entity
@Table(name = "transactionpoint")
@Data
public class TransactionPoint {
    @Id
    @GeneratedValue(generator = "custom_id")
    @GenericGenerator(
            name = "custom_id",
            type = com.app.magicpostapi.components.CustomGeneratedId.class,
            parameters = {@Parameter(name = "prefix", value = "TSP_"),
                    @Parameter(name = "tableName", value = "transactionpoint_seq")}
    )
    @Column(name = "id_transaction_point", nullable = false, unique = true)
    private String id;
    @Column(name = "address")
    private String address;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "city")
    private String city;
    @Column(name = "name")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint gatheringPoint;
    @OneToMany(mappedBy = "transactionPoint", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;

    public String getId() {
        return id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionPoint() {
    }

    public GatheringPoint getGatheringPoint() {
        return gatheringPoint;
    }

    public void setGatheringPoint(GatheringPoint gatheringPoint) {
        this.gatheringPoint = gatheringPoint;
    }

    public TransactionPoint(String address, String city, String name, boolean active) {
        this.address = address;
        this.city = city;
        this.name = name;
        this.active = active;
    }

    public TransactionPoint(String address, boolean active) {
        this.address = address;
        this.active = active;
    }

    @Override
    public String toString() {
        return "TransactionPoint{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", active=" + active +
                '}';
    }
}
