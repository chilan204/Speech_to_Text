package com.example.speech_to_text.entities;

import com.example.speech_to_text.entities.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column
    private String action;

    @Column
    private String direction;

    @Column
    private Integer value;

    @Column
    private String speaker;

    @Column
    private Double speakerScore;

    @Column
    private Double verificationScore;

    @Column
    private Boolean verified;

    @Column
    private String commandStatus;

    @Column
    private Boolean executed;

    @Column
    private String role;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;
}