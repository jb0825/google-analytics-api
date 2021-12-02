package test;

import com.google.analytics.admin.v1alpha.Account;
import com.google.analytics.admin.v1alpha.AccountName;
import com.google.analytics.admin.v1alpha.AnalyticsAdminServiceClient;
import com.google.analytics.admin.v1alpha.ListAccountsRequest;
import com.google.protobuf.FieldMask;

import java.util.Arrays;

public class Accounts {
    public static void main(String[] args) throws Exception {
        AnalyticsAdminServiceClient adminService = AnalyticsAdminServiceClient.create();

/*
        System.out.println("getAccountList() : ");
        getAccountList(adminService);

        System.out.println("----------\ngetAccount() : ");
        getAccount(adminService);
        */

        System.out.println("----------\nupdateAccount() : ");
        updateAccount(adminService);
        getAccount(adminService);

    }

    public static void getAccountList(AnalyticsAdminServiceClient adminService) {
        ListAccountsRequest request = ListAccountsRequest.newBuilder().build();
        adminService.listAccountsCallable().call(request).getAccountsList().forEach(i -> System.out.println(i.toString()));
    }


    public static Account getAccount(AnalyticsAdminServiceClient adminService) {
        AccountName name = AccountName.of("199724607");
        com.google.analytics.admin.v1alpha.Account response = adminService.getAccount(name);

        System.out.println(response.toString());
        return response;
    }

    public static void updateAccount(AnalyticsAdminServiceClient adminService) {
        Account account = Account.newBuilder().setName(getAccount(adminService).getName()).setDisplayName("changeAccountName").build();
        String[] update = {"display_name"};
        FieldMask updateMask = FieldMask.newBuilder().addAllPaths(Arrays.asList(update)).build();

        adminService.updateAccount(account, updateMask);
    }

}
