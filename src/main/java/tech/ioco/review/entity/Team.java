package tech.ioco.review.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Basic(optional = false)
    private String name;

    @Column(name = "reviewer")
    private boolean isReviewer;
    @JoinTable(name = "team_review", joinColumns = {
            @JoinColumn(name = "team_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "review_id", referencedColumnName = "id")})
    @ManyToMany
    private Set<Review> reviews;
    @ManyToMany
    @JoinTable(name = "team_member", joinColumns = {
            @JoinColumn(name = "team_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "member_id", referencedColumnName = "id")})
    private Set<Member> members;
    @JoinTable(name = "team_stakeholder", joinColumns = {
            @JoinColumn(name = "team_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "stakeholder_id", referencedColumnName = "id")})
    @ManyToMany
    private Set<Stakeholder> stakeholders;

    public Team() {
        this(null, null, false);
    }

    public Team(UUID id, String name, boolean reviewer) {
        this.id = id;
        this.name = name;
        this.isReviewer = reviewer;

        this.reviews = new HashSet<>();
        this.members = new HashSet<>();
        this.stakeholders = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReviewer() {
        return isReviewer;
    }

    public void setReviewer(boolean isReviewer) {
        this.isReviewer = isReviewer;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public Set<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(Set<Stakeholder> stakeholders) {
        this.stakeholders = stakeholders;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Team other)) {
            return false;
        }
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "tech.ioco.review.Team[ id=" + id + " ]";
    }

}
