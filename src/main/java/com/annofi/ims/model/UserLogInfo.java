package com.annofi.ims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "user_log_info")
public class UserLogInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;

    @JsonProperty("user_id")
    private Short userId;

    @JsonProperty("logged_in_date_time")
    private Timestamp loggedInDateTime;
}
