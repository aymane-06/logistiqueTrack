package com.logitrack.logitrack.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "clients")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User {


    private String contactInfo;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<SalesOrder> orders ;

}
