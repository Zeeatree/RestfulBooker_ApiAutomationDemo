package com.restfulBooker.suites;

import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("Regression Test Suite")
@SelectClasses({com.restfulBooker.tests.DataDrivenBookingTestsJson.class, com.restfulBooker.tests.DataDrivenBookingTests.class, com.restfulBooker.tests.SchemaValidationTests.class})
@IncludeTags("regression")
public class RegressionTestSuite {
}
