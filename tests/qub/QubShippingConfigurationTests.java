package qub;

public interface QubShippingConfigurationTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubShippingConfiguration.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final QubShippingConfiguration configuration = QubShippingConfiguration.create();
                test.assertNotNull(configuration);
                test.assertNull(configuration.getShipments());
                test.assertEqual(JSONObject.create(), configuration.toJson());
                test.assertEqual("{}", configuration.toString());
            });

            runner.testGroup("create(JSONObject)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubShippingConfiguration.create(null),
                        new PreConditionFailure("json cannot be null."));
                });

                runner.test("with empty JSON object", (Test test) ->
                {
                    final QubShippingConfiguration configuration = QubShippingConfiguration.create(JSONObject.create());
                    test.assertNotNull(configuration);
                    test.assertNull(configuration.getShipments());
                    test.assertEqual(JSONObject.create(), configuration.toJson());
                    test.assertEqual(QubShippingConfiguration.create(), configuration);
                });

                runner.test("with non-empty JSON object", (Test test) ->
                {
                    final QubShippingConfiguration configuration = QubShippingConfiguration.create(JSONObject.create()
                        .setArray("shipments", JSONArray.create()));
                    test.assertNotNull(configuration);
                    test.assertEqual(Iterable.create(), configuration.getShipments());
                });
            });

            runner.testGroup("getShipments()", () ->
            {
                final Action2<QubShippingConfiguration,Iterable<Shipment>> getShipmentsTest = (QubShippingConfiguration configuration, Iterable<Shipment> expected) ->
                {
                    runner.test("with " + configuration, (Test test) ->
                    {
                        test.assertEqual(expected, configuration.getShipments());
                    });
                };

                getShipmentsTest.run(
                    QubShippingConfiguration.create(),
                    null);
                getShipmentsTest.run(
                    QubShippingConfiguration.create(JSONObject.create()
                        .setNull("shipments")),
                    null);
                getShipmentsTest.run(
                    QubShippingConfiguration.create(JSONObject.create()
                        .setNumber("shipments", 1)),
                    null);
                getShipmentsTest.run(
                    QubShippingConfiguration.create(JSONObject.create()
                        .setArray("shipments", JSONArray.create())),
                    Iterable.create());
                getShipmentsTest.run(
                    QubShippingConfiguration.create(JSONObject.create()
                        .setArray("shipments", JSONArray.create(
                            Shipment.create()
                                .setTrackingId("fake-tracking-id")
                                .toJson()))),
                    Iterable.create(
                        Shipment.create()
                            .setTrackingId("fake-tracking-id")));
            });

            runner.testGroup("setShipments(Iterable<Shipment>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final QubShippingConfiguration configuration = QubShippingConfiguration.create();
                    test.assertThrows(() -> configuration.setShipments(null),
                        new PreConditionFailure("shipments cannot be null."));
                    test.assertNull(configuration.getShipments());
                });

                runner.test("with empty", (Test test) ->
                {
                    final QubShippingConfiguration configuration = QubShippingConfiguration.create();
                    final QubShippingConfiguration setShipmentsResult = configuration.setShipments(Iterable.create());
                    test.assertSame(configuration, setShipmentsResult);
                    test.assertEqual(Iterable.create(), configuration.getShipments());
                    test.assertEqual(
                        JSONObject.create()
                            .setArray("shipments", JSONArray.create()),
                        configuration.toJson());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final QubShippingConfiguration configuration = QubShippingConfiguration.create();
                    final Iterable<Shipment> shipments = Iterable.create(
                        Shipment.create()
                            .setTrackingId("fake-tracking-id")
                            .setCarrier("USPS"));
                    final QubShippingConfiguration setShipmentsResult = configuration.setShipments(shipments);
                    test.assertSame(configuration, setShipmentsResult);
                    test.assertEqual(shipments, configuration.getShipments());
                    test.assertEqual(
                        JSONObject.create()
                            .setArray("shipments", JSONArray.create(
                                JSONObject.create()
                                    .setString("trackingId", "fake-tracking-id")
                                    .setString("carrier", "USPS"))),
                        configuration.toJson());
                });
            });
        });
    }
}
