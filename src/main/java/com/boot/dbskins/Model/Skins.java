package com.boot.dbskins.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@Entity(name = "skins")

public class Skins {
    @Id
    @SequenceGenerator(name = "skinSeqGen", sequenceName = "skins_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "skinSeqGen")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_name", nullable = false)
    private NameOfSkins skinName;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type", nullable = false)
    private TypeOfSkins skinType;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "float", nullable = false)
    private Float floatOfSkin;

    @JoinColumn(name = "user_id")
    private Long userId;
}
