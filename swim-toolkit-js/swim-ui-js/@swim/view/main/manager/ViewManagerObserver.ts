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

import {View} from "../View";
import {ViewManager} from "./ViewManager";

export interface ViewManagerObserver<V extends View = View, VM extends ViewManager<V> = ViewManager<V>> {
  viewManagerWillAttach?(viewManager: VM): void;

  viewanagerDidAttach?(viewManager: VM): void;

  viewManagerWillDetach?(viewManager: VM): void;

  viewManagerDidDetach?(viewManager: VM): void;

  viewManagerWillInsertRootView?(rootView: V, viewManager: VM): void;

  viewManagerDidInsertRootView?(rootView: V, viewManager: VM): void;

  viewManagerWillRemoveRootView?(rootView: V, viewManager: VM): void;

  viewManagerDidRemoveRootView?(rootView: V, viewManager: VM): void;
}
