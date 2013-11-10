require 'benchmark'
require 'oj'

require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)
hsh = Oj.compat_load(File.read(filename))

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("oj mri generate: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { Oj.dump(hsh) }
  end
end
