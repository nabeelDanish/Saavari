/* Queries*/

/* Query for findDriver() */
SELECT USER_ID, USER_NAME, CAST(LATITUDE AS CHAR(12)) AS LATITUDE, CAST (LONGITUDE AS CHAR(12)) AS LONGITUDE FROM DRIVER_DETAILS
WHERE (LATITUDE BETWEEN %s AND %s) AND (LONGITUDE BETWEEN %s and %s)
ORDER BY LATITUDE %s + LONGITUDE - %s 


/* Query for checkRideStatus(), return rider details if request has been received */
SELECT D.RIDE_STATUS, D.RIDER_ID, R.USER_NAME, R.LATITUDE AS SOURCE_LAT, R.LONGITUDE AS SOURCE_LONG, D.DEST_LAT, D.DEST_LONG
FROM driver_details D
LEFT JOIN rider_details R
ON D.RIDER_ID = R.USER_ID
WHERE D.RIDE_STATUS = 1;

/* Fetch Ride details after drivcer accepts & Insert into Rides table */
INSERT INTO RIDES
SELECT 0, R.USER_ID AS RIDER_ID, D.USER_ID DRIVER_ID, NULL AS PAYMENT_ID, D.SOURCE_LAT, D.SOURCE_LONG, D.DEST_LAT, D.DEST_LONG, CURRENT_TIME(), NULL AS FINISH_TIME, 1 AS RIDE_TYPE, 0 AS ESTIMATED_FARE, 0 AS FARE, 1 AS STATUS
FROM DRIVER_DETAILS AS D, RIDER_DETAILS AS R
WHERE D.RIDER_ID = R.USER_ID;

/* Fetch complete ride details */
SELECT RD.RIDE_ID, R.USER_NAME, D.USER_NAME, RD.PAYMENT_ID, RD.SOURCE_LAT, RD.SOURCE_LONG, RD.DEST_LAT, RD.DEST_LONG, RD.START_TIME, RD.RIDE_TYPE, RD.ESTIMATED_FARE, RD.STATUS
FROM RIDES RD, RIDER_DETAILS R, DRIVER_DETAILS D
WHERE RD.RIDER_ID = 2 AND RD.DRIVER_ID = 2 AND RD.RIDER_ID = R.USER_ID AND RD.DRIVER_ID = D.USER_ID