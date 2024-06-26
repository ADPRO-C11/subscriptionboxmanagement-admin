package id.ac.ui.cs.advprog.snackscription_subscriptionbox.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table (name = "Item")
public class Item {
    @Id
    private String id;
    @Column(name = "item_name")
    private String name;

    @Column(name = "item_quantity")
    private int quantity;

    @ManyToMany(mappedBy = "items")
    @JsonBackReference
    private List<SubscriptionBox> subscriptionBoxes;

    public Item(String id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Item() {
    }
}