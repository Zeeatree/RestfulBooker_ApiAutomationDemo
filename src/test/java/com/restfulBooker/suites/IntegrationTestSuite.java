package com.restfulBooker.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.restfulBooker.tests")
@IncludeTags("integration")
public class IntegrationTestSuite {
}