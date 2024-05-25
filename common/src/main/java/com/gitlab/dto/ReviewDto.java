package com.gitlab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Setter
@Getter
public class ReviewDto {

    @ReadOnlyProperty
    private Long id;

    @NotNull(message = "Review's productId should not be empty")
    private Long productId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createDate;

    @NotEmpty(message = "Review's pros should not be empty")
    @Size(min = 3, max = 512, message = "Length of Review's pros should be between 3 and 512 characters")
    private String pros;

    @NotEmpty(message = "Review's cons should not be empty")
    @Size(min = 3, max = 512, message = "Length of Review's cons should be between 3 and 512 characters")
    private String cons;

    @Size(min = 3, max = 1024, message = "Length of Review's comment should be between 3 and 1024 characters")
    private String comment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long[] reviewImagesId;

    @NotNull(message = "Review's rating should not be empty")
    @Range(min = 1, max = 10, message = "Review's rating should be between 1 and 10")
    private Byte rating;

    @Range(min = 1, max = 2147483333, message = "Review's helpfulCounter should be between 0 and 2147483333")
    private Integer helpfulCounter;

    @Range(min = 1, max = 2147483333, message = "Review's notHelpfulCounter should be between 0 and 2147483333")
    private Integer notHelpfulCounter;

}
