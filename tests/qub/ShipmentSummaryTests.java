package qub;

public interface ShipmentSummaryTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(ShipmentSummary.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final ShipmentSummary summary = ShipmentSummary.create();
                test.assertNotNull(summary);
                test.assertNull(summary.getTrackingId());
                test.assertNull(summary.getCarrierId());
                test.assertNull(summary.getText());
            });

            runner.testGroup("create(JSONObject)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> ShipmentSummary.create(null),
                        new PreConditionFailure("json cannot be null."));
                });

                runner.test("with empty JSON object", (Test test) ->
                {
                    final JSONObject json = JSONObject.create();
                    final ShipmentSummary summary = ShipmentSummary.create(json);
                    test.assertNotNull(summary);
                    test.assertEqual(json, summary.toJson());
                    test.assertNull(summary.getTrackingId());
                    test.assertNull(summary.getCarrierId());
                    test.assertNull(summary.getText());
                });

                runner.test("with full JSON object", (Test test) ->
                {
                    final JSONObject json = JSONObject.create()
                        .setString("carrierId", "a")
                        .setString("trackingId", "b")
                        .setString("text", "c");
                    final ShipmentSummary summary = ShipmentSummary.create(json);
                    test.assertNotNull(summary);
                    test.assertEqual(json, summary.toJson());
                    test.assertEqual("b", summary.getTrackingId());
                    test.assertEqual("a", summary.getCarrierId());
                    test.assertEqual("c", summary.getText());
                });
            });

            runner.testGroup("setCarrierId(String)", () ->
            {
                final Action2<String,Throwable> setCarrierIdErrorTest = (String carrierId, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(carrierId), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        test.assertThrows(() -> summary.setCarrierId(carrierId), expected);
                        test.assertNull(summary.getCarrierId());
                    });
                };

                setCarrierIdErrorTest.run(null, new PreConditionFailure("carrierId cannot be null."));
                setCarrierIdErrorTest.run("", new PreConditionFailure("carrierId cannot be empty."));

                final Action1<String> setCarrierIdTest = (String carrierId) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(carrierId), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        final ShipmentSummary setCarrierIdResult = summary.setCarrierId(carrierId);
                        test.assertSame(summary, setCarrierIdResult);
                        test.assertEqual(carrierId, summary.getCarrierId());
                        test.assertNull(summary.getTrackingId());
                        test.assertNull(summary.getText());
                    });
                };

                setCarrierIdTest.run("USPS");
                setCarrierIdTest.run("foo");
            });

            runner.testGroup("setTrackingId(String)", () ->
            {
                final Action2<String,Throwable> setTrackingIdErrorTest = (String trackingId, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(trackingId), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        test.assertThrows(() -> summary.setTrackingId(trackingId), expected);
                        test.assertNull(summary.getTrackingId());
                    });
                };

                setTrackingIdErrorTest.run(null, new PreConditionFailure("trackingId cannot be null."));
                setTrackingIdErrorTest.run("", new PreConditionFailure("trackingId cannot be empty."));

                final Action1<String> setTrackingIdTest = (String trackingId) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(trackingId), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        final ShipmentSummary setTrackingIdResult = summary.setTrackingId(trackingId);
                        test.assertSame(summary, setTrackingIdResult);
                        test.assertEqual(null, summary.getCarrierId());
                        test.assertEqual(trackingId, summary.getTrackingId());
                        test.assertEqual(null, summary.getText());
                    });
                };

                setTrackingIdTest.run("a");
                setTrackingIdTest.run("foo");
            });

            runner.testGroup("setText(String)", () ->
            {
                final Action2<String,Throwable> setTextErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        test.assertThrows(() -> summary.setText(text), expected);
                        test.assertNull(summary.getTrackingId());
                    });
                };

                setTextErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                setTextErrorTest.run("", new PreConditionFailure("text cannot be empty."));

                final Action1<String> setTextTest = (String text) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final ShipmentSummary summary = ShipmentSummary.create();
                        final ShipmentSummary setTextResult = summary.setText(text);
                        test.assertSame(summary, setTextResult);
                        test.assertEqual(null, summary.getCarrierId());
                        test.assertEqual(null, summary.getTrackingId());
                        test.assertEqual(text, summary.getText());
                    });
                };

                setTextTest.run("a");
                setTextTest.run("foo");
            });
        });
    }
}
