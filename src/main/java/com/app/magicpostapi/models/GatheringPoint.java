package com.app.magicpostapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "gatheringpoint")
@Data
public class GatheringPoint {
    @Id
    @GeneratedValue(generator = "custom_id")
    @GenericGenerator(
            name = "custom_id",
            type = com.app.magicpostapi.components.CustomGeneratedId.class,
            parameters = {@Parameter(name = "prefix", value = "GRP_"),
                    @Parameter(name = "tableName", value = "gatheringpoint_seq")}
    )
    @Column(name = "id_gathering_point", nullable = false, unique = true)
    private String id;
    @Column(name = "address")
    private String address;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "city")
    private String city;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "gatheringPoint", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TransactionPoint> transactionPoints;
    @OneToMany(mappedBy = "gatheringPoint", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<TransactionPoint> getTransactionPoints() {
        return transactionPoints;
    }

    public void setTransactionPoints(Set<TransactionPoint> transactionPoints) {
        this.transactionPoints = transactionPoints;
    }

    public GatheringPoint(String id, String address, boolean active, String city, String name) {
        this.id = id;
        this.address = address;
        this.active = active;
        this.city = city;
        this.name = name;
    }

    public GatheringPoint() {

    }

    public String getId() {
        return id;
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

    public GatheringPoint(String address, boolean active, String city, String name) {
        this.address = address;
        this.active = active;
        this.city = city;
        this.name = name;
    }

    public GatheringPoint(String id, String address, boolean active, String city) {
        this.id = id;
        this.address = address;
        this.active = active;
        this.city = city;
    }

    public GatheringPoint(String address, boolean active, String city) {
        this.address = address;
        this.active = active;
        this.city = city;
    }

    public GatheringPoint(String address, boolean active) {
        this.address = address;
        this.active = active;
    }

    @Override
    public String toString() {
        return "GatheringPoint{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GatheringPoint that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
