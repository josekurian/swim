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
import swim.collections.FingerTrieSeq;
import swim.dataflow.connector.ConnectorTestUtil.Multiple;
import swim.dataflow.connector.ConnectorTestUtil.Parity;
import swim.dataflow.graph.persistence.FingerTrieSeqForm;
import swim.dataflow.graph.persistence.TrivialPersistenceProvider;
import swim.dataflow.graph.persistence.ValuePersister;
import swim.structure.Form;
import static swim.dataflow.graph.Either.left;
import static swim.dataflow.graph.Either.right;

public class StatefulConduitSpec {

  @Test
  public void foldInputs() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<FingerTrieSeq<Integer>> persister =
        provider.forValue("state", new FingerTrieSeqForm<>(Form.forInteger()), FingerTrieSeq.of(0));

    final Conduit<Integer, FingerTrieSeq<Integer>> conduit = StatefulConduits.foldPersistent(
        persister, FingerTrieSeq::appended);

    final ArrayList<FingerTrieSeq<Integer>> results = ConnectorTestUtil.pushData(conduit, 1, 2, 3, 4);

    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0), FingerTrieSeq.of(0, 1));
    Assert.assertEquals(results.get(1), FingerTrieSeq.of(0, 1, 2));
    Assert.assertEquals(results.get(2), FingerTrieSeq.of(0, 1, 2, 3));
    Assert.assertEquals(results.get(3), FingerTrieSeq.of(0, 1, 2, 3, 4));

    Assert.assertEquals(persister.get(), FingerTrieSeq.of(0, 1, 2, 3, 4));
  }

  @Test
  public void reduceInputsFromNothing() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<Integer> persister =
        provider.forValue("state", Form.forInteger(), null);

    final Conduit<Integer, Integer> conduit = StatefulConduits.reduce(persister, Math::min);
    final ArrayList<Integer> results = ConnectorTestUtil.pushData(conduit, 35, 76, 12, 7);
    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0).intValue(), 35);
    Assert.assertEquals(results.get(1).intValue(), 35);
    Assert.assertEquals(results.get(2).intValue(), 12);
    Assert.assertEquals(results.get(3).intValue(), 7);

    Assert.assertEquals(persister.get().intValue(), 7);
  }

  @Test
  public void reduceInputsFromSeed() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<Integer> persister =
        provider.forValue("state", Form.forInteger(), 30);

    final Conduit<Integer, Integer> conduit = StatefulConduits.reduce(persister, Math::min);
    final ArrayList<Integer> results = ConnectorTestUtil.pushData(conduit, 35, 76, 12, 7);
    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0).intValue(), 30);
    Assert.assertEquals(results.get(1).intValue(), 30);
    Assert.assertEquals(results.get(2).intValue(), 12);
    Assert.assertEquals(results.get(3).intValue(), 7);

    Assert.assertEquals(persister.get().intValue(), 7);
  }

  @Test
  public void foldInputsModally() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<Parity> modePersister = provider.forValue("mode", Form.forEnum(Parity.class), Parity.ODD);

    final ValuePersister<FingerTrieSeq<Integer>> valPersister =
        provider.forValue("state", new FingerTrieSeqForm<>(Form.forInteger()), FingerTrieSeq.of(0));

    final Junction2<Integer, Parity, FingerTrieSeq<Integer>> conduit = StatefulConduits.modalFold(
        modePersister, valPersister, (Parity p) -> (FingerTrieSeq<Integer> seq, Integer n) ->
        (n % 2) == p.ordinal() ? seq.appended(n) : seq);

    final ArrayList<FingerTrieSeq<Integer>> results = ConnectorTestUtil.pushData(
        conduit, left(1), left(2), right(Parity.EVEN), left(3), left(4));

    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0), FingerTrieSeq.of(0, 1));
    Assert.assertEquals(results.get(1), FingerTrieSeq.of(0, 1));
    Assert.assertEquals(results.get(2), FingerTrieSeq.of(0, 1));
    Assert.assertEquals(results.get(3), FingerTrieSeq.of(0, 1, 4));

    Assert.assertEquals(modePersister.get(), Parity.EVEN);
    Assert.assertEquals(valPersister.get(), FingerTrieSeq.of(0, 1, 4));
  }

  @Test
  public void reduceInputsModallyFromNothing() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<Multiple> modePersister = provider.forValue(
        "mode", Form.forEnum(Multiple.class), Multiple.DOUBLE);
    final ValuePersister<Integer> valPersister =
        provider.forValue("state", Form.forInteger(), null);

    final Junction2<Integer, Multiple, Integer> conduit =
        StatefulConduits.modalReduce(modePersister, valPersister, m -> (sum, n) -> sum * m.getFactor() + n);

    final ArrayList<Integer> results = ConnectorTestUtil.pushData(
        conduit, left(1), left(2), right(Multiple.TRIPLE), left(3), left(4));

    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0).intValue(), 1);
    Assert.assertEquals(results.get(1).intValue(), 4);
    Assert.assertEquals(results.get(2).intValue(), 15);
    Assert.assertEquals(results.get(3).intValue(), 49);

    Assert.assertEquals(modePersister.get(), Multiple.TRIPLE);
    Assert.assertEquals(valPersister.get().intValue(), 49);
  }

  @Test
  public void reduceInputsModallyFromSeed() {

    final TrivialPersistenceProvider provider = new TrivialPersistenceProvider();

    final ValuePersister<Multiple> modePersister = provider.forValue(
        "mode", Form.forEnum(Multiple.class), Multiple.DOUBLE);
    final ValuePersister<Integer> valPersister =
        provider.forValue("state", Form.forInteger(), 2);

    final Junction2<Integer, Multiple, Integer> conduit =
        StatefulConduits.modalReduce(modePersister, valPersister, m -> (sum, n) -> sum * m.getFactor() + n);

    final ArrayList<Integer> results = ConnectorTestUtil.pushData(
        conduit, left(1), left(2), right(Multiple.TRIPLE), left(3), left(4));

    Assert.assertEquals(results.size(), 4);
    Assert.assertEquals(results.get(0).intValue(), 5);
    Assert.assertEquals(results.get(1).intValue(), 12);
    Assert.assertEquals(results.get(2).intValue(), 39);
    Assert.assertEquals(results.get(3).intValue(), 121);

    Assert.assertEquals(modePersister.get(), Multiple.TRIPLE);
    Assert.assertEquals(valPersister.get().intValue(), 121);
  }

}
