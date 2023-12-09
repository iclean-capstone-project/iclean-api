package iclean.code.data.mapper;

import iclean.code.data.domain.Report;
import iclean.code.data.dto.response.report.GetReportResponseAsManager;
import iclean.code.data.dto.response.report.GetReportResponseDetail;
import iclean.code.data.mapper.converter.ReportAttachmentToReportResponseConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {
    private final ModelMapper modelMapper;

    public ReportMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new ReportAttachmentToReportResponseConverter());

        modelMapper.typeMap(Report.class, GetReportResponseDetail.class)
                .addMappings(mapper -> {
                    mapper.map(Report::getReportAttachments, GetReportResponseDetail::setAttachmentResponses);
                });
        modelMapper.addMappings(new PropertyMap<Report, GetReportResponseAsManager>() {
            @Override
            protected void configure() {
                map().setFullName(source.getBookingDetail().getBooking().getRenter().getFullName());
                map().setPhoneNumber(source.getBookingDetail().getBooking().getRenter().getPhoneNumber());
                map().setReportTypeDetail(source.getReportType().getReportName());
            }
        });

        modelMapper.addMappings(new PropertyMap<Report, GetReportResponseDetail>() {
            @Override
            protected void configure() {
                map().setBookingDetailId(source.getBookingDetail().getBookingDetailId());
                map().setFullName(source.getBookingDetail().getBooking().getRenter().getFullName());
                map().setPhoneNumber(source.getBookingDetail().getBooking().getRenter().getPhoneNumber());
                map().setReportTypeDetail(source.getReportType().getReportName());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}