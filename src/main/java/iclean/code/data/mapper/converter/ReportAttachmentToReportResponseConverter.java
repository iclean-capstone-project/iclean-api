package iclean.code.data.mapper.converter;

import iclean.code.data.domain.ReportAttachment;
import iclean.code.data.dto.response.report.ReportAttachmentResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ReportAttachmentToReportResponseConverter implements Converter<ReportAttachment, ReportAttachmentResponse> {
    @Override
    public ReportAttachmentResponse convert(MappingContext<ReportAttachment, ReportAttachmentResponse> context) {
        ReportAttachment source = context.getSource();
        ReportAttachmentResponse response = new ReportAttachmentResponse();
        response.setBookingAttachmentId(source.getReportAttachmentId());
        response.setBookingAttachmentLink(source.getReportAttachmentLink());
        response.setCreateAt(source.getCreateAt());
        return response;
    }
}