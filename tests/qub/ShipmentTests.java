package qub;

public interface ShipmentTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Shipment.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final Shipment shipment = Shipment.create();
                test.assertNotNull(shipment);
                test.assertNull(shipment.getCarrier());
                test.assertNull(shipment.getTrackingId());
                test.assertEqual(JSONObject.create(), shipment.toJson());
                test.assertEqual("{}", shipment.toString());
            });

            runner.testGroup("create(JSONObject)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Shipment.create(null),
                        new PreConditionFailure("json cannot be null."));
                });

                runner.test("with empty JSON object", (Test test) ->
                {
                    final Shipment shipment = Shipment.create(JSONObject.create());
                    test.assertNotNull(shipment);
                    test.assertNull(shipment.getCarrier());
                    test.assertNull(shipment.getTrackingId());
                });

                runner.test("with non-empty JSON object", (Test test) ->
                {
                    final Shipment shipment = Shipment.create(JSONObject.create()
                        .setString("carrier", "USPS")
                        .setString("trackingId", "fake-tracking-id"));
                    test.assertNotNull(shipment);
                    test.assertEqual("USPS", shipment.getCarrier());
                    test.assertEqual("fake-tracking-id", shipment.getTrackingId());
                });
            });

            runner.testGroup("getCarrier()", () ->
            {
                final Action2<Shipment,String> getCarrierTest = (Shipment shipment, String expected) ->
                {
                    runner.test("with " + shipment, (Test test) ->
                    {
                        test.assertEqual(expected, shipment.getCarrier());
                    });
                };

                getCarrierTest.run(Shipment.create(), null);
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setStringOrNull("carrier", null)),
                    null);
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "")),
                    "");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setBoolean("carrier", true)),
                    null);
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "fake-carrier")),
                    "fake-carrier");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "USPS")),
                    "USPS");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "usps")),
                    "usps");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "fedex")),
                    "fedex");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "ups")),
                    "ups");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "amazon")),
                    "amazon");
                getCarrierTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("carrier", "dhl")),
                    "dhl");
            });

            runner.testGroup("setCarrier(Carrier)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Shipment shipment = Shipment.create();
                    test.assertThrows(() -> shipment.setCarrier(null),
                        new PreConditionFailure("carrier cannot be null."));
                    test.assertNull(shipment.getCarrier());
                    test.assertEqual(Shipment.create(), shipment);
                });

                final Action1<String> setCarrierTest = (String carrier) ->
                {
                    runner.test("with " + carrier, (Test test) ->
                    {
                        final Shipment shipment = Shipment.create();
                        final Shipment setCarrierResult = shipment.setCarrier(carrier);
                        test.assertSame(shipment, setCarrierResult);
                        test.assertEqual(carrier, shipment.getCarrier());
                    });
                };

                setCarrierTest.run("blah");
                setCarrierTest.run("USPS");
                setCarrierTest.run("usps");
                setCarrierTest.run("UPS");
                setCarrierTest.run("FedEx");
                setCarrierTest.run("Amazon");
                setCarrierTest.run("DHL");
            });

            runner.testGroup("getTrackingId()", () ->
            {
                final Action2<Shipment,String> getTrackingIdTest = (Shipment shipment, String expected) ->
                {
                    runner.test("with " + shipment, (Test test) ->
                    {
                        test.assertEqual(expected, shipment.getTrackingId());
                    });
                };

                getTrackingIdTest.run(
                    Shipment.create(),
                    null);
                getTrackingIdTest.run(
                    Shipment.create(JSONObject.create()
                        .setStringOrNull("trackingId", null)),
                    null);
                getTrackingIdTest.run(
                    Shipment.create(JSONObject.create()
                        .setNumber("trackingId", 5)),
                    null);
                getTrackingIdTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("trackingId", "")),
                    "");
                getTrackingIdTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("trackingId", "hello")),
                    "hello");
            });

            runner.testGroup("setTrackingId(String)", () ->
            {
                final Action2<String,Throwable> setTrackingIdErrorTest = (String trackingId, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(trackingId), (Test test) ->
                    {
                        final Shipment shipment = Shipment.create();
                        test.assertThrows(() -> shipment.setTrackingId(trackingId), expected);
                        test.assertNull(shipment.getTrackingId());
                        test.assertEqual(Shipment.create(), shipment);
                    });
                };

                setTrackingIdErrorTest.run(null, new PreConditionFailure("trackingId cannot be null."));
                setTrackingIdErrorTest.run("", new PreConditionFailure("trackingId cannot be empty."));

                final Action1<String> setTrackingIdTest = (String trackingId) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(trackingId), (Test test) ->
                    {
                        final Shipment shipment = Shipment.create();
                        final Shipment setTrackingIdResult = shipment.setTrackingId(trackingId);
                        test.assertSame(shipment, setTrackingIdResult);
                        test.assertEqual(trackingId, shipment.getTrackingId());
                    });
                };

                setTrackingIdTest.run("fake-tracking-id");
            });
        });
    }
}
