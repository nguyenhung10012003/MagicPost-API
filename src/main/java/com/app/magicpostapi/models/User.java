package com.app.magicpostapi.models;

import com.app.magicpostapi.components.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "account")
@Data
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_account", unique = true)
    private Long id;
    @Column(name = "username", nullable = false, unique = true, length = 24)
    private String username;
    @Column(name = "password", nullable = false, unique = true, length = 24)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gathering_point", referencedColumnName = "id_gathering_point")
    @JsonIgnore
    private GatheringPoint gatheringPoint;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaction_point", referencedColumnName = "id_transaction_point")
    @JsonIgnore
    private TransactionPoint transactionPoint;
    @Column(name = "active", nullable = false)
    private boolean active;

    public User(String username, String password, Role role, boolean active) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public User(Long id, String username, String password, Role role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public User() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role.name();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setGatheringPoint(GatheringPoint gatheringPoint) {
        this.gatheringPoint = gatheringPoint;
    }

    public TransactionPoint getTransactionPoint() {
        return transactionPoint;
    }

    public void setTransactionPoint(TransactionPoint transactionPoint) {
        this.transactionPoint = transactionPoint;
    }
}
