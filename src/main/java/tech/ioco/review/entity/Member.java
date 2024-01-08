package tech.ioco.review.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Member implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private String surname;
    @Basic(optional = false)
    private String email;
    @ManyToMany(mappedBy = "members")
    private Set<Team> teams;
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @ManyToOne
    private Role role;

//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
//    private Stakeholder stakeholder;

    public Member() {
        this(null);
    }

    public Member(UUID id) {
        this(id, null, null, null);
    }

    public Member(UUID id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;

        this.teams = new HashSet<>();
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Member other)) {
            return false;
        }
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "tech.ioco.review.Member[ id=" + id + " ]";
    }

}
