package qub;

public interface CarrierTests
{
    static void test(TestRunner runner, Function1<Test,? extends Carrier> creator)
    {
        PreCondition.assertNotNull(runner, "runner");
        PreCondition.assertNotNull(creator, "creator");

        runner.testGroup(Carrier.class, () ->
        {
            runner.testGroup("getShipmentSummary(Shipment)", () ->
            {
                final Action2<Shipment,Throwable> getShipmentSummaryErrorTest = (Shipment shipment, Throwable expected) ->
                {
                    runner.test("with " + shipment, (Test test) ->
                    {
                        final Carrier carrier = creator.run(test);
                        test.assertThrows(() -> carrier.getShipmentSummary(shipment).await(), expected);
                    });
                };

                getShipmentSummaryErrorTest.run(
                    null,
                    new PreConditionFailure("shipment cannot be null."));
                getShipmentSummaryErrorTest.run(
                    Shipment.create(),
                    new PreConditionFailure("shipment.getTrackingId() cannot be null."));
                getShipmentSummaryErrorTest.run(
                    Shipment.create(JSONObject.create()
                        .setString("trackingId", "")),
                    new PreConditionFailure("shipment.getTrackingId() cannot be empty."));
            });
        });
    }
}
