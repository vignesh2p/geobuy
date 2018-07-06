package apps.codette.geobuy.Constants;

import java.util.List;

import apps.codette.forms.Organization;

/**
 * Created by user on 10-04-2018.
 */

public class GeobuyConstants {

    public static List<Organization> NEAR_BY_ORGS = null;

    public static String ORG_PRODUCTS_TABLE ="organization-products";
    public static String ORG_TABLE ="organization";
    public final static int REQUEST_LOCATION = 1000;
    public final static int HOME_REQUEST_LOCATION = 1001;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


}
