package com.gitlab.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewDtoTest extends AbstractDtoTest {

    private ReviewDto getValidReviewDto() {
        var reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setPros("pros1");
        reviewDto.setCons("cons1");
        reviewDto.setComment("comment1");
        reviewDto.setRating((byte) 2);
        reviewDto.setHelpfulCounter(11);
        reviewDto.setNotHelpfulCounter(1);
        reviewDto.setUserId(8L);
        return reviewDto;
    }

    @Test
    void test_valid_reviewDto() {

        assertTrue(validator.validate(getValidReviewDto()).isEmpty());
    }

    @Test
    void test_invalid_pros_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setPros("a");

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's pros should be between 3 and 512 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_pros_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setPros("a".repeat(513));

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's pros should be between 3 and 512 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_null_pros() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setPros(null);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's pros should not be empty";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }

    //////////////////////////////////////////////////////////////////////////////////
    @Test
    void test_null_productId() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setProductId(null);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's productId should not be empty";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }
    //////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_invalid_cons_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setCons("a");

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's cons should be between 3 and 512 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_cons_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setCons("a".repeat(513));

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's cons should be between 3 and 512 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_null_cons() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setCons(null);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's cons should not be empty";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }
    //////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_invalid_comment_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setComment("a");

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's comment should be between 3 and 1024 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_comment_length() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setComment("a".repeat(1025));

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Length of Review's comment should be between 3 and 1024 characters";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_invalid_rating_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setRating((byte) 0);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's rating should be between 1 and 10";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_rating_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setRating((byte) 17);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's rating should be between 1 and 10";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_null_rating() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setRating(null);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's rating should not be empty";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_invalid_helpfulCounter_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setHelpfulCounter(-1);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's helpfulCounter should be between 0 and 2147483333";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_helpfulCounter_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setHelpfulCounter(Integer.MAX_VALUE);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's helpfulCounter should be between 0 and 2147483333";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_invalid_notHelpfulCounter_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setNotHelpfulCounter(-1);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's notHelpfulCounter should be between 0 and 2147483333";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }


    @Test
    void test_invalid_max_notHelpfulCounter_value() {
        ReviewDto reviewDto = getValidReviewDto();
        reviewDto.setNotHelpfulCounter(Integer.MAX_VALUE);

        assertFalse(validator.validate(reviewDto).isEmpty());
        String expectedMessage = "Review's notHelpfulCounter should be between 0 and 2147483333";
        assertEquals(expectedMessage, validator.validate(reviewDto).iterator().next().getMessage());
    }

    //////////////////////////////////////////////////////////////////////////////////
}
