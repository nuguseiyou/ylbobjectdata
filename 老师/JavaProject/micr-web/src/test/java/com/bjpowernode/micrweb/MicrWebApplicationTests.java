package com.bjpowernode.micrweb;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class MicrWebApplicationTests {

	@Test
	void contextLoads() {

		/*Date curDate = new Date(); // 09-26
		Date beginTime = DateUtils.truncate(DateUtils.addDays(curDate,-1), Calendar.DATE);
		System.out.println("beginTime="+beginTime);*/

		BigDecimal rate = new BigDecimal("6.5");
		BigDecimal dateRate = rate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
				.divide(new BigDecimal("360"),10, RoundingMode.HALF_UP);
		System.out.println("dateRate="+dateRate);
	}

}
