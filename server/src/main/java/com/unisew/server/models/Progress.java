package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`progress`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`first_milestone_date`")
    LocalDate firstMilestoneDate;

    @Column(name = "`first_milestone_completed_quantity`")
    int firstMilestoneCompletedQuantity;

    @Column(name = "`first_milestone_estimated_quantity`")
    int firstMilestoneEstimatedQuantity;

    @Column(name = "`second_milestone_date`")
    LocalDate secondMilestoneDate;

    @Column(name = "`second_milestone_completed_quantity`")
    int secondMilestoneCompletedQuantity;

    @Column(name = "`second_milestone_estimated_quantity`")
    int secondMilestoneEstimatedQuantity;

    @Column(name = "`third_milestone_date`")
    LocalDate thirdMilestoneDate;

    @Column(name = "`third_milestone_completed_quantity`")
    int thirdMilestoneCompletedQuantity;

    @Column(name = "`third_milestone_estimated_quantity`")
    int thirdMilestoneEstimatedQuantity;

    @Column(name = "`fourth_milestone_date`")
    LocalDate fourthMilestoneDate;

    @Column(name = "`fourth_milestone_completed_quantity`")
    int fourthMilestoneCompletedQuantity;

    @Column(name = "`fourth_milestone_estimated_quantity`")
    int fourthMilestoneEstimatedQuantity;

    @Column(name = "`delay_date`")
    LocalDate delayDate;

    @OneToOne
    @JoinColumn(name = "`contract_id`")
    GarmentContract garmentContract;
}
