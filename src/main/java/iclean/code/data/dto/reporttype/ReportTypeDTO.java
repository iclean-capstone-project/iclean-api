package iclean.code.data.dto.reporttype;

import lombok.Data;

import javax.persistence.Column;

@Data
public class ReportTypeDTO {
    private int reportTypeId;

    private String reportDetail;

}
