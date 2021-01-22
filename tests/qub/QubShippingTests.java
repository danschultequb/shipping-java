package qub;

public interface QubShippingTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubShipping.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubShipping.main(null),
                        new PreConditionFailure("args cannot be null."));
                });
            });
        });
    }
}