package com.coronis.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.coronis.test.frames.*;
import com.coronis.test.modules.*;

@RunWith(Suite.class)
@SuiteClasses(value={	ConfigTest.class,
						CoronisLibTest.class,
						CoronisFrameTest.class,
						CoronisFrameBuilderTest.class,
						CoronisFrameReaderTest.class,
						ModuleTest.class,
						WavePortTest.class,
						WaveFlowTest.class,
						WaveTalkTest.class,
						WaveTankTest.class,
						WaveSense4_20Test.class,
						WaveSense5VTest.class,
						WaveThermDalasTest.class,
						WaveThermPT100Test.class
})
public class TestSuite {

}
