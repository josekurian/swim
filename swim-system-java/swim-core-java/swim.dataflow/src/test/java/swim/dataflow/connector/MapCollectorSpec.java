// Copyright 2015-2019 SWIM.AI inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package swim.dataflow.connector;

import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.Test;
import swim.dataflow.graph.Pair;
import static swim.dataflow.graph.Pair.pair;

public class MapCollectorSpec {

  @Test
  public void accumulateEntries() {

    final MapCollector<Pair<Integer, Integer>, Integer, Integer> collector =
        new MapCollector<>(Pair::getFirst, Pair::getSecond);

    final ArrayList<ConnectorTestUtil.MapAction<Integer, Integer>> results = ConnectorTestUtil.pushData(
        collector, pair(1, 2), pair(3, 3), pair(4, 5));

    Assert.assertEquals(results.size(), 3);

    ConnectorTestUtil.expectUpdate(results.get(0), (k, v, m) -> {
      Assert.assertEquals(k.intValue(), 1);
      Assert.assertEquals(v.intValue(), 2);
      Assert.assertEquals(m.size(), 1);
      Assert.assertEquals(m.get(1).get().intValue(), 2);
    });

    ConnectorTestUtil.expectUpdate(results.get(1), (k, v, m) -> {
      Assert.assertEquals(k.intValue(), 3);
      Assert.assertEquals(v.intValue(), 3);
      Assert.assertEquals(m.size(), 2);
      Assert.assertEquals(m.get(1).get().intValue(), 2);
      Assert.assertEquals(m.get(3).get().intValue(), 3);
    });

    ConnectorTestUtil.expectUpdate(results.get(2), (k, v, m) -> {
      Assert.assertEquals(k.intValue(), 4);
      Assert.assertEquals(v.intValue(), 5);
      Assert.assertEquals(m.size(), 3);
      Assert.assertEquals(m.get(1).get().intValue(), 2);
      Assert.assertEquals(m.get(3).get().intValue(), 3);
      Assert.assertEquals(m.get(4).get().intValue(), 5);
    });
  }

}
