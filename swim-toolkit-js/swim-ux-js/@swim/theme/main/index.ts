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

export * from "./look";

export * from "./feel";

export * from "./mood";

export * from "./theme";

export * from "./looks";

export * from "./feels";

export * from "./moods";

export * from "./themed";

export * from "./manager";

export * from "./themes";

import {ThemeService} from "./manager/ThemeService";
declare module "@swim/view" {
  abstract class View {
    get themeService(): ThemeService<this>; // defined by ThemeService
  }
}