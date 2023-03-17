package com.annofi.ims.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
    
    @JsonProperty("name")
    //@NotNull(message = "error.common.null")
    //@NotBlank(message = "error.common.empty")
    private String name;

    @JsonProperty("user_name")
    @Column(unique = true)
    //@NotNull(message = "error.common.null")
    //@NotBlank(message = "error.common.empty")
    private String username;

    private String password;
    
    @JsonProperty("contact_number")
    private String contactNumber;
    
    @JsonProperty("gender")
    private String gender;
    
    @JsonProperty("remarks")
    private String remarks;
    
    @JsonProperty("email")
    @Email(message = "Enter valid email address dumbass!!!")
    private String email;

    @Column(columnDefinition = "boolean default false")
    private boolean enabled;
    
    @Column(name = "account_non_locked", columnDefinition = "boolean default true")
    private boolean accountNonLocked = true;
     
    @Column(name = "failed_attempt", columnDefinition = "integer default 0")
    private Integer failedAttempt = 0;
     
    @Column(name = "lock_time")
    private Date lockTime;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

    @NotNull(message = "error.common.null")
    @Column(columnDefinition = "boolean default true")
    private boolean active;

    private String roles = "";

    private String permissions = "";

    /*@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private List<UserLogInfo> userLogInfos;*/

    public User(String username, String password, String roles, String permissions){
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.permissions = permissions;
        this.active = true;
    }
    
    protected User(){}

    @JsonIgnore
    public List<String> getRoleList(){
        if(this.roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

    @JsonIgnore
    public List<String> getPermissionList(){
        if(this.permissions.length() > 0){
            return Arrays.asList(this.permissions.split(","));
        }
        return new ArrayList<>();
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}