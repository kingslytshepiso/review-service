package tech.ioco.review.entity;

import jakarta.persistence.*;

@Entity
public class Stakeholder extends Member {
    @Column(name = "staff")
    private boolean isStaffMember;

    @Column(name = "reviewee")
    private boolean isReviewee;

    @Column(name = "reviewer")
    private boolean isReviewer;

//    @ManyToMany(mappedBy = "stakeholders")
//    private Set<Team> groups;

//    @JoinColumn(name = "member_id", referencedColumnName = "id", insertable = false, updatable = false)
//    @OneToOne(optional = false)
//    private Member member;

    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    @ManyToOne
    private Organisation organisation;

    public Stakeholder() {
    }
    public Stakeholder(boolean staff, boolean reviewee, boolean reviewer) {
        this.isStaffMember = staff;
        this.isReviewee = reviewee;
        this.isReviewer = reviewer;
    }

    public boolean isStaffMember() {
        return isStaffMember;
    }

    public void setStaffMember(boolean isStaffMember) {
        this.isStaffMember = isStaffMember;
    }

    public boolean isReviewee() {
        return isReviewee;
    }

    public void setReviewee(boolean isReviewee) {
        this.isReviewee = isReviewee;
    }

    public boolean isReviewer() {
        return isReviewer;
    }

    public void setReviewer(boolean isReviewer) {
        this.isReviewer = isReviewer;
    }

//    public Set<Team> getGroups() {
//        return groups;
//    }
//
//    public void setGroups(Set<Team> groups) {
//        this.groups = groups;
//    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Stakeholder other)) {
            return false;
        }
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "tech.ioco.review.Stakeholder[ memberId=" + id + " ]";
    }
    
}
