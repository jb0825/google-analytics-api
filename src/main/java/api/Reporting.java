package api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.McfData;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
public class Reporting {
    private static String KEY_FILE_LOCATION = "key/key_file.json";
    private static HttpTransport httpTransport;
    private static GoogleCredential credential;

    private AnalyticsReporting reporting;
    private Analytics analytics;
    private String viewId;

    public Reporting() throws GeneralSecurityException, IOException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsScopes.all());
    }

    public Reporting(String viewId) throws GeneralSecurityException, IOException {
        this.viewId = viewId;
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsScopes.all());
    }

    public void reportingInit() throws GeneralSecurityException, IOException {
        this.reporting = new AnalyticsReporting(httpTransport, GsonFactory.getDefaultInstance(), credential);
    }

    public void mcfInit() throws GeneralSecurityException, IOException {
        this.analytics = new Analytics(httpTransport, GsonFactory.getDefaultInstance(), credential);
    }

    /**
     * 소스/매체 보고서 조회
     * @param startDate 시작일 (ex. 1DaysAgo)
     * @param endDate 종료일 (ex. today)
     * @return Report
     * @throws IOException
     */
    public List<Report> getSourceMediumRevenueReport(String startDate, String endDate) throws IOException {
        DateRange dateRange = new DateRange().setStartDate(startDate).setEndDate(endDate);
        Dimension dimension = new Dimension().setName("ga:sourceMedium");
        Metric metric = new Metric().setExpression("ga:transactionRevenue").setFormattingType("INTEGER");

        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(Collections.singletonList(dateRange))
                .setDimensions(Collections.singletonList(dimension))
                .setMetrics(Collections.singletonList(metric));

        GetReportsRequest reportsRequest = new GetReportsRequest().setReportRequests(Collections.singletonList(request));

        return reporting.reports().batchGet(reportsRequest).execute().getReports();
    }

    /*
     * Multi Channel Funnel
     *      MCF Dimensions & Metrics Reference :
     *      https://developers.google.com/analytics/devguides/reporting/mcf/dimsmets
     */

    /**
     * MCF 소스/매체 보고서 지원 전환 가치 조회
     * @param startDate
     * @param endDate
     * @return
     * @throws IOException
     */
    public McfData getMcfSourceMediumData(String startDate, String endDate) throws IOException {
        return analytics.data().mcf().get(
            "ga:" + viewId, startDate, endDate, "mcf:assistedValue"
        ).setDimensions("mcf:sourceMedium").execute();
    }


    // 여러가지 조건 쿼리
    /**
     * 광고 콘텐츠가 'abc' 인 광고의 직접기여 조회
     * 각 ReportRow 예시 :
     * {"dimensions":["abc"],"metrics":[{"values":["직접기여"]}]}
     * @return List<ReportRow> directList
     */
    public List<ReportRow> getDirectList() {
        if (reporting == null) {
            System.out.println("service error");
            return null;
        }

        DateRange dateRange = new DateRange().setStartDate("1DaysAgo").setEndDate("today");
        List<Dimension> dimensions = new ArrayList<>();
        List<DimensionFilter> filters = new ArrayList<>();

        dimensions.add(new Dimension().setName("ga:adContent"));
        filters.add(new DimensionFilter().setDimensionName("ga:adContent").setOperator("EXACT").setExpressions(Collections.singletonList("abc")));

        DimensionFilterClause dimensionFilter = new DimensionFilterClause().setFilters(filters);
        Metric metric = new Metric().setExpression("ga:transactionRevenue").setFormattingType("INTEGER");

        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(Collections.singletonList(dateRange))
                .setDimensions(dimensions)
                .setDimensionFilterClauses(Collections.singletonList(dimensionFilter))
                .setMetrics(Collections.singletonList(metric));

        try {
            return reporting
                    .reports()
                    .batchGet(new GetReportsRequest().setReportRequests(Collections.singletonList(request)))
                    .execute().getReports().get(0).getData().getRows();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * 광고 콘텐츠가 'abc' 인 광고의 간접기여 조회
     * 각 List<Rows> 예시 :
     * [{"primitiveValue":"campaign-id"}, {"primitiveValue":"campaign-content"}, {"primitiveValue":"assistedValue"}]
     * @return
     */
    public List<List<McfData.Rows>> getIndirectList() {
        if (analytics == null) {
            System.out.println("service error");
            return null;
        }

        // filter =@ : substring
        // == : equals
        try {
            McfData response = analytics.data().mcf()
                    .get("ga:" + viewId, "2021-12-20", "today", "mcf:assistedValue")
                    .setDimensions("mcf:adwordsCampaignID,mcf:adwordsAdContent") // Dimension 여러개 사용할땐 , 쉼표로
                    .setFilters("mcf:adwordsAdContent=@abc")
                    .execute();
            return response.getRows();
        } catch (NullPointerException | IOException e) {
            return null;
        }
    }

}
