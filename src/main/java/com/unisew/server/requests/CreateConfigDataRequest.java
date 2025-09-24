package com.unisew.server.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateConfigDataRequest {
    List<FabricData> fabricDataList;
    BusinessData businessData;
    MediaData mediaData;
    DesignData designData;
    OrderData orderData;
    ReportData reportData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FabricData{
        String name;
        String description;
        boolean forShirt;
        boolean forPants;
        boolean forSkirt;
        boolean forRegular;
        boolean forPE;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BusinessData{
        double taxRate;
        double serviceRate;
        double minPay;
        double maxPay;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MediaData{
        int maxImgSize;
        int maxVideoSize;
        int maxDesignRefImg;
        int maxFeedbackImg;
        int maxFeedbackVideo;
        int maxReportImg;
        int maxReportVideo;
        int maxGarmentThumbnail;
        int maxDesignerThumbnail;
        List<MediaFormat> imageFormats;
        List<MediaFormat> videoFormats;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MediaFormat{
        String format;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DesignData{
        String illustrationImage;
        List<LogoPosition> positions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LogoPosition{
        String position;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderData{
        int minUniformQty;
        int maxAssignedMilestone;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ReportData{
        int maxDisbursementDay;
        List<SeverityLevel> levels;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SeverityLevel{
        String name;
        String compensation;
    }
}
