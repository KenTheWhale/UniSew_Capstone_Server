package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_draft`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String description;

    @Column(name = "`design_date`")
    LocalDate designDate;

    @Column(name = "`final`")
    boolean isFinal;

    @ManyToOne
    @JoinColumn(name = "`cloth_id`")
    Cloth cloth;

    @OneToMany(mappedBy = "designDraft")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DraftImage> draftImages;

    @OneToMany(mappedBy = "designDraft")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RevisionRequest> revisionRequests;
}
