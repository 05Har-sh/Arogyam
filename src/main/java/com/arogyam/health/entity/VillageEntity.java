package com.arogyam.health.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "villages")
public class VillageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    private Integer population;

    @Column(length = 50)
    private String primaryLanguage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL)
    private List<UserEntity> users;

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL)
    private List<HealthReportEntity> healthReports;


    // Default constructor
    public VillageEntity() {
    }

    // Constructor with essential fields
    public VillageEntity(String name, String district, String state, Integer population) {
        this.name = name;
        this.district = district;
        this.state = state;
        this.population = population;
    }

    // All arguments constructor
    public VillageEntity(Long id, String name, String district, String state,
                         BigDecimal latitude, BigDecimal longitude, Integer population,
                         String primaryLanguage, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.primaryLanguage = primaryLanguage;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder pattern implementation
    public static VillageEntityBuilder builder() {
        return new VillageEntityBuilder();
    }

    // Builder class
    public static class VillageEntityBuilder {
        private Long id;
        private String name;
        private String district;
        private String state;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Integer population;
        private String primaryLanguage;
        private LocalDateTime createdAt;

        public VillageEntityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public VillageEntityBuilder name(String name) {
            this.name = name;
            return this;
        }

        public VillageEntityBuilder district(String district) {
            this.district = district;
            return this;
        }

        public VillageEntityBuilder state(String state) {
            this.state = state;
            return this;
        }

        public VillageEntityBuilder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public VillageEntityBuilder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public VillageEntityBuilder population(Integer population) {
            this.population = population;
            return this;
        }

        public VillageEntityBuilder primaryLanguage(String primaryLanguage) {
            this.primaryLanguage = primaryLanguage;
            return this;
        }

        public VillageEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public VillageEntity build() {
            return new VillageEntity(id, name, district, state, latitude, longitude,
                    population, primaryLanguage, createdAt);
        }
    }

    // equals() method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VillageEntity that = (VillageEntity) obj;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (population != null ? !population.equals(that.population) : that.population != null) return false;
        if (primaryLanguage != null ? !primaryLanguage.equals(that.primaryLanguage) : that.primaryLanguage != null) return false;
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    // hashCode() method
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (population != null ? population.hashCode() : 0);
        result = 31 * result + (primaryLanguage != null ? primaryLanguage.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }

    // toString() method
    @Override
    public String toString() {
        return "VillageEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", population=" + population +
                ", primaryLanguage='" + primaryLanguage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}