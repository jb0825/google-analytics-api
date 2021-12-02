package test;

import com.google.analytics.admin.v1alpha.*;
import com.google.protobuf.FieldMask;

import java.io.IOException;
import java.util.ArrayList;

public class Properties {
    private static AnalyticsAdminServiceClient adminService;
    private static String propertyName = "274111670";

    public static void main(String... args) throws IOException {
        adminService = AnalyticsAdminServiceClient.create();
        AccountName name = AccountName.of("198242552");
        System.out.println(name);

        System.out.println("----------\nget() : ");
        get();
        /*
        System.out.println("----------\ngetList() : ");
        getList();
        System.out.println("----------\npatch() : ");
        patch();
        System.out.println("----------\ncreate() : ");
        create(name);

         */

    }

    static void get() {
        PropertyName name = PropertyName.of(propertyName);
        Property response = adminService.getProperty(name);
        System.out.println(response);
    }

    static void getList() {
        ListPropertiesRequest request = ListPropertiesRequest.newBuilder().setFilter("parent:accounts/198242552").build();
        adminService.listPropertiesCallable().call(request).getPropertiesList().forEach(i -> System.out.println(i.toString()));
    }

    static void patch() {
        Property property=  Property.newBuilder().setName("properties/" + propertyName).setDisplayName("updateProperty").setIndustryCategory(IndustryCategory.valueOf("FINANCE")).build();
        ArrayList<String> masks = new ArrayList<>();
        masks.add("display_name");
        masks.add("industry_category");

        FieldMask updateMask = FieldMask.newBuilder().addAllPaths(masks).build();

        System.out.println(adminService.updateProperty(property, updateMask));
    }

    static void create(AccountName name) {


        Property property = Property.newBuilder()
                .setParent(String.valueOf(name))
                .setDisplayName("createTest")
                .setIndustryCategory(IndustryCategory.valueOf("FINANCE"))
                .setTimeZone("Asia/Seoul")
                .setCurrencyCode("USD")
                .build();

        System.out.println(adminService.createProperty(property));
    }


    static void getUserLinkList() {
        ListUserLinksRequest request = ListUserLinksRequest.newBuilder().setParent("properties/" + propertyName).build();
        adminService.listUserLinksCallable().call(request).getUserLinksList().forEach(i -> System.out.println(i.toString()));
    }
}
