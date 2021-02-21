package qub;

public interface USPSCarrierTests
{
    static USPSCarrier createCarrier(Test test)
    {
        PreCondition.assertNotNull(test, "test");

        final Network network = test.getNetwork();
        final HttpClient httpClient = HttpClient.create(network);
        final EnvironmentVariables environmentVariables = test.getProcess().getEnvironmentVariables();
        final String uspsUserId = environmentVariables.get("USPS_USER_ID").await();
        final RealUSPSClient uspsClient = RealUSPSClient.create(httpClient)
            .setUserId(uspsUserId);
        final USPSCarrier result = USPSCarrier.create(uspsClient);

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static void test(TestRunner runner)
    {
        runner.testGroup(USPSCarrier.class, () ->
        {
            CarrierTests.test(runner, USPSCarrierTests::createCarrier);

            runner.testGroup("getShipmentSummary(Shipment)", () ->
            {
                final Action2<Shipment,String> getShipmentSummaryTest = (Shipment shipment, String expectedText) ->
                {
                    runner.test("with " + shipment, (Test test) ->
                    {
                        final USPSCarrier carrier = USPSCarrierTests.createCarrier(test);
                        test.assertEqual(
                            ShipmentSummary.create()
                                .setCarrierId("USPS")
                                .setTrackingId(shipment.getTrackingId())
                                .setText(expectedText),
                            carrier.getShipmentSummary(shipment).await());
                    });
                };

                getShipmentSummaryTest.run(
                    Shipment.create()
                        .setTrackingId("fake-tracking-id"),
                    "The Postal Service could not locate the tracking information for your request. Please verify your tracking number and try again later.");

                getShipmentSummaryTest.run(
                    Shipment.create()
                        .setTrackingId("9405511899564158800673"),
                    "Your item was delivered to the front desk, reception area, or mail room at 6:42 pm on November 24, 2020 in ONTARIO, CA 91761.");
            });
        });
    }
}
