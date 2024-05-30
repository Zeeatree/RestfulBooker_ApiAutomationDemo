package com.restfulBooker.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Smoke Test Suite")
@SelectClasses({com.restfulBooker.tests.DataDrivenBookingTestsJson.class, com.restfulBooker.tests.DataDrivenBookingTests.class})
@IncludeTags("smoke")
public class SmokeTestSuite {
}