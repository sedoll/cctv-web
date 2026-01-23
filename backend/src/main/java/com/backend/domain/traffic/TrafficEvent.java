package com.backend.domain.traffic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrafficEvent {
    @Id
    private String eventId;
    private String type;
    private String message;
    private Double coordX;
    private Double coordY;
    private LocalDateTime updateTime;

    public boolean needsUpdate(TrafficEvent newData) {
        return !this.message.equals(newData.getMessage()) || !this.type.equals(newData.getType());
    }
}