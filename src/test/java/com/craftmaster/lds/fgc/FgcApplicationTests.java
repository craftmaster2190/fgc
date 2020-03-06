package com.craftmaster.lds.fgc;

import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FgcApplicationTests {

  @Rule public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

  @Test
  public void contextLoads() {}
}
