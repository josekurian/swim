// Copyright 2015-2020 Swim inc.
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

package swim.dynamic.observable.function;

import swim.dynamic.Bridge;
import swim.dynamic.BridgeGuest;
import swim.observable.function.DidRemoveKey;

public class GuestDidRemoveKey<K, V> extends BridgeGuest implements DidRemoveKey<K, V> {

  public GuestDidRemoveKey(Bridge bridge, Object guest) {
    super(bridge, guest);
  }

  @Override
  public void didRemove(K key, V oldValue) {
    this.bridge.guestExecuteVoid(this.guest, key, oldValue);
  }

}
