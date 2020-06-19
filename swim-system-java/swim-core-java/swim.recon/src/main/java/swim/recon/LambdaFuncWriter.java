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

package swim.recon;

import swim.codec.Output;
import swim.codec.Writer;
import swim.codec.WriterException;

final class LambdaFuncWriter<I, V> extends Writer<Object, Object> {

  final ReconWriter<I, V> recon;
  final V bindings;
  final V template;
  final Writer<?, ?> part;
  final int step;

  LambdaFuncWriter(ReconWriter<I, V> recon, V bindings, V template, Writer<?, ?> part, int step) {
    this.recon = recon;
    this.bindings = bindings;
    this.template = template;
    this.part = part;
    this.step = step;
  }

  static <I, V> int sizeOf(ReconWriter<I, V> recon, V bindings, V template) {
    int size = 0;
    size += recon.sizeOfPrimary(bindings);
    size += 4; // " => "
    size += recon.sizeOfValue(template);
    return size;
  }

  static <I, V> Writer<Object, Object> write(Output<?> output, ReconWriter<I, V> recon,
                                             V bindings, V template, Writer<?, ?> part, int step) {
    if (step == 1) {
      if (part == null) {
        part = recon.writePrimary(bindings, output);
      } else {
        part = part.pull(output);
      }
      if (part.isDone()) {
        part = null;
        step = 2;
      } else if (part.isError()) {
        return part.asError();
      }
    }
    if (step == 2 && output.isCont()) {
      output = output.write(' ');
      step = 3;
    }
    if (step == 3 && output.isCont()) {
      output = output.write('=');
      step = 4;
    }
    if (step == 4 && output.isCont()) {
      output = output.write('>');
      step = 5;
    }
    if (step == 5 && output.isCont()) {
      output = output.write(' ');
      step = 6;
    }
    if (step == 6) {
      if (part == null) {
        part = recon.writeValue(template, output);
      } else {
        part = part.pull(output);
      }
      if (part.isDone()) {
        return done();
      } else if (part.isError()) {
        return part.asError();
      }
    }
    if (output.isDone()) {
      return error(new WriterException("truncated"));
    } else if (output.isError()) {
      return error(output.trap());
    }
    return new LambdaFuncWriter<I, V>(recon, bindings, template, part, step);
  }

  static <I, V> Writer<Object, Object> write(Output<?> output, ReconWriter<I, V> recon,
                                             V bindings, V template) {
    return write(output, recon, bindings, template, null, 1);
  }

  @Override
  public Writer<Object, Object> pull(Output<?> output) {
    return write(output, this.recon, this.bindings, this.template, this.part, this.step);
  }

}
