package tech.ioco.review.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StakeholderTest {

    private Stakeholder stakeholder;

    @BeforeEach
    void setUp() {
        stakeholder = new Stakeholder();
    }

    @Test
    @DisplayName("test the setStaffMember and isStaffMember methods when the staff member status is set to true")
    void testSetStaffMemberWhenTrueThenIsStaffMemberReturnsTrue() {
        stakeholder.setStaffMember(true);
        assertTrue(stakeholder.isStaffMember());
    }

    @Test
    @DisplayName("test the setStaffMember and isStaffMember methods when the staff member status is set to false")
    void testSetStaffMemberWhenFalseThenIsStaffMemberReturnsFalse() {
        stakeholder.setStaffMember(false);
        assertFalse(stakeholder.isStaffMember());
    }

    @Test
    @DisplayName("test the setReviewee and isReviewee methods when the reviewee status is set to true")
    void testSetRevieweeWhenTrueThenIsRevieweeReturnsTrue() {
        stakeholder.setReviewee(true);
        assertTrue(stakeholder.isReviewee());
    }

    @Test
    @DisplayName("test the setReviewee and isReviewee methods when the reviewee status is set to false")
    void testSetRevieweeWhenFalseThenIsRevieweeReturnsFalse() {
        stakeholder.setReviewee(false);
        assertFalse(stakeholder.isReviewee());
    }

    @Test
    @DisplayName("test the setReviewer and isReviewer methods when the reviewer status is set to true")
    void testSetReviewerWhenTrueThenIsReviewerReturnsTrue() {
        stakeholder.setReviewer(true);
        assertTrue(stakeholder.isReviewer());
    }

    @Test
    @DisplayName("test the setReviewer and isReviewer methods when the reviewer status is set to false")
    void testSetReviewerWhenFalseThenIsReviewerReturnsFalse() {
        stakeholder.setReviewer(false);
        assertFalse(stakeholder.isReviewer());
    }

    @Test
    @DisplayName("test the setOrganisation and getOrganisation methods")
    void testSetOrganisationAndGetOrganisation() {
        Organisation organisation = new Organisation();
        stakeholder.setOrganisation(organisation);
        assertSame(organisation, stakeholder.getOrganisation());
    }

    @Test
    @DisplayName("test the equals method for equality")
    void testEqualsMethod() {
        Stakeholder otherStakeholder = new Stakeholder();
        otherStakeholder.setId(stakeholder.getId());
        assertEquals(stakeholder, otherStakeholder);
    }

    @Test
    @DisplayName("test the toString method for correct format")
    void testToStringMethod() {
        String expectedString = "tech.ioco.review.Stakeholder[ memberId=" + stakeholder.getId() + " ]";
        assertEquals(expectedString, stakeholder.toString());
    }
}
